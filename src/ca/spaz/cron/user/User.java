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
 *******************************************************************************/
package ca.spaz.cron.user;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.JFrame;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.datasource.Datasources;
import ca.spaz.cron.foods.NutrientInfo;
import ca.spaz.cron.metrics.Metric;
import ca.spaz.cron.notes.NotesHistory;
import ca.spaz.cron.targets.Target;
import ca.spaz.gui.ErrorReporter;
import ca.spaz.util.*;

/**
 * A CRONOMETER-specific, property-based <code>User</code> implementation. 
 * 
 * @author Chris Rose
 */
public class User {
   
   private static final String CU_BASE = "cron.user.";
   private static final String CU_FIRST_RUN = CU_BASE + "first.run";
   private static final String CU_PREF_BASE = CU_BASE + "pref.";
   private static final String CU_HEIGHT = CU_BASE + "height";
   private static final String CU_WEIGHT = CU_BASE + "weight"; 
   private static final String CU_CUSTOM_TARGETS = CU_BASE + "custom.targets";
   private static final String CU_MALE = CU_BASE + "male";
   private static final String CU_BD_DAY = CU_BASE + "birthdate.day";   
   private static final String CU_BD_MONTH = CU_BASE + "birthdate.month";
   private static final String CU_BD_YEAR = CU_BASE + "birthdate.year";
   private static final String CU_TARGET = CU_BASE + "target.";
   private static final String CU_TRACK = CU_BASE + "track.";
   private static final String CU_STATUS = CU_BASE + "female.status";
   private static final String CU_ACTIVITY = CU_BASE + "activity.level";
   
   private static final String CU_PROTEIN_PERC = CU_BASE + "proten.perc";
   private static final String CU_CARBS_PERC = CU_BASE + "carb.perc";
   private static final String CU_FAT_PERC = CU_BASE + "fat.perc";
   
   private static final String CU_HEIGHT_UNIT_METRIC = "height.unit.cm";
   private static final String CU_WEIGHT_UNIT_METRIC = "weight.unit.kg";
   
   private static final String LAST_BUILD = "last.build";
   
   public static final String CHECK_FOR_UDAPTES = "check.for.updates";
   private static final String HIDE_WHEN_MINIMIZED = CU_BASE+"hide.when.minimized";
   private static final String USER_PROPERTIES_FILE = "user.settings";
   
   public static final String NORMAL_FEMALE = "Normal";
   public static final String PREGNANT_FEMALE = "Pregnant";
   public static final String LACTATING_FEMALE = "Lactating";
   
   private static final String MAIN_WINDOW = CU_BASE + "main.window";
   private static final String DIET_DIVIDER =  CU_BASE + "diet.divider";
   
   
   public static String subdirectory = "cronometer";
   
   private static User instance = null;
   
   private Settings settings;
   private List listeners; 
   private Date birthDate;
   
   public static final User getUser() {
      if (null == instance) {
         instance = new User();         
      }
      return instance;
   }
   
   private User() {
      settings = new Settings(getUserPropertiesFile());
   }
   
   
   public void saveUserProperties() throws IOException {
      settings.save();
   }
   
   /**
    * Sets the subdirectory to use for user data. 
    * @param aSubdirectory
    */
   public static void setSubdirectory(String aSubdirectory) {
      subdirectory = aSubdirectory;
   }
 
   /**
    * Gets the subdirectory being used for user data.
    */
   public static String getSubdirectory() {
      return subdirectory;
   } 
   
   public static File getUserDirectory() {
      File appDir = ToolBox.getUserAppDirectory(subdirectory);
      if (!appDir.exists()) {
         appDir.mkdirs();
         if (!appDir.exists()) {
            Logger.error("Unable to create user app prefs directory " + appDir);
         }
      }
      return appDir;
   }
   
   public File getUserPropertiesFile() {
      File userPropertyFile = new File(getUserDirectory(), USER_PROPERTIES_FILE);
      Logger.debug("Initializing user property file " + userPropertyFile.getAbsolutePath());
      if (!userPropertyFile.exists()) {
         Logger.debug("Creating user property file " + userPropertyFile.getAbsolutePath());
         try {
            if (userPropertyFile.createNewFile()) {
               // Nothing.  All is well.
            } else {
               Logger.error("Unable to create user property file");
            }
         } catch (IOException e) {
            Logger.error("getUserPropertiesFile()", e);
            ErrorReporter.showError(e, CRONOMETER.getInstance()); 
         }
      } else {
         // Nothing.  All is well.
      }
      return userPropertyFile;
   }
   
