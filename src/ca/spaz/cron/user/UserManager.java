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

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.gui.ErrorReporter;
import ca.spaz.gui.WrapperDialog;
import ca.spaz.util.*;

/**
 * A CRONOMETER-specific, property-based <code>User</code> implementation. This 
 * class stores all the global settings for all users and manages the list of 
 * individual <code>User</code>'s in CRONOMETER.
 * 
 * @author Simon Werner
 */
public class UserManager {
   

   private static final String LAST_BUILD = "last.build";
   private static final String CHECK_FOR_UDAPTES = "check.for.updates";
   private static final String HIDE_WHEN_MINIMIZED = "hide.when.minimized";
   private static final String SETTINGS_FILE = "Settings.xml";
   private static final String OLD_SETTINGS_FILE = "user.settings";
   private static final String MAIN_WINDOW = "main.window";
   private static final String LAST_USER = "last.user";
   private static final String FIRST_CRON_RUN = "first.cron.run";
   private static final String DIET_DIVIDER = "diet.divider";

   private static String subdirectory = "cronometer";
   private static UserManager instance = null;
   private static Settings settings;
   private static User currentUser;
   private static List<User> userList;
   private static List<UserChangeListener> listeners;
   private static User lastSelectedUser = null;

   // TODO: Should not hardcode these
   public static final String userFileList[] = { 
      "biomarkers.xml", "metrics.xml", "notes.xml", "servings.xml" };
      
   public static final UserManager getUserManager() {
      if (null == instance) {
         instance = new UserManager();
      }
      return instance;
   }
   
   public UserManager() {
      if (settings == null) {
         settings = new Settings(Settings.TAG_GENERAL);
         userList = settings.loadSettings(getSettingsFile());
         settings.save();
         setCurrentUser(getLastUser());
      }
   }

   /**
    * Get the last user that used CRON-o-meter.
    */
   public User getLastUser() {
      User user = getUser(getLastUsername());
      if (user == null) {
         user = getUserList().get(0);
      }
      return user;
   }
   
   /**
    * Get the last username that used CRON-o-meter, the last time it was run.
    * @return
    */
   public String getLastUsername() {
      return settings.get(LAST_USER, "Default User");
   }
   
   /**
    * Open the dialog to manage the users.
    */
   public static void startUserManagerDialog() {
      WrapperDialog.showDialog(CRONOMETER.getInstance(), new UserManagerDialog(), true);      
   }

   public void saveUserProperties() throws IOException {
      settings.save();
   }
   
   /**
    * Add a new user to Cronometer via the UI.
    * @param parentWindow allow a popup to set the user settings
    * @return the user that was just added.
    */
   public void addUser(JComponent parentWindow) {
      User user = new User(new Settings(Settings.TAG_USER));
      setCurrentUser(user);
      addUser(user);
      user.doFirstRun(parentWindow);
      settings.save();
   }
   
   /**
    * Add a new user to Cronometer
    * @param user the user to add.
    */
   public void addUser(User user) {
      userList.add(user);
      settings.save();
   }
   
   /**
    * Find the User with the given username
    * @param username the name of the user
    * @return User the Object that relates to the given name.  Null if the User is not found.
    */
   public User getUser(String username) {
      return getUser(getUserList(), username);
   }
   
   /**
    * Find the User with the given username
    * @param username the name of the user
    * @return User the Object that relates to the given name.  Null if the User is not found.
    */
   public static User getUser(List<User> uList, String username) {
      Iterator<User> it = uList.iterator();
      User user = null;
      
      while (it.hasNext()) {
         user = it.next();
         if (username.equals(user.getUsername())) {
            return user;
         }
      }
      return null;
   }

   /**
    * Delete a user from Cronometer.
    * @param user the user Object to delete.
    */
   public void deleteUser(User user) {
      userList.remove(user);
      deleteFiles(user);
      if (currentUser == user) {
         // Make sure we select a new currentUser, find the first active user
         setCurrentUser(getUserList().get(0));
      }
      settings.save();
   }
   
