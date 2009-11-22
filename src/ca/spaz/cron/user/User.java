/*
 *******************************************************************************
 * Copyright (c) 2005 Chris Rose and AIMedia
 * All rights reserved. CRONUser and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Chris Rose
 *     Simon Werner
 *******************************************************************************/
package ca.spaz.cron.user;

import java.util.*;

import javax.swing.JComponent;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.exercise.ExerciseHistory;
import ca.spaz.cron.foods.FoodHistory;
import ca.spaz.cron.foods.NutrientInfo;
import ca.spaz.cron.metrics.*;
import ca.spaz.cron.notes.NotesHistory;
import ca.spaz.cron.targets.*;
import ca.spaz.util.Settings;

/**
 * A CRONOMETER-specific, property-based <code>User</code> implementation. 
 * This contains all the necessary code for data relating to a 
 * single user in CRONOMETER.
 * 
 * @author Chris Rose
 * @author Simon Werner
 */
public class User {
   
   public static final String CU_FIRST_RUN = "first.run";
   public static final String NORMAL_FEMALE = "Normal";
   public static final String PREGNANT_FEMALE = "Pregnant";
   public static final String LACTATING_FEMALE = "Lactating";
   public static final String DEFAULT_USERNAME = "Default User";
   
   private static final String CU_PREF_BASE = "pref.";
   private static final String CU_HEIGHT = "height";
   private static final String CU_WEIGHT = "weight"; 
   private static final String CU_CUSTOM_TARGETS = "custom.targets";
   private static final String CU_MALE = "male";
   private static final String CU_BD_DAY = "birthdate.day";   
   private static final String CU_BD_MONTH = "birthdate.month";
   private static final String CU_BD_YEAR = "birthdate.year";
   private static final String CU_TARGET = "target.";
   private static final String CU_TRACK = "track.";
   private static final String CU_STATUS = "female.status";
   private static final String CU_ACTIVITY = "activity.level";
   private static final String CU_HEIGHT_UNIT_METRIC = "height.unit.cm";
   private static final String CU_WEIGHT_UNIT_METRIC = "weight.unit.kg";
   private static final String CU_PROTEIN_PERC = "protein.perc";
   private static final String CU_CARBS_PERC = "carb.perc";
   private static final String CU_FAT_PERC = "fat.perc";

   private FoodHistory foodHist;
   private NotesHistory noteHist;
   private BiometricsHistory bioHist;
   private BiomarkerDefinitions bioDefs;
   private ExerciseHistory exerciseHist;
   
   private String username;
   private Date birthDate;
   private Settings settings;

   /**
    * Constructor.
    * @param settings the settings that will be used for this user
    */
   public User(Settings settings) {
      this.settings = settings;
   }
   
   public void setFemaleStatus(String status) {
      settings.set(CU_STATUS, status);
      UserManager.getUserManager().notifyUserChangeListeners();
   }

   public Date getBirthDate() {
      if (null == birthDate) {
         Calendar cal = Calendar.getInstance();
         cal.set(Calendar.YEAR, settings.getInt(CU_BD_YEAR, 1944));
         cal.set(Calendar.MONTH, settings.getInt(CU_BD_MONTH, 06));
         cal.set(Calendar.DAY_OF_MONTH, settings.getInt(CU_BD_DAY, 06));
         birthDate = new Date(cal.getTimeInMillis());
      }
      return birthDate;
   }

   public void setBirthDate(Date date) {
      if (null != date) {
         Calendar cal = Calendar.getInstance();
         cal.setTime(date);
         settings.set(CU_BD_YEAR, "" + cal.get(Calendar.YEAR));
         settings.set(CU_BD_MONTH, "" + cal.get(Calendar.MONTH));
         settings.set(CU_BD_DAY, "" + cal.get(Calendar.DAY_OF_MONTH));
      }
      birthDate = date;
      UserManager.getUserManager().notifyUserChangeListeners();
   }
   
   /**
    * A rough estimate of the user's age, close enough for our purposes.
    */
   public int getAge() {
      if (null != getBirthDate()) {
         return getAge(getBirthDate());
      }
      return 0;
   }

   /**
    * A rough estimate of the user's age, close enough for our purposes.
    */
   public static int getAge(Date date) {
      Calendar bdcal = Calendar.getInstance();
      bdcal.setTime(date);
      Calendar cal = Calendar.getInstance();
      long age = (cal.getTimeInMillis() - bdcal.getTimeInMillis());
      long ONE_YEAR = (long)((long)365*(long)24*(long)60*(long)60*(long)1000);
      age = age / ONE_YEAR;
      return (int)age;
   }

   public void setWeightUnitMetric(boolean val) {
      settings.set(CU_WEIGHT_UNIT_METRIC, val);       
   }
   
   public boolean getWeightUnitMetric() {
      return settings.getBoolean(CU_WEIGHT_UNIT_METRIC, true);
   }
   
