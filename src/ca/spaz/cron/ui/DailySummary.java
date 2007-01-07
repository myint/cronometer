/*
 * Created on Apr 2, 2005 by davidson
 */
package ca.spaz.cron.ui;

import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import ca.spaz.cron.datasource.Datasources;
import ca.spaz.cron.foods.Serving;
import ca.spaz.cron.summary.NutritionSummaryPanel;
import ca.spaz.gui.TranslucentPanel;
import ca.spaz.util.ImageFactory;
import ca.spaz.util.ToolBox;

/**
 * Shows all data for a particular date
 * 
 * @todo: Calendar widget to skip to ANY date
 * 
 * @author davidson
 */
public class DailySummary extends JPanel { 

   private static final long ONE_DAY = 1000 * 60 * 60 * 24;

   private BiomarkerPanel bioMarkerPanel;

   private Date curDate = new Date(System.currentTimeMillis());
 
   private ServingTable servingTable;
   private JComponent dailyTracker;

   private DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);

   private JPanel dietPanel;
 
   private JButton nextButton; 
   private JButton prevButton;
   private JButton titleLabel;
   private JButton copyPrevDayButton;
   private JButton todayButton;
   private JPanel toolBar;
   private NutritionSummaryPanel totals;
   boolean asked = false; 
   
   public DailySummary() { 
      setPreferredSize(new Dimension(450,600));
      initialize();
      setDate(curDate);
      notifyObservers();
   }

   public void addServing(Serving c) {
      if (isOkToAddServings()) {
         c.setDate(curDate);
         Datasources.getFoodHistory().addServing(c);
         notifyObservers(); 
      }
   }
   
   public boolean isOkToAddServings() {
      Date now = new Date(System.currentTimeMillis());
      if (!ToolBox.isSameDay(curDate, now) && !asked) {        
         int choice = JOptionPane.showConfirmDialog(this, 
               "You are adding a food to a date in the past or future.\nAre you sure you want to do this?",
               "Add food?", JOptionPane.YES_NO_OPTION);
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


   private JPanel getBioMarkersPanel() {
      if (null == bioMarkerPanel) {
         bioMarkerPanel = new BiomarkerPanel();
      }
      return bioMarkerPanel;
   }

   private JComponent getDailyTrackerPanel() {
      if (null == dailyTracker) {
         dailyTracker = new JTabbedPane();
         dailyTracker.add("Diet", getDietPanel()); 
         dailyTracker.add("Biomarkers", getBioMarkersPanel());
      }
      return dailyTracker;
   }

   private JPanel getDietPanel() {
      if (null == dietPanel) {
         dietPanel = new JPanel(new BorderLayout(4, 4));
         dietPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
         dietPanel.add(getServingTable(), BorderLayout.CENTER);
         dietPanel.add(getNutritionSummaryPanel(), BorderLayout.SOUTH);
      }
      return dietPanel;
   }
   
   public ServingTable getServingTable() {
      if (null == servingTable) {
         servingTable = new ServingTable();
         servingTable.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
               List servings = servingTable.getSelectedServings();
               if (servings.size() == 0) {
                  servings = servingTable.getServings();
               }
               totals.setServings(servings);               
            }           
         });               
      }
      return servingTable;
   }

     

   private JButton getNextButton() {
      if (null == nextButton) {
         nextButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/forth.gif")));
         nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               setDate(new Date(curDate.getTime() + ONE_DAY));
            }  
         }); 
         nextButton.setFocusable(false); 
         FoodDBToolBar.fixButton(nextButton); 
         nextButton.setToolTipText("Next Day");          
      }
      return nextButton;
   }
 

   private JButton getPreviousButton() {
      if (null == prevButton) {
         prevButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/back.gif")));
         prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               setDate(new Date(curDate.getTime() - ONE_DAY));
            }  
         }); 
         FoodDBToolBar.fixButton(prevButton);
         prevButton.setToolTipText("Previous Day");         
         prevButton.setFocusable(false); 
      }
      return prevButton;
   }

   private JButton getCopyPreviousDayButton() {
      if (null == copyPrevDayButton) {
         copyPrevDayButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/Copy24.gif")));
         copyPrevDayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               copyPreviousDay();
            }
         });
         FoodDBToolBar.fixButton(copyPrevDayButton);
         copyPrevDayButton.setToolTipText("Copy Previous Day");
         copyPrevDayButton.setFocusable(false);
      }
      return copyPrevDayButton;
   }   
   
   private JButton getTodayButton() {
      if (null == todayButton) {
         todayButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/Today24.gif")));
         todayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               goToToday();
            }
         });
         FoodDBToolBar.fixButton(todayButton);
         todayButton.setToolTipText("Go To Today");
         todayButton.setFocusable(false);
      }
      return todayButton;
   }   

   /**
    * Set the current date to today
    */
   public void goToToday() {
      setDate(new Date(System.currentTimeMillis()));
   }
   
   /**
    * Copies the foods from the previous day into this day.
    */
   private void copyPreviousDay() {
	   Date previousDay = new Date(curDate.getTime() - ONE_DAY);
	   Datasources.getFoodHistory().copyConsumedOn(previousDay, curDate);
	   notifyObservers();
   }

   /**
    * @return the title label for this component
    */
   private JButton getTitle() {
      if (null == titleLabel) {
         titleLabel = new JButton(df.format(curDate));
         titleLabel.setFont(new Font("Application", Font.BOLD, 16)); 
         titleLabel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               chooseDate();
            }
         });
         FoodDBToolBar.fixButton(titleLabel); 
      }
      return titleLabel;
   }
   

   private JComponent getToolbar() {
      if (null == toolBar) {
         toolBar = new TranslucentPanel(0.25);
         toolBar.setBackground(Color.BLACK); 
         toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
         toolBar.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
         toolBar.add(getTodayButton());         
         toolBar.add(Box.createHorizontalGlue());
         toolBar.add(getPreviousButton());
         toolBar.add(Box.createHorizontalStrut(5));
         toolBar.add(getTitle());
         toolBar.add(Box.createHorizontalStrut(5));
         toolBar.add(getNextButton());
         toolBar.add(Box.createHorizontalGlue());
         toolBar.add(getCopyPreviousDayButton());         
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
      getTitle().setText(df.format(curDate));
      List consumed = Datasources.getFoodHistory().getConsumedOn(curDate);
      getServingTable().setServings(consumed);
   }

   public void setDate(Date d) {
      curDate = d;
      bioMarkerPanel.setDate(d);
      asked = false;
      getTodayButton().setEnabled(!ToolBox.isSameDay(d, new Date(System.currentTimeMillis())));
      notifyObservers();
   }

   public void pickDate() {
      Date d = DateChooser.pickDate(this, curDate);
      if (d != null) {
         setDate(d);
      }
   }

 
     
}
