package ca.spaz.cron;
 
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ca.spaz.cron.actions.CreateRecipeAction;
import ca.spaz.cron.datasource.Datasources;
import ca.spaz.cron.foods.Food;
import ca.spaz.cron.foods.Serving;
import ca.spaz.cron.targets.DRITargetModel;
import ca.spaz.cron.targets.TargetEditor;
import ca.spaz.cron.ui.*;
import ca.spaz.cron.user.User;
import ca.spaz.cron.user.UserSettingsDialog;
import ca.spaz.gui.*;
import ca.spaz.task.Task;
import ca.spaz.task.TaskListener;
import ca.spaz.util.*;

import com.apple.mrj.MRJAboutHandler;
import com.apple.mrj.MRJQuitHandler;

/**
 * The main app.  
 * 
 * TODO: Backup files, especially on startup-error.
 * 
 *   0.7.0 changelist
 *      - added edit menu and copy/paste shortcuts to food list
 *      - does not show pie-chart if no calories == 0
 *      - added copy last day button
 *      - fixes to new update dialog
 *      - Spiffed up nutrition report (lined up columns).
 *      - Made a couple dialogs resizeable.
 *      - fixed beep
 *      - fixed several bugs with save/cancel of foods and recipes
 *      - bugs stomped?

 *   0.7.0 TODOs
 *      - get new icon set?
 *      - better search ranking algorithm
 *      - bug stomp !
 *      - fix missing checkbox bug in ReportWindow
 *      - make sure mac version looks ok
 *      - try crazy dialog idea 
 *      
 * @author davidson
 */
public class CRONOMETER extends JFrame implements TaskListener, MRJQuitHandler, MRJAboutHandler, ClipboardOwner  {
   public static final String TITLE = "CRON-o-Meter";
   public static final String VERSION = "0.7";
   public static final int BUILD = 7;
   public static JFrame mainFrame = null; 

   private static Clipboard clipboard = new Clipboard ("CRON-o-Meter");
   
   private DBPanel dbp;

   private DailySummary ds;

   private SpazMenuBar menu;

   private JPanel mainPanel;    

   private static CRONOMETER instance;

   public static CRONOMETER getInstance() {
      if (null == instance) {
         instance = new CRONOMETER();         
      }
      return instance;
   }

   /**
    * Constructor
    */
   public CRONOMETER() {
      setupForMacOSX();
   }
   
   public static Clipboard getClipboard() {
      return clipboard;
   }

   public static Image getWindowIcon() {
      return ImageFactory.getInstance().loadImage("/img/icon.png");
   }
   