   public void setHeightUnitMetric(boolean val) {
      settings.set(CU_HEIGHT_UNIT_METRIC, val);       
   }
   
   public boolean getHeightUnitMetric() {
      return settings.getBoolean(CU_HEIGHT_UNIT_METRIC, true);
   }

   public double getHeightInCM() {     
      return settings.getDouble(CU_HEIGHT, 170);
   }

   public void setHeightInCM(double height) { 
      settings.set(CU_HEIGHT, height);
      UserManager.getUserManager().notifyUserChangeListeners();
   }

   public String getUserPreference(String prefName, String def) {
      return settings.get(CU_PREF_BASE + prefName, def);
   }

   public void setUserPreference(String prefName, String value) {
      settings.set(CU_PREF_BASE + prefName, value);
      UserManager.getUserManager().notifyUserChangeListeners();      
   }
   
   public void setTarget(NutrientInfo nutrient, Target target) {
      settings.set(CU_TARGET + nutrient.getName()+".min", target.getMin());
      settings.set(CU_TARGET + nutrient.getName()+".max", target.getMax());
      UserManager.getUserManager().notifyUserChangeListeners();
   }

   public Target getTarget(NutrientInfo nutrient) {
      if (nutrient == null) return null;
      double min = settings.getDouble(CU_TARGET+nutrient.getName()+".min", 0);
      double max = settings.getDouble(CU_TARGET+nutrient.getName()+".max", 0);
      return new Target(min,max);
   }

   public boolean isCustomTargets(NutrientInfo ni) {
      return settings.getBoolean(CU_PREF_BASE + CU_CUSTOM_TARGETS, false);
   }

   public void setCustomTargets(boolean value) {
      settings.set(CU_PREF_BASE + CU_CUSTOM_TARGETS, value);
      UserManager.getUserManager().notifyUserChangeListeners();
   }

   public boolean isTracking(NutrientInfo ni) {
      return settings.getBoolean(CU_TRACK+ni.getName(), ni.getDefaultTracking());
   }
   
   public void setTracking(NutrientInfo ni, boolean b) {
      settings.set(CU_TRACK+ni.getName(), b);
      UserManager.getUserManager().notifyUserChangeListeners();
   }

   public boolean isMale() {
      return settings.getBoolean(CU_MALE, true);
   }

   public boolean isFemale() {     
      return !isMale();
   }

   public boolean isPregnant() {      
      return isFemale() && getFemaleStatus().equals(PREGNANT_FEMALE);
   }

   public boolean isLactating() {      
      return isFemale() && getFemaleStatus().equals(LACTATING_FEMALE);
   }

   public String getFemaleStatus() {
      return settings.get(CU_STATUS, NORMAL_FEMALE);   
   }
   
   public void setGender(boolean male) {
      settings.set(CU_MALE, male);
      UserManager.getUserManager().notifyUserChangeListeners();
   }

   public boolean firstRun() {
      return settings.getBoolean(CU_FIRST_RUN, true);
   }
   
   public void setFirstRun(boolean val) {
      settings.set(CU_FIRST_RUN, val);
      UserManager.getUserManager().notifyUserChangeListeners();
   }

   public double getWeightInKilograms() {
      return settings.getDouble(CU_WEIGHT, 150/2.2); 
   }

   public double getBMI() {
      double meters = getHeightInCM()/100.0;
      return getWeightInKilograms() / (meters*meters);
   }

   public int getActivityLevel() { 
      return settings.getInt(CU_ACTIVITY, 0);
   }

   public void setActivityLevel(int act) {
      settings.set(CU_ACTIVITY, act);         
   }
   
   public void setWeightInKilograms(double userWeight) {
      settings.set(CU_WEIGHT, userWeight);       
   }

   public int getProteinPercentage() { 
      return settings.getInt(CU_PROTEIN_PERC, 30); 
   }

   public int getCarbPercentage() { 
      return settings.getInt(CU_CARBS_PERC, 40); 
   }

   public int getFatPercentage() { 
      return settings.getInt(CU_FAT_PERC, 30); 
   }
   
   public void setProteinPercentage(int val) { 
      settings.set(CU_PROTEIN_PERC, val); 
   }
   
   public void setCarbPercentage(int val) { 
      settings.set(CU_CARBS_PERC, val); 
   }
   
   public void setFatPercentage(int val) { 
       settings.set(CU_FAT_PERC, val); 
   }   
   
   public static String removeCharAt(String s, int pos) {
      return s.substring(0,pos)+s.substring(pos+1);
   }
   