   public void setFemaleStatus(String status) {
      settings.set(CU_STATUS, status);
      notifyListeners();
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
      notifyListeners();
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
   
   public double getHeightInCM() {     
      return settings.getDouble(CU_HEIGHT, 170);
   }

   public void setHeightInCM(double height) { 
      settings.set(CU_HEIGHT, height);
      notifyListeners();
   }

   public String getUserPreference(String prefName, String def) {
      return settings.get(CU_PREF_BASE + prefName, def);
   }

   public void setUserPreference(String prefName, String value) {
      settings.set(CU_PREF_BASE + prefName, value);
      notifyListeners();      
   }


   public NotesHistory getNotesHistory() {
      return Datasources.getNotes();
   }   
   
   public String getNotes(Date date) {
      return Datasources.getNotes().getNote(date);
   }
   
   public void setNotes(String note, Date d) {
      Datasources.getNotes().setNote(note, d);
   }
   
   
   public List getBiometrics(Date date) {
      return Datasources.getBiometricsHistory().getMetricsOn(date);
   }

   public void addMetric(Metric metric) {
      Datasources.getBiometricsHistory().addMetric(metric);
   }
   
   public void updateMetric(Metric metric) {
      Datasources.getBiometricsHistory().update(metric);
   }   

   public void removeMetric(Metric metric) {
      Datasources.getBiometricsHistory().delete(metric);
   }

   public void setTarget(NutrientInfo nutrient, Target target) {
      settings.set(CU_TARGET+nutrient.getName()+".min", target.getMin());
      settings.set(CU_TARGET+nutrient.getName()+".max", target.getMax());
      notifyListeners();
   }

   public Target getTarget(NutrientInfo nutrient) {
      if (nutrient == null) return null;
      double min = settings.getDouble(CU_TARGET+nutrient.getName()+".min", 0);
      double max = settings.getDouble(CU_TARGET+nutrient.getName()+".max", 0);
      return new Target(min,max);
   }

   public boolean isCustomTargets(NutrientInfo ni) {
      return settings.getBoolean(CU_PREF_BASE+CU_CUSTOM_TARGETS, false);
   }

   public void setCustomTargets(boolean value) {
      settings.set(CU_PREF_BASE + CU_CUSTOM_TARGETS, value);
      notifyListeners();
   }

   public boolean isTracking(NutrientInfo ni) {
      return settings.getBoolean(CU_TRACK+ni.getName(), ni.getDefaultTracking());
   }
   
   public void setTracking(NutrientInfo ni, boolean b) {
      settings.set(CU_TRACK+ni.getName(), b);
      notifyListeners();
   }

   
   public final void addUserChangeListener(UserChangeListener l) {
      getListeners().add(l);
   }

   private List getListeners() {
      if (null == listeners) {
         listeners = new ArrayList();
      }
      return listeners;
   }
   
   protected final void notifyListeners() {
      List l = getListeners();
      for (Iterator iter = l.iterator(); iter.hasNext();) {
         UserChangeListener listener = (UserChangeListener) iter.next();
         listener.userChanged(this);
      }
   }

   public final void removeUserChangeListener(UserChangeListener l) {
      getListeners().remove(l);
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
      notifyListeners();
   }

   public boolean firstRun() {
      return settings.getBoolean(CU_FIRST_RUN, true);
   }
   
   public void setFirstRun(boolean val) {
      settings.set(CU_FIRST_RUN, val);
      notifyListeners();
   }
 
   public boolean getCheckForUpdates() {
      return settings.getBoolean(CHECK_FOR_UDAPTES, true);
   }
   
   public void setCheckForUpdates(boolean val) {
      settings.set(CHECK_FOR_UDAPTES, val);
      notifyListeners();
   }

   public List getTracked(List list) {
      List tracked = new ArrayList();
      for (int i=0; i<list.size(); i++) {
         NutrientInfo ni = (NutrientInfo)list.get(i);
         if (isTracking(ni)) {
            tracked.add(ni);
         }
      }
      return tracked;
   }

   public void setLastBuild(int build) {
      settings.set(LAST_BUILD, build);         
   }

   public int getLastBuild() {
      return settings.getInt(LAST_BUILD, 0);
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
   

   public void setHideWhenMinimized(boolean state) { 
      settings.set(HIDE_WHEN_MINIMIZED, state);
   }
   
   public boolean getHideWhenMinimized() {
      return settings.getBoolean(HIDE_WHEN_MINIMIZED, false);
   }
   
   
   public void setDietDivider(int val) { 
      settings.set(DIET_DIVIDER, val); 
   }
   public int getDietDivider(int val) { 
      return settings.getInt(DIET_DIVIDER, val); 
   }
   
   public void saveWindow(JFrame frame) {
      try {
         frame.setVisible(true);
         settings.set(MAIN_WINDOW +".width", frame.getWidth());
         settings.set(MAIN_WINDOW +".height", frame.getHeight());
         settings.set(MAIN_WINDOW +".x", frame.getLocationOnScreen().x);
         settings.set(MAIN_WINDOW +".y", frame.getLocationOnScreen().y);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   public void restoreWindow(JFrame frame, Point p) {      
      Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
      Dimension screen = defaultToolkit.getScreenSize();
      int x = settings.getInt(MAIN_WINDOW +".x", p.x);
      int y = settings.getInt(MAIN_WINDOW +".y", p.y);
      int w = settings.getInt(MAIN_WINDOW +".width", frame.getWidth());
      int h = settings.getInt(MAIN_WINDOW +".height", frame.getHeight());
      if (x < 0 || x+w*0.10 > screen.width) {
         x = p.x;
         //w = frame.getWidth();
      }
      if (y < 0 || y+h*0.10 > screen.height) {
         y = p.y;
         //h = frame.getHeight();
      }
      frame.setLocation(x, y);
      frame.setSize(w, h);       
   }
}