   private void initGUI() {
      try {
         setJMenuBar(getMenu());
         
         setIconImage(getWindowIcon());
         setTitle(getFullTitle());
         getContentPane().add(getMainPanel());
         setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
         addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
               doQuit();
            }
         });
         pack();
         ToolBox.centerFrame(this);
         setVisible(true);        
         mainFrame = this;
         if (User.getUser().firstRun()) {            
            doShowReadMe();
            User.getUser().setFirstRun(false);
            UserSettingsDialog.showDialog(User.getUser(), getMainPanel());
            TargetEditor.setDefaultTargets(new DRITargetModel(), User.getUser());
            doEditUserSettings(); 
         } 
         User.getUser().setLastBuild(CRONOMETER.BUILD);
         makeAutoSaveTimer();
      } catch (Exception e) {
         Logger.debug(e);
         ErrorReporter.showError(e, this); 
      }
   }

   private void makeAutoSaveTimer() {
      Timer t = new Timer(6000*5, new ActionListener(){
         public void actionPerformed(ActionEvent e) {
            Datasources.saveAll();
         }
      });
      t.setCoalesce(true);
      t.setRepeats(true);
      t.start();
   }

   private void doShowReadMe() {
      WrappedPanel wp = new WrappedPanel() {         
         public String getTitle() {
            return "Licence Agreement";
         }
         public String getSubtitle() {
            return "Licence Agreement";
         }
         public String getInfoString() {
            return null;
         }
         public ImageIcon getIcon() {
            return null;
         }
         public boolean showSidebar() { 
            return false;
         }
         public boolean isCancellable() {
            return true;
         }
         public void doCancel() {
            System.exit(1);
         }
         public void doAccept() { }        
      };
      wp.setLayout(new BorderLayout());
      WebViewer wv = new WebViewer(ToolBox.loadFile(new File("docs/readme.html")));
      wv.setPreferredSize(new Dimension(600,400));
      wp.add(wv, BorderLayout.CENTER);
      WrapperDialog.showDialog(this, wp);      
   }

   public void doHelp() {
      launchURL(CRONOMETER.getInstance(), "http://spaz.ca/cronometer/docs/");
   }

   public void doReportBug() {
      launchURL(CRONOMETER.getInstance(), "http://sourceforge.net/tracker/?atid=735995&group_id=136481&func=browse");
   }

   public void doRequestFeature() {
      launchURL(CRONOMETER.getInstance(), "http://sourceforge.net/tracker/?group_id=136481&atid=735998");
   }
    
   public static String getFullTitle() {
      return TITLE + " v" + VERSION;
   }

   private JMenuBar getMenu() {
      if (null == menu) {
         menu = new SpazMenuBar(getClass().getResourceAsStream("/menubar.xml"), this);
         if (ToolBox.isMacOSX()) {
            menu.remove(menu.getMenu(0)); // remove file menu on Mac OS X....
         }
         
         // insert SWING edit menu:
         JMenu mainMenu = new JMenu("Edit");
         mainMenu.setMnemonic(KeyEvent.VK_E);
         TransferActionListener actionListener = new TransferActionListener();

         JMenuItem menuItem = new JMenuItem("Cut");
         menuItem.setActionCommand((String)TransferHandler.getCutAction().
                  getValue(Action.NAME));
         menuItem.addActionListener(actionListener);
         menuItem.setAccelerator(
           KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
         menuItem.setMnemonic(KeyEvent.VK_T);
         mainMenu.add(menuItem);
         menuItem = new JMenuItem("Copy");
         menuItem.setActionCommand((String)TransferHandler.getCopyAction().
                  getValue(Action.NAME));
         menuItem.addActionListener(actionListener);
         menuItem.setAccelerator(
           KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
         menuItem.setMnemonic(KeyEvent.VK_C);
         mainMenu.add(menuItem);
         menuItem = new JMenuItem("Paste");
         menuItem.setActionCommand((String)TransferHandler.getPasteAction().
                  getValue(Action.NAME));
         menuItem.addActionListener(actionListener);
         menuItem.setAccelerator(
           KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
         menuItem.setMnemonic(KeyEvent.VK_P);
         mainMenu.add(menuItem);
         
         menu.add(mainMenu, 1);
         
      }
      return menu;
   }


   private JPanel getMainPanel() {
      if (null == mainPanel) {
         mainPanel = new JPanel(new BorderLayout(4, 4));
         mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      /*   JSplitPane jsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
               getDBPanel(), getDailySummary());
         jsplit.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));*/
         mainPanel.add(getDailySummary(), BorderLayout.CENTER);       
      }
      return mainPanel;
   }

   public DBPanel getDBPanel() {
      if (null == dbp) {
         dbp = new DBPanel();
      }
      return dbp;
   }

   public DailySummary getDailySummary() {
      if (null == ds) {
         ds = new DailySummary();
         ds.getServingTable().addServingSelectionListener(new ServingSelectionListener() {
            public void servingSelected(Serving s) {
               getDBPanel().getSearchPanel().deselect();
               getDBPanel().getServingEditor().setServing(new Serving(s));
               getDBPanel().getToolBar().setSelectedFood(s.getFoodProxy());
            }
            public void servingDoubleClicked(Serving s) {
               FoodEditor.editFood(s);
            }
         });
      }
      return ds;
   }

   public void doCreateNewFood() {
      FoodEditor.editFood(new Food());
      refreshDisplays();
   }

   public void doCreateNewRecipe() {
      List servings = getDailySummary().getServingTable().getSelectedServings();
      CreateRecipeAction.execute(servings);
      refreshDisplays();
   }
    

   public void doEditUserSettings() {
      TargetEditor.editTargets(); 
   }
   
   public void doImportFood() {
      getDBPanel().getToolBar().doImportFood();
      refreshDisplays();
   }

   public void doAbout() {
      AboutScreen.showAbout(this);
   }
   
   public void doQuit() {
      try {
         Datasources.closeAll();
         User.getUser().saveUserProperties();
      } catch (IOException e1) {
         e1.printStackTrace(); 
         ErrorReporter.showError(e1, this);
      }
      System.exit(0);
   }
    
    
   public void taskStarted(Task t) {
      // Nothing
   }

   public void taskFinished(Task t) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            initGUI();
         }
      });
   }

   public void taskAborted(Task t) {
      Logger.error("Startup tasks aborted unexpectedly.  Halting...");
      System.exit(1);
   }

   public void handleQuit() {
      doQuit();
   }
   
   public void doNutritionReport() {
      getDailySummary().getNutritionSummaryPanel().generateReport();
   }
   
   /**
    * Installs special apple event listeners for Mac OS X specific functions 
    */
   private void setupForMacOSX() {
      if (ToolBox.isMacOSX()) { 
         try {
            Class appleAppClass = Class.forName("com.apple.mrj.MRJApplicationUtils");
            
            Class quitClass = Class.forName("com.apple.mrj.MRJQuitHandler");           
            Class[] quitArgs = {quitClass};
            Method registerQuitMethod = appleAppClass.getDeclaredMethod("registerQuitHandler", quitArgs);
            if (registerQuitMethod != null) {
               Object[] args = {this};
               registerQuitMethod.invoke(appleAppClass, args);
            }
            
            Class aboutClass = Class.forName("com.apple.mrj.MRJAboutHandler");           
            Class[] aboutArgs = {aboutClass};
            Method registerAboutMethod = appleAppClass.getDeclaredMethod("registerAboutHandler", aboutArgs);
            if (registerAboutMethod != null) {
               Object[] args = {this};
               registerAboutMethod.invoke(appleAppClass, args);
            }
         } catch (Exception e) {
            Logger.debug(e);
            ErrorReporter.showError(e, this); 
         }         
      }
   }
   
   private static final class SplashScreenTask implements Task, ProgressListener {
      int prog = 0;

      public int getTaskProgress() {
         return prog;
      }

      public void abortTask() { }

      public boolean canAbortTask() {
         return false;
      }

      public String getTaskDescription() {
         return "Starting "+TITLE+"...";
      }

      public void run() {
         ToolBox.sleep(1000);
         Datasources.initialize(this);  
         if (User.getUser().getCheckForUpdates()) {
            Thread t = new Thread( new Runnable() {
               public void run() {
                  CRONOMETER.getInstance().doCheckForUpdates();
               }
            });
            t.start();
         }
      }
      

      public void progressStart() {
         prog = 0;
      }

      public void progressFinish() {
         prog = 100;
      }

      public void progress(int percent) {
         prog = percent;
      }
   }
   
   

   
   public static void launchURL(Component parent, String url) {
      try {
         BrowserLauncher.openURL(url);
         return;
      } catch (IOException e) {
         e.printStackTrace();
      } 
      try {
         if (ToolBox.isOlderWindows()) {
            Runtime.getRuntime().exec("command.com /e:4096 /c start \""+url+"\"");
         } else {
            Runtime.getRuntime().exec("start \""+url+"\"");
         }
         return;
      } catch (IOException e) {
         Logger.error(e);      
      }
      ErrorReporter.showError("Could not load URL:\n"+url, parent);
   }
   

   public void doCheckForUpdates() {      
      try {
         URL url = new URL("http://spaz.ca/cronometer/updates.xml");     
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();         
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document d = db.parse(url.openStream());
         Element e = d.getDocumentElement();
         int b = XMLNode.getInt(e, "build");
         if (b > BUILD) {
            final String www = e.getAttribute("url");
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
            	   int result = JOptionPane.showOptionDialog(CRONOMETER.getInstance(),
            			   "A new version of " + TITLE + " is available!",
                           "New Version",
                           JOptionPane.OK_CANCEL_OPTION,
                           JOptionPane.INFORMATION_MESSAGE,
                           null,
                           null,
                           null);
                  if (result == JOptionPane.OK_OPTION  && www != null) {
                     launchURL(CRONOMETER.mainFrame, www);
                  }                  
               }
            });
         }
      } catch (Exception ex) {
         Logger.error(ex);         
      }
   }

   public void handleAbout() {
      doAbout();
   }   

   public void lostOwnership(Clipboard clipboard, Transferable contents) {
      // unused interface
      // grey out paste menu?
   }

   /**
    * Some unspecified change has occurred in the underlying data, so refresh the current views.
    * For instance a food may have been edited and the search results and servings table need to
    * reflect the changes made.
    */
   public void refreshDisplays() {
      repaint();
      getDBPanel().getSearchPanel().doDBSearch();  
      CRONOMETER.getInstance().getDailySummary().notifyObservers();
   }


   public static void main(String[] args) {
      try {
         if (!ToolBox.isMacOSX()) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } else {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
         }
      } catch (Exception e) {
         Logger.error("main() - Error configuring UI delegate", e);
      }
      // The optional program argument sets the name of the subdirectory for user data.
      // This permits using a different subdirectory for development and testing 
      // (by setting the argument in your IDE's run configuration) than for
      // daily use of the application.
      if (args.length > 0) {
         User.setSubdirectory(args[0]);
      }
      
      final CRONOMETER cron = CRONOMETER.getInstance();
      SplashScreen scr = new SplashScreen(new SplashScreenTask());
      mainFrame = scr;
      scr.setIconImage(CRONOMETER.getWindowIcon());
      scr.addTaskListener(cron);
      scr.start();      
   }

}

