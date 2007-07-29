package ca.spaz.cron.ui;

import java.awt.*;
import java.awt.event.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.user.UserManager;

public class SysTray { 
   private TrayIcon trayIcon;
   private PopupMenu popup;
   
   public SysTray() {
      if (SystemTray.isSupported()) {         
         try {
            SystemTray tray = SystemTray.getSystemTray();  
            tray.add(getTrayIcon());
         } catch (AWTException e) {
            System.err.println("TrayIcon could not be added.");
         }
      }  
   }
   
   private TrayIcon getTrayIcon() {
      if (trayIcon == null) {
         trayIcon = new TrayIcon(CRONOMETER.getWindowIcon(), CRONOMETER.getFullTitle(), getPopupMenu());
         trayIcon.setImageAutoSize(true);
         trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {              
               CRONOMETER.getInstance().restoreWindow();
            }
         });
      }
      return trayIcon;
   }

   public void showInfoMessage(String title, String message) {
      trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
   }
   
   public void showWarningMessage(String title, String message) {
      trayIcon.displayMessage(title, message, TrayIcon.MessageType.WARNING);
   }
   
   public void showErrorMessage(String title, String message) {
      trayIcon.displayMessage(title, message, TrayIcon.MessageType.ERROR);
   }
   
   public void showMessage(String title, String message) {
      trayIcon.displayMessage(title, message, TrayIcon.MessageType.NONE);
   }
   
   private PopupMenu getPopupMenu() {
      if (popup == null) {
         popup = new PopupMenu();
         popup.add(makeAboutItem());
         popup.addSeparator();         
         popup.add(makeServingItem());
         popup.add(makeFoodItem());
         popup.add(makeRecipeItem());
         popup.addSeparator();
         popup.add(makeHideItem()); 
         popup.addSeparator();
         popup.add(makeQuitItem());
      }
      return popup;
   }

   private MenuItem makeHideItem() {
      final CheckboxMenuItem item = new CheckboxMenuItem("Hide when minimized");
      item.setState(UserManager.getUserManager().getHideWhenMinimized());
      item.addItemListener(new ItemListener() {      
         public void itemStateChanged(ItemEvent e) {
            UserManager.getUserManager().setHideWhenMinimized(item.getState());
         }
      });
      return item;
   }

   private MenuItem makeServingItem() {
      MenuItem item = new MenuItem("Add Serving...");
      item.addActionListener( new ActionListener() {
         public void actionPerformed(ActionEvent e) {  
            CRONOMETER.getInstance().doAddServing();
         }
      });
      return item;
   }

   private MenuItem makeFoodItem() {
      MenuItem item = new MenuItem("Create New Food...");
      item.addActionListener( new ActionListener() {
         public void actionPerformed(ActionEvent e) {  
            CRONOMETER.getInstance().doCreateNewFood();
         }
      });
      return item;
   }
   
   private MenuItem makeRecipeItem() {
      MenuItem item = new MenuItem("Create New Recipe...");
      item.addActionListener( new ActionListener() {
         public void actionPerformed(ActionEvent e) {  
            CRONOMETER.getInstance().doCreateNewRecipe();
         }
      });
      return item;
   }

   private MenuItem makeQuitItem() {
      MenuItem item = new MenuItem("Exit");
      item.addActionListener( new ActionListener() {
         public void actionPerformed(ActionEvent e) { 
            CRONOMETER.getInstance().doQuit();
         }
      });
      return item;
   }

   private MenuItem makeAboutItem() {
      MenuItem item = new MenuItem("About...");
      item.addActionListener( new ActionListener() {
         public void actionPerformed(ActionEvent e) { 
            CRONOMETER.getInstance().doAbout();
         }
      });
      return item;
   }
    
}