   public List<NutrientInfo> getTracked(List<NutrientInfo> list) {
      List<NutrientInfo> tracked = new ArrayList<NutrientInfo>();
      for (int i=0; i<list.size(); i++) {
         NutrientInfo ni = (NutrientInfo)list.get(i);
         if (isTracking(ni)) {
            tracked.add(ni);
         }
      }
      return tracked;
   }

   
   /**
    * Returns <code>true</code> when a character is a valid character for a username. 
    */
   public static boolean isValidChar(char c) {
      if ('0' <= c && c <= '9') {
         return true;
      } else if ('A' <= c && c <= 'Z') {
         return true;
      } else if ('a' <= c && c <= 'z') {
         return true;
      } else if (192 <= c && c <= 255) {
         return true;
      } else if (c == '.' || c == '-' || c == '_' || c == ' ') {
         return true;
      }
      return false;
   }
   
   /**
    * Clean the username.  The resulting username will only have the following 
    * characters: [A-Za-z0-9.-_]. 
    * @param username the string to clean
    * @return the input string minus any invalid characters 
    */
   public static String cleanUsername(String s) {
      int i = 0;
      s = s.trim();
      while (i < s.length()) {
         if (!isValidChar(s.charAt(i))) {
            s = removeCharAt(s, i);
         } else {
            i++;
         }
      }
      return s;
   }
   
   /**
    * Rename the current user to the given name.  Need to rename the directory as well.
    * @param username the new name of the user
    */
   public void setUsername(String newUsername) {
      
      String newCleanUsername = cleanUsername(newUsername); 
      
      if ( ! newUsername.equals(newCleanUsername)) {
         CRONOMETER.okDialog("Your username contains invalid characters, " +
               "these have been removed.", "User name updated");
      }
      
      if (newCleanUsername.equals(username)) {
         // given username is the same as current, therefore username has not changed
         return;
      }
      
      if (username == null) {
         username = newCleanUsername;
      } else {
         // Update the directory name where the usernames are stored
         if (UserManager.renameUserDirectory(this, newCleanUsername)) {
            // The rename of the directory PASSED, we can change the name
            username = newCleanUsername;
         } else {
            // An error occurred while renaming the directory, inform the user
            CRONOMETER.okDialog("An error occurred while changing your username. " + 
                  "Your orignal username will be used.", "User name updated");
         }
      }
      UserManager.getUserManager().notifyUserChangeListeners();
   }
   
   public String getUsername(){
      if (username == null) {
         // Loop until we find a username that is valid.
         username = DEFAULT_USERNAME;
         int i = 2;
         while (UserManager.getUserManager().getUser(username) != null) {
            username = DEFAULT_USERNAME + " " + (new Integer(i++)).toString();
         }
         return username;
      } else {
         return username;
      }
   }

   /**
    * The first time a user is initialised we need to do some things, such as setting
    * the user targets.
    * @param parentWindow
    */
   public void doFirstRun(JComponent parentWindow) {
      this.setFirstRun(false);
      UserSettingsDialog.showDialog(UserManager.getUserManager(), parentWindow);
      TargetEditor.setDefaultTargets(new DRITargetModel(), this);
      TargetEditor.editTargets();
   }

   public void clearSettings() {
      settings.clearAll();
   }

   public Settings getSettings() {
      return settings;
   }
   
   /**
    * Initialise the User specific data sources.
    */
   public void initUserData() {
      getFoodHistory();
      getNotesHistory();
      getBiometricsHistory();
   }

   public BiometricsHistory getBiometricsHistory() {
      if (bioHist == null) {
         bioHist = new BiometricsHistory();
      }
      return bioHist;
   }

   public BiomarkerDefinitions getBiomarkerDefinitions() {
      if (bioDefs == null) {
         bioDefs = new BiomarkerDefinitions();         
      }
      return bioDefs;
   }
   
   public FoodHistory getFoodHistory() {
      if (foodHist == null) {
         foodHist = new FoodHistory();
      }
      return foodHist;
   }
   
   public ExerciseHistory getExerciseHistory() {
      if (exerciseHist == null) {
         exerciseHist = new ExerciseHistory();
      }
      return exerciseHist;
   }

   public NotesHistory getNotesHistory() {
      if (noteHist == null) {
         noteHist = new NotesHistory();
      }
      return noteHist;
   } 
   
   /**
    * Ensure all data is saved to backing stores.
    */
   public void saveUserData() {
      getFoodHistory().save();
      getBiometricsHistory().save();
      getNotesHistory().save();
      getExerciseHistory().save();
   }
   
   public String getNotes(Date date) {
      return getNotesHistory().getNote(date);
   }
   
   public void setNotes(String note, Date date) {
      getNotesHistory().setNote(note, date);
   }
   
   public List getBiometrics(Date date) {
      return getBiometricsHistory().getMetricsOn(date);
   }

   public void addMetric(Metric metric) {
      getBiometricsHistory().addMetric(metric);
   }
   
   public void updateMetric(Metric metric) {
      getBiometricsHistory().update(metric);
   }   

   public void removeMetric(Metric metric) {
      getBiometricsHistory().delete(metric);
   }
   
}
