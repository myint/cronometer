/*
 * Created on Apr 2, 2005 by davidson
 */
package ca.spaz.cron.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.exercise.*;
import ca.spaz.cron.foods.*;
import ca.spaz.cron.metrics.BiomarkerPanel;
import ca.spaz.cron.metrics.BiomarkerPanelOld;
import ca.spaz.cron.notes.NoteEditor;
import ca.spaz.cron.summary.NutritionSummaryPanel;
import ca.spaz.cron.user.*;
import ca.spaz.gui.DateChooser;
import ca.spaz.gui.TranslucentToolBar;
import ca.spaz.util.ImageFactory;
import ca.spaz.util.ToolBox;

/**
 * Shows all data for a particular date
 * 
 * @todo: Calendar widget to skip to ANY date
 * 
 * @author davidson
 */
public class DailySummary extends JPanel implements UserChangeListener { 

   private static final long ONE_DAY = 1000 * 60 * 60 * 24;

   private BiomarkerPanel bioMarkerPanel;
   private BiomarkerPanelOld bioMarkerPanelOld;
   private ExercisePanel exercisePanel;
   private NoteEditor noteEditor;

   private Date curDate = new Date(System.currentTimeMillis());
 
   private ServingTable servingTable;
   private ExerciseTable exerciseTable;
   private JTabbedPane dailyTracker;