   /**
    * Delete all the settings file related to this user.
    * @param user
    */
   public void deleteFiles(User user) {
      File userDir = getUserDirectory(user);
      if (userDir.exists()) {
         int i;
         for (i=0; i<userFileList.length; i++) {
            File userFile = new File(userDir.getAbsolutePath(), userFileList[i]);
            if (userFile.exists()) {
               if (!userFile.delete()) {
                  Logger.error("Unable to delete the file: " + userFile.getAbsolutePath());
               }
            }
         }
         if (!userDir.delete()) {
            Logger.error("Unable to delete the folder: " + userDir.getAbsolutePath());
         }
      }
   }
   
   
   /**
    * Delete the user with the given <code>username</code>
    * @param username the name of the user
    * @return true if the delete was succesful
    */
   public boolean deleteUser(String username) {
      User user = getUser(username);
      if (userExists(username)) {
         deleteUser(user);
         return true;
      }
      return false;
   }
   
   public static User getCurrentUser() {
      return currentUser; 
   }
   
   public void setCurrentUser(User user) {
      if (user == null || currentUser == user) {
         return;
      }
      if (currentUser != null) {
         currentUser.saveUserData();
      }
      currentUser = user;
      settings.set(LAST_USER, currentUser.getUsername());
      this.notifyUserChangeListeners();
   }
   
   /**
    * Search for a user and set that user as the current user.  
    * @param username the name of the user
    * @return true if the operation was successful
    */
   public boolean setCurrentUser(String username) {
      User user = getUser(username);
      if (user != null) {
         setCurrentUser(user);
         return true;
      } else {
         return false;
      }
   }
   
