package ca.spaz.cron.ui;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.CompoundBorder;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.graphics.ReflectionRenderer;

import se.datadosen.component.RiverLayout;
import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.user.UserManager;
import ca.spaz.gui.TranslucentPanel;
import ca.spaz.task.*;
import ca.spaz.util.ToolBox;

public class SplashScreen extends JFrame implements TaskListener {

   private TaskBar taskBar;
   private JLabel splash, version;
   private JPanel mainPanel;
   private JCheckBox checkForUpdates;
   private Task task;
   
   public static void showSplashScreen(Task startupTasks) {
      SplashScreen scr = new SplashScreen(startupTasks);
      scr.start();
   }
   
   public SplashScreen(Task startupTasks) {
      
      JPanel mp = new JPanel(new BorderLayout());
      mp.setBorder(new CompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(15,15,15,15) ));
      mp.add(getMainPanel(), BorderLayout.CENTER);
      
      setUndecorated(true);
      setTitle(CRONOMETER.getFullTitle());
      getContentPane().setLayout(new BorderLayout(4,4));
      getContentPane().add(mp, BorderLayout.CENTER);
      pack();
      ToolBox.centerFrame(this);
      this.task = startupTasks;
   }
   
   public ImageIcon getIcon() {
      try {
         BufferedImage img = GraphicsUtilities.loadCompatibleImage(this.getClass().getResource("/img/apple-100x100.png"));
         ReflectionRenderer r = new ReflectionRenderer(0.5f, 0.25f, true);
         return  new ImageIcon(r.appendReflection(img));
      } catch (IOException e) {
         e.printStackTrace();
      }
      return new ImageIcon(); // empty on failure
   }
   
   public void start() {
      setVisible(true);
      getTaskBar().executeTask(task);
   }
   
   private JPanel getMainPanel() {
      if (mainPanel == null) {
         mainPanel = new TranslucentPanel(0.3);
         mainPanel.setBackground(Color.BLACK);
         mainPanel.setLayout(new RiverLayout());
         mainPanel.setBorder(BorderFactory.createEmptyBorder(15,40,15,40));      
         mainPanel.add("p center", getSplash());
         
         JXBusyLabel busy = new JXBusyLabel();
         mainPanel.add("br center", busy);
         busy.setBusy(true);

         //mainPanel.add("p hfill", getTaskBar());
         
         mainPanel.add("p center", getVersionLabel());
         if (System.getProperty("ca.spaz.mode", "application").equalsIgnoreCase("application")) {
            mainPanel.add("p center", getCheckForUpdatesBox());
         }
      }
      return mainPanel;
   }
   
   private TaskBar getTaskBar() {
      if (taskBar == null) {
         taskBar = new TaskBar();
         taskBar.setOpaque(false);
         taskBar.getAbortButton().setVisible(false);
         taskBar.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
         taskBar.addTaskListener(this);
      }
      return taskBar;
   }
   
   private JLabel getSplash() {
      if (splash == null) {
         splash = new JLabel(CRONOMETER.TITLE, getIcon(), JLabel.CENTER);
         splash.setVerticalTextPosition(JLabel.BOTTOM);
         splash.setHorizontalTextPosition(JLabel.CENTER);
         splash.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
         splash.setFont(splash.getFont().deriveFont(Font.BOLD, 24));
      }
      return splash;
   }
   

   private JLabel getVersionLabel() {
      if (version == null) {
         version = new JLabel(CRONOMETER.getFullTitle(), JLabel.CENTER);         
         version.setFont(version.getFont().deriveFont(Font.PLAIN, 10));
         version.setForeground(Color.DARK_GRAY);
      }
      return version;
   }

   public void taskStarted(Task t) {}

   public void taskFinished(Task t) {
      setVisible(false);
      dispose();
   }

   public void taskAborted(Task t) {
      setVisible(false);
      dispose();
   }

   /**
    * Adds a task listener to this object to receive events on
    * a task's progress.
    * 
    * @param tl a task listener 
    */
   public synchronized void addTaskListener(TaskListener tl) {
      getTaskBar().addTaskListener(tl);
   }

   /**
    * Remove a task listener from the TaskBar
    * 
    * @param tl a task listener 
    */
   public synchronized void removeTaskListener(TaskListener tl) {
      getTaskBar().removeTaskListener(tl);
   }
  
   
   private JCheckBox getCheckForUpdatesBox() {
      if (checkForUpdates == null) {
         checkForUpdates = new JCheckBox(
               "check website for updates", 
               UserManager.getUserManager().getCheckForUpdates());              
         checkForUpdates.setFocusable(false);
         checkForUpdates.setOpaque(false);
         checkForUpdates.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               UserManager.getUserManager().setCheckForUpdates(checkForUpdates.isSelected());
            }            
         });
      }
      return checkForUpdates;
   }
   
}
