/*
 * Created on 13-Jun-2005
 */
package ca.spaz.cron.user;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.Border;

import se.datadosen.component.RiverLayout;
import ca.spaz.gui.*;
import ca.spaz.util.ImageFactory;

import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;

public class UserSettingsDialog extends WrappedPanel {
   private static final String CM_UNITS = "Centimeters";
   private static final String INCHES_UNITS = "Inches";
   private static final String[] LENGTH_MEASURES = { CM_UNITS, INCHES_UNITS };
   private static final double CM_PER_INCH = 2.53999996;
   
   private static final String POUND_UNITS = "Pounds";
   private static final String KILOGRAM_UNITS = "Kilograms";
   private static final String[] WEIGHT_MEASURES = { KILOGRAM_UNITS, POUND_UNITS };
   private static final double POUNDS_PER_KILO = 2.20462262;
   
   private static final String[] ACTIVITY_MEASURES = { 
      "Sedentary", "Low Active", "Active", "High Active"
   };

   private static final String[] STATUS = { 
      User.NORMAL_FEMALE, User.PREGNANT_FEMALE, User.LACTATING_FEMALE 
   };

   
   private JPanel namePanel;
   private JPanel genderPanel;
   private JPanel birthPanel;
   private JPanel heightPanel;
   private JPanel weightPanel;
   private JPanel activityPanel;
   private JRadioButton male, female; 
   private JYearChooser yearSpinner;
   private JMonthChooser monthChooser;
   private JSpinner heightField;
   private JComboBox heightUnits;
   private JSpinner weightField;
   private JComboBox weightUnits; 
   private JComboBox activityUnits;
   private JComboBox status;
   private JLabel bmiLabel;
   private User user;
   
   public UserSettingsDialog(User user) {
      this.user = user;

      JPanel cp = new JPanel(new RiverLayout(1,1)); 
      cp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      cp.add("p center hfill", getGenderPanel());
      cp.add("p center hfill", getBirthPanel());
      cp.add("p center hfill", getHeightPanel());
      cp.add("p center hfill", getWeightPanel());
      cp.add("p center hfill", getActivityPanel());
      cp.add("p center vfill", Box.createVerticalBox());

      setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
      setLayout(new BorderLayout(12,12));
        
      add(cp, BorderLayout.CENTER); 
       
   }
   
   public String getTitle() { return "Body Profile"; }
   public String getSubtitle() { return "Body Profile"; }
   public String getInfoString() { 
      return  "<div align=\"justify\" width=\"180\"><br>" +
              "The following information is needed to define "
            + "your profile and to calculate appropriate "
            + "nutritional targets for your body type." 
            + "<br><br>"
            + "Targets default to the <i>Dietary Reference Intake</i> (DRI) "
            + "values. Alternatively, you may manually edit the target " 
            + "values to your custom perference."
            + "</div>";
   }

   public boolean showSidebar() { 
      return true;
   }
   
   public ImageIcon getIcon() {
      return new ImageIcon(ImageFactory.getInstance().loadImage("/img/apple-100x100.png"));
   }
   
   public static boolean showDialog(User user, JComponent parent) {
      UserSettingsDialog usd = new UserSettingsDialog(user);
      return WrapperDialog.showDialog(parent, usd);
   }