   public static List<User> getUserList() {
      return userList;
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
   
   public static File getCronometerDirectory() {
      File appDir = ToolBox.getUserAppDirectory(UserManager.getSubdirectory());
      if (!appDir.exists()) {
         appDir.mkdirs();
         if (!appDir.exists()) {
            Logger.error("Unable to create user app prefs directory " + appDir);
         }
      }
      return appDir;
   }

   public static File getUserDirectory(User user) {
      File userDir = new File(getCronometerDirectory(), user.getUsername());
      if (!userDir.exists()) {
         userDir.mkdirs();
         if (!userDir.exists()) {
            Logger.error("Unable to create user app prefs directory " + userDir);
         }
      }
      return userDir;
   }
   
   /**
    * Upgrade the settings file to the multiuser style.
    * @param inFile the old "user.settings" file
    * @param outFile the new filename, "Settings.xml".
    */
   private void upgradeToMultiuser(File inFile, File outFile) {
      // Move the User files to the new location
      String cronPath = UserManager.getCronometerDirectory().getAbsolutePath();
      moveUserFiles(cronPath, cronPath, User.DEFAULT_USERNAME);
      
      // Update the settings file
      if (inFile.exists() && outFile.exists()) {
         // This condition should never occur
          //System.err.println("Error: Upgrade of old settings file failed.  Please contact the CRON-o-meter developers.");
      //    System.exit(-1);
      }
      
      if (!inFile.exists()) return;
      
      if (!outFile.exists()) {
         try {
              InputStream in = new BufferedInputStream(new FileInputStream(inFile));
              PrintStream out = new PrintStream(
                      new BufferedOutputStream(new FileOutputStream(outFile)));
              Settings.convertSettingsFile(in, out);
              out.close();
              in.close();
              //inFile.renameTo(new File(inFile.getParent(), "backup"));             
         } catch (Exception e) {
              e.printStackTrace();
         }
      }
   }
   
   public void moveUserFiles(String oldUserDirStr, String newBaseDir, String newUsername) {
      try {
         File oldUserDir = new File(oldUserDirStr);
         File newUserDir = new File(newBaseDir, newUsername);
   
         Logger.log(oldUserDir.getAbsolutePath());
         if (!oldUserDir.exists()) {
            Logger.error("Unable to find the old user directory.");
            return;
         }
         
         if (!newUserDir.exists()) {
            newUserDir.mkdir();
         }
         
         int i;
         for (i=0; i<userFileList.length; i++) {
            File oldUserFile = new File(oldUserDir.getAbsolutePath(), userFileList[i]);
            File newUserFile = new File(newUserDir.getAbsolutePath(), userFileList[i]);
            if (oldUserFile.renameTo(newUserFile)) {
               Logger.log(oldUserFile.getAbsolutePath() + " moved to " + newUserFile.getAbsolutePath());
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   
   public File getSettingsFile() {
      File settingFile = new File(getCronometerDirectory(), SETTINGS_FILE);
      File oldSettingFile = new File(getCronometerDirectory(), OLD_SETTINGS_FILE);

      Logger.debug("Initializing settings file " + settingFile.getAbsolutePath());
      if (oldSettingFile.exists()) {
         Logger.debug("Upgrading settings file " + settingFile.getAbsolutePath());
         upgradeToMultiuser(oldSettingFile, settingFile);
      } else if(!settingFile.exists()) {
         Logger.debug("Creating settings file " + settingFile.getAbsolutePath());
         try {
            if (settingFile.createNewFile()) {
               // Nothing.  All is well.
            } else {
               Logger.error("Unable to create settings file");
            }
         } catch (IOException e) {
            Logger.error("getSettingsFile()", e);
            ErrorReporter.showError(e, CRONOMETER.getInstance()); 
         }
      } else {
         // Nothing.  All is well.
      }
      return settingFile;
   }
   
   public void setLastBuild(int build) {
      settings.set(LAST_BUILD, build);         
   }

   public int getLastBuild() {
      return settings.getInt(LAST_BUILD, 0);
   }

   public void setCheckForUpdates(boolean val) {
      settings.set(CHECK_FOR_UDAPTES, val);
      notifyUserChangeListeners();
   }

   public boolean getCheckForUpdates() {
      return settings.getBoolean(CHECK_FOR_UDAPTES, true);
   }
   
   public void setHideWhenMinimized(boolean state) { 
      settings.set(HIDE_WHEN_MINIMIZED, state);
   }
   
   public boolean getHideWhenMinimized() {
      return settings.getBoolean(HIDE_WHEN_MINIMIZED, false);
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

   public boolean firstCronRun() {
      return settings.getBoolean(FIRST_CRON_RUN, true);
   }

   public void setFirstCronRun(boolean val) {
      settings.set(FIRST_CRON_RUN, val);
      notifyUserChangeListeners();
   }
   
   public void setDietDivider(int val) { 
      settings.set(DIET_DIVIDER, val); 
   }
   
   public int getDietDivider(int val) { 
      return settings.getInt(DIET_DIVIDER, val); 
   }

   /**
    * Search if the given username exists
    * @param username
    * @return
    */
   public boolean userExists(String username) {
      return getUser(username) != null;
   }

   public static boolean renameUserDirectory(User oldUser, String newName) {
      File userDir = getUserDirectory(oldUser); 
      if (!userDir.renameTo(new File(getCronometerDirectory(), newName))) {
         System.err.println("Unable to rename the user folder");
         return false;
      }
      return true;
         
   }

   private List<UserChangeListener> getListeners() {
      if (null == listeners) {
         listeners = new ArrayList<UserChangeListener>();
      }
      return listeners;
   }
   
   public final void addUserChangeListener(UserChangeListener l) {
      if ( ! getListeners().contains(l)) {
         getListeners().add(l);
      }
   }

   protected final void notifyUserChangeListeners() {
      List l = getListeners();
      for (Iterator iter = l.iterator(); iter.hasNext();) {
         UserChangeListener listener = (UserChangeListener) iter.next();
         listener.userChanged(this);
      }
      CRONOMETER.getInstance().setTitle(CRONOMETER.getFullTitleWithUser());
   }
   
   public final void removeUserChangeListener(UserChangeListener l) {
      getListeners().remove(l);
   }

   /**
    * Get the number of users that the UserManager class is managing.
    * @return the number of users
    */
   public int numberOfUsers() {
      return userList.size();
   }

   /**
    * Return any user, except the current user.  If the lastSelectedUser has been set, then 
    * return the lastSelectedUser.
    * @return any user
    */
   public static User selectOtherUser() {
      List<User> uList = getOtherUsers();
      if (uList.size() == 0) {
         return null;
      }
      if (lastSelectedUser == null || lastSelectedUser == currentUser) {
         lastSelectedUser = uList.get(0);
      }
      return lastSelectedUser;
   }

   /**
    * Return the list of users, but does not include the current user.
    * @return the list of Users
    */
   public static List<User> getOtherUsers() {
      ArrayList<User> uList = new ArrayList<User>(userList);
      uList.remove(getCurrentUser());
      return uList;
   }

   public static void setLastSelectedUser(User user) {
      lastSelectedUser = user;
   }
}