   private DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);

   private JSplitPane dietPanel;
 
   private JButton nextButton; 
   private JButton prevButton;
   private JButton dateTitle;
   private JButton copyPrevDayButton;
   private JButton todayButton;
   private JButton prefsButton;
   
   private TranslucentToolBar toolBar;
   private NutritionSummaryPanel totals;
   boolean asked = false; 
   
   public DailySummary() { 
      setPreferredSize(new Dimension(580,640));
      initialize();
      setDate(curDate, false);
      UserManager.getUserManager().addUserChangeListener(this);
      notifyObservers();
   }

   public void addServingToUser(Serving c, User user, Date date) {
      if (isOkToAddServings(date, true) && user != null) {
         Serving copy = new Serving(c);
         copy.setDate(date);
         user.getFoodHistory().addServing(copy);
         notifyObservers(); 
      } else {
         CRONOMETER.okDialog("No servings copied", "Warning");
      }
   }
   
   public void addServingToUser(Serving c, User user) {
      addServingToUser(c, user, curDate);
   }

   public void addServing(Serving c) {
      addServingToUser(c, UserManager.getCurrentUser());
   }
   
   public void addExerciseToUser(Exercise e, User user, Date date) {
      if (isOkToAddServings(date, false) && user != null) {
         Exercise copy = new Exercise(e);
         copy.setDate(date);
         user.getExerciseHistory().addExercise(copy);
         notifyObservers(); 
      } else {
         CRONOMETER.okDialog("No servings copied", "Warning");
      }
   }
   
   public void addExerciseToUser(Exercise e, User user) {
      addExerciseToUser(e, user, curDate);
   }
   
   public void addExercise(Exercise e) {
      addExerciseToUser(e, UserManager.getCurrentUser());
   }

   public boolean isOkToAddServings(Date date, boolean food) {
      String typeString = "food";
      
      if(!food)
      {
         typeString = "exercise";
      }
      
      Date now = new Date(System.currentTimeMillis());
      if (!ToolBox.isSameDay(date, now) && !asked) {        
         int choice = JOptionPane.showConfirmDialog(this, 
               "You are adding a " + typeString + " to a date in the past or future.\n" +
               "Are you sure you want to do this?",
               "Add " + typeString + "?", JOptionPane.YES_NO_OPTION);
         if (choice != JOptionPane.YES_OPTION) {
            return false;
         }
      }
      asked = true;
      return true;
   } 
   
   /**
    * Prompt the user for a specific calendar date and set the panel to that
    * current date.
    */
   public void chooseDate() {
      pickDate();
   }

   public Date getDate() {
      return curDate;
   }

   private BiomarkerPanelOld  getBioMarkersPanel() {
      if (null == bioMarkerPanelOld) {
         bioMarkerPanelOld = new BiomarkerPanelOld();
      }
      return bioMarkerPanelOld;
   }

   private BiomarkerPanel  getBioMarkersPanel2() {
      if (null == bioMarkerPanel) {
         bioMarkerPanel = new BiomarkerPanel();
      }
      return bioMarkerPanel;
   }

   private ExercisePanel getExercisePanel() {
      if (null == exercisePanel) {
         exercisePanel = new ExercisePanel();
      }
      return exercisePanel;
   }

   private JTabbedPane getDailyTrackerPanel() {
      if (null == dailyTracker) {
         dailyTracker = new JTabbedPane();
         dailyTracker.addTab("Diet", new ImageIcon(ImageFactory.getInstance().loadImage("/img/apple-16x16.png")), getDietPanel()); 
         dailyTracker.addTab("Biomarkers", new ImageIcon(ImageFactory.getInstance().loadImage("/img/graph.gif")), getBioMarkersPanel());
         dailyTracker.addTab("Exercise", new ImageIcon(ImageFactory.getInstance().loadImage("/img/runner.gif")), getExercisePanel());
         dailyTracker.addTab("Notes", new ImageIcon(ImageFactory.getInstance().loadImage("/img/toc_open.gif")), getNotesEditor());
      }
      return dailyTracker;
   }

   public NoteEditor getNotesEditor() {
      if (noteEditor == null) {
         noteEditor = new NoteEditor();
      }
      return noteEditor;
   }

   public JSplitPane getDietPanel() {
      if (null == dietPanel) {
         dietPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
               getServingTable(), getNutritionSummaryPanel());
         dietPanel.setDividerLocation(300);
         dietPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3)); 
      }
      return dietPanel;
   }
   
   public ServingTable getServingTable() {
      if (null == servingTable) {
         servingTable = ServingTable.getServingTable();
         
         servingTable.addServingSelectionListener(new ServingSelectionListener() {
            public void servingSelected(Serving s) { }
            public void servingDoubleClicked(Serving s) {
               FoodEditor.editFood(s);
            }
            public void servingChosen(Serving s) {
               if (isOkToAddServings(curDate, true)) {
                  addServing(s);           
               }
            }
         });
         
         servingTable.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
               List servings = servingTable.getSelectedServings();
               boolean allSelected = false;
               if (servings.size() == 0) {
                  servings = servingTable.getServings();
                  allSelected = true;
               }
               getNutritionSummaryPanel().setServings(servings, allSelected);               
            }           
         });               
      }
      return servingTable;
   }

   public ExerciseTable getExerciseTable() {
      if (null == exerciseTable) {
         exerciseTable = ExerciseTable.getExerciseTable();
         
         exerciseTable.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
               List exercises = exerciseTable.getExercises();
               getNutritionSummaryPanel().setExercises(exercises);               
            }           
         });
      }
      
      return exerciseTable;
   }

   private JButton getPrefsButton() {
      if (null == prefsButton) {
         ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/task.gif"));
         prefsButton = new JButton(icon);         
         CRONOMETER.fixButton(prefsButton);    
         prefsButton.setFocusable(false); 
         prefsButton.setToolTipText("Edit Nutritional Targets");
         prefsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               CRONOMETER.getInstance().doEditUserSettings();
            }
         });
      }
      return prefsButton;
   }
   
   private JButton helpButton;
   
   private JButton getHelpButton() {
      if (null == helpButton) {
         helpButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/help.gif")));
         helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               CRONOMETER.getInstance().doHelp();
            }  
         }); 
         CRONOMETER.fixButton(helpButton); 
         helpButton.setFocusable(false); 
         helpButton.setToolTipText("Help Browser");          
      }
      return helpButton;
   }
     
   private JButton getNextButton() {
      if (null == nextButton) {
         nextButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/forth.gif")));
         nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               setDate(new Date(curDate.getTime() + ONE_DAY), false);
            }  
         }); 
         CRONOMETER.fixButton(nextButton); 
         nextButton.setFocusable(false); 
         nextButton.setToolTipText("Next Day");          
      }
      return nextButton;
   }

   private JButton getPreviousButton() {
      if (null == prevButton) {
         prevButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/back.gif")));
         prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               setDate(new Date(curDate.getTime() - ONE_DAY), false);
            }  
         }); 
         CRONOMETER.fixButton(prevButton);
         prevButton.setToolTipText("Previous Day");         
         prevButton.setFocusable(false); 
      }
      return prevButton;
   }

   private JButton getCopyPreviousDayButton() {
      if (null == copyPrevDayButton) {
         copyPrevDayButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/copy.gif")));
         copyPrevDayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               copyPreviousDay();
            }
         });
         CRONOMETER.fixButton(copyPrevDayButton);
         copyPrevDayButton.setToolTipText("Copy Previous Day");
         copyPrevDayButton.setFocusable(false);
      }
      return copyPrevDayButton;
   }   
   
   private JButton getTodayButton() {
      if (null == todayButton) {
         todayButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/trace.gif")));
         todayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               goToToday();
            }
         });
         CRONOMETER.fixButton(todayButton);
         todayButton.setToolTipText("Go To Today");
         todayButton.setFocusable(false);
      }
      return todayButton;
   }   

   /**
    * Set the current date to today
    */
   public void goToToday() {
      setDate(new Date(System.currentTimeMillis()), false);
   }
   
   /**
    * Copies the foods from the previous day into this day.
    */
   private void copyPreviousDay() {
//      if (isOkToAddServings(curDate)) {
	   Date previousDay = new Date(curDate.getTime() - ONE_DAY);
      UserManager.getCurrentUser().getFoodHistory().copyConsumedOn(previousDay, curDate);
	   notifyObservers();
//      }
   }

   /**
    * @return the title label for this component
    */
   private JButton getDateTitle() {
      if (null == dateTitle) {
         
         dateTitle = new JButton(df.format(curDate));
         dateTitle.setFont(new Font("Application", Font.BOLD, 16)); 
         dateTitle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               chooseDate();
            }
         });
         CRONOMETER.fixButton(dateTitle); 
         dateTitle.setFocusable(false); 
      }
      return dateTitle;
   }
   

   private JComponent getToolbar() {
      if (null == toolBar) {
         toolBar = new TranslucentToolBar(0.25);
         toolBar.setBackground(Color.BLACK); 
         toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
         toolBar.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
         toolBar.add(getHelpButton());
         toolBar.add(Box.createHorizontalStrut(5));
         toolBar.add(getTodayButton());         
         toolBar.add(Box.createHorizontalGlue());
         toolBar.add(getPreviousButton());
         toolBar.add(Box.createHorizontalStrut(5));
         toolBar.add(getDateTitle());
         toolBar.add(Box.createHorizontalStrut(5));
         toolBar.add(getNextButton());
         toolBar.add(Box.createHorizontalGlue());
         toolBar.add(getCopyPreviousDayButton());  
         toolBar.add(Box.createHorizontalStrut(5));
         toolBar.add(getPrefsButton()); 
      }
      return toolBar;
   }
 
   public NutritionSummaryPanel getNutritionSummaryPanel() {
      if (null == totals) {
         totals = new NutritionSummaryPanel();
      }
      return totals;
   }

   private void initialize() {
      setLayout(new BorderLayout(4, 4));
      setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 3));
      add(getToolbar(), BorderLayout.NORTH);
      add(getDailyTrackerPanel(), BorderLayout.CENTER);
   }

   public void notifyObservers() {     
      List consumed = UserManager.getCurrentUser().getFoodHistory().getConsumedOn(curDate);
      getServingTable().setServings(consumed);
      List exercises = UserManager.getCurrentUser().getExerciseHistory().getConsumedOn(curDate);
      getExerciseTable().setExercises(exercises);
   }
   
   public void userChanged(UserManager userMan) { 
      //getNotesEditor().clear();
      setDate(curDate, true);
   }

   /**
    * Set the current date being displayed by this daily summary
    */
   public void setDate(Date d, boolean userChanged) {
      curDate = d;      
      // getDateTitle().setDate(curDate);
      getDateTitle().setText(df.format(curDate));
      validate();
      getBioMarkersPanel().setDate(d);
      getServingTable().setTitle(df.format(curDate));
      if (!userChanged) {
         getNotesEditor().saveCurrentNote(); 
      }
      getNotesEditor().setDate(d);
      asked = false;
      refreshTime();
      notifyObservers();
   }

   public void pickDate() {      
      Date d = DateChooser.pickDate(this, curDate);
      if (d != null) {
         setDate(d, false);
      }
   }

   /**
    * Called periodically. 
    * 
    * Currently just checks if the date has changed, and updates the state of the today-button
    */
   public void refreshTime() {
      if (!ToolBox.isSameDay(curDate, new Date(System.currentTimeMillis()))) {
         if (asked && getTodayButton().isEnabled()) {
            asked = false;
         }
         getTodayButton().setEnabled(true);
      } else {
         getTodayButton().setEnabled(false);
      }
   }
     
}