   private static Border makeTitle(String str) {
      return BorderFactory.createCompoundBorder(
         BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(), str), 
            BorderFactory.createEmptyBorder(2,26,2,26));
   }
 
   private JPanel getGenderPanel() {
      if (genderPanel == null) {
         ButtonGroup bg = new ButtonGroup();
         male = new JRadioButton("Male", user.isMale());
         female = new JRadioButton("Female", user.isFemale());
         female.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
               getFemaleStatus().setEnabled(female.isSelected());
            }            
         });
         
         bg.add(male);
         bg.add(female);         
         genderPanel = new JPanel(new GridLayout(1, 3, 4, 4));
         genderPanel.setBorder(makeTitle("Gender:"));
         genderPanel.add(male);
         genderPanel.add(female);
         genderPanel.add(getFemaleStatus());
      }
      return genderPanel;
   }
   
   private JPanel getBirthPanel() {
      if (birthPanel == null) {
         birthPanel = new JPanel(new GridLayout(1, 2, 4, 4)); 
         birthPanel.setBorder(makeTitle("Birthdate:"));
         birthPanel.add(getYearSpinner()); 
         birthPanel.add(getMonthChooser());          
      }
      return birthPanel;
   }
   
   public JYearChooser getYearSpinner() {
      if (yearSpinner == null) {              
         Calendar calendar = Calendar.getInstance();         
         yearSpinner = new JYearChooser();
         yearSpinner.setStartYear(calendar.get(Calendar.YEAR)-120);
         yearSpinner.setEndYear(calendar.get(Calendar.YEAR));
         calendar.setTime(user.getBirthDate());
         yearSpinner.setYear(calendar.get(Calendar.YEAR));
      }
      return yearSpinner;
   }
   
   public JMonthChooser getMonthChooser() {
      if (monthChooser == null) {     
         monthChooser = new JMonthChooser(false);   
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(user.getBirthDate());
         monthChooser.setMonth(calendar.get(Calendar.MONTH));
      }
      return monthChooser;
   }

   private JPanel getHeightPanel() {
      if (heightPanel == null) {
         heightPanel = new JPanel(new GridLayout(1, 2, 4, 4));
         heightPanel.setBorder(makeTitle("Height:"));
         heightPanel.add(getHeightField());
         heightPanel.add(getHeightUnits());
      }
      return heightPanel;
   }
   
   private JSpinner getHeightField() {
      if (heightField == null) {
         heightField = new JSpinner(new SpinnerNumberModel(0, 0, 300, 1));
         Double height = new Double(user.getHeightInCM());
         if (height != null) {
            heightField.setValue(height);
         }
      }
      return heightField;
   }   
   
   private JComboBox getHeightUnits() {
      if (heightUnits == null) {
         heightUnits = new JComboBox(LENGTH_MEASURES);  
         if (!user.getHeightUnitMetric()) {
            heightUnits.setSelectedItem(INCHES_UNITS);
            heightField.setValue(new Double(user.getHeightInCM() / CM_PER_INCH));
         }
      }
      return heightUnits;
   }   
       

   private JPanel getWeightPanel() {
      if (weightPanel == null) {
         weightPanel = new JPanel(new GridLayout(1, 2, 4, 4));
         weightPanel.setBorder(makeTitle("Weight:"));
         weightPanel.add(getWeightField());
         weightPanel.add(getWeightUnits());         
      }
      return weightPanel;
   }
   
   private JSpinner getWeightField() {
      if (weightField == null) {
         weightField = new JSpinner(new SpinnerNumberModel(150, 1, 1000, 1));
         Double weight = new Double(user.getWeightInKilograms());
         if (weight != null) {
            weightField.setValue(weight);
         }
      }
      return weightField;
   }   
   
   private JComboBox getWeightUnits() {
      if (weightUnits == null) {
         weightUnits = new JComboBox(WEIGHT_MEASURES);  
         if (!user.getWeightUnitMetric()) {
            weightUnits.setSelectedItem(POUND_UNITS);
            weightField.setValue(new Double(user.getWeightInKilograms() * POUNDS_PER_KILO));
         }
      }
      return weightUnits;
   }   
   
   private JLabel getBMILabel() {
      if (bmiLabel == null) {
         bmiLabel = new JLabel("BMI: " + Math.round(user.getBMI()*10.0)/10.0, JLabel.CENTER);         
      }
      return bmiLabel;
   }   
   
   private JPanel getActivityPanel() {
      if (activityPanel == null) {
         activityPanel = new JPanel(new GridLayout(1, 2, 4, 4));
         activityPanel.setBorder(makeTitle("Activity:")); 
         activityPanel.add(getActivityUnits());
         activityPanel.add(new JLabel(""));
         //activityPanel.add(getBMILabel());
      }
      return activityPanel;
   }
   

   private JComboBox getActivityUnits() {
      if (activityUnits == null) {
         activityUnits = new JComboBox(ACTIVITY_MEASURES);   
         activityUnits.setSelectedIndex(user.getActivityLevel());
      }
      return activityUnits;
   }   
   
   private JComboBox getFemaleStatus() {
      if (status == null) {
         status = new JComboBox(STATUS);  
         status.setEnabled(user.isFemale());
         status.setSelectedItem(user.getFemaleStatus());
      }
      return status;
   }   
       
   
   private Date getBirthDate() {       
      Calendar calendar = Calendar.getInstance(); 
      calendar.set(Calendar.YEAR, getYearSpinner().getYear());
      calendar.set(Calendar.MONTH, getMonthChooser().getMonth());
      return calendar.getTime();      
   }

   private double getUserHeight() {
      double height = ((Number)getHeightField().getValue()).intValue();
      if (getHeightUnits().getSelectedItem().equals(INCHES_UNITS)) {
         height = (height * CM_PER_INCH);
      }
      return height;
   }
   
   private double getUserWeight() {
      double weight = ((Number)getWeightField().getValue()).doubleValue();
      if (getWeightUnits().getSelectedItem().equals(POUND_UNITS)) {
         weight /= POUNDS_PER_KILO;
      }
      return weight;
   }
   
   public boolean isValid() { 
      if (getUserHeight() <= 10) {
         JOptionPane.showMessageDialog(this, "Please enter a valid height.", 
               "Error", JOptionPane.ERROR_MESSAGE);
         return false;
      }

      if (getUserWeight() <= 10) {
         JOptionPane.showMessageDialog(this, "Please enter a valid weight.", 
               "Error", JOptionPane.ERROR_MESSAGE);
         return false;
      }
      
      if (getBirthDate() == null) {
         JOptionPane.showMessageDialog(this, "Please enter a valid birth date.", 
               "Error", JOptionPane.ERROR_MESSAGE);
         return false;
      }
      
      return true;
   }

   public boolean isCancellable() {
      return true;
   }

   public void doCancel() {
       // nothing needed
   }

   private void setUser() {
      user.setGender(male.isSelected()); 
      user.setHeightInCM(getUserHeight());
      user.setWeightInKilograms(getUserWeight());
      user.setBirthDate(getBirthDate());      
      if (female.isSelected()) {
         user.setFemaleStatus((String)(getFemaleStatus().getSelectedItem()));
      }
      user.setHeightUnitMetric(getHeightUnits().getSelectedItem().equals(CM_UNITS));
      user.setWeightUnitMetric(getWeightUnits().getSelectedItem().equals(KILOGRAM_UNITS));
      user.setActivityLevel(getActivityUnits().getSelectedIndex());      
      System.out.println("AGE/DATE:" + getBirthDate() + " | " + user.getAge());
      System.out.println("BMI:" + user.getBMI());
   }
   
   public void doAccept() {
      setUser();
      try {
         user.saveUserProperties();
      } catch (Exception e) { 
         e.printStackTrace();
         ErrorReporter.showError(e, this); 
      }
   }
}