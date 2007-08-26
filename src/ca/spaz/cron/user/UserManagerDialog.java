package ca.spaz.cron.user;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;

import se.datadosen.component.RiverLayout;
import ca.spaz.cron.CRONOMETER;
import ca.spaz.gui.*;
import ca.spaz.util.ImageFactory;
import ca.spaz.util.Logger;

public class UserManagerDialog extends WrappedPanel implements ListSelectionListener {

   private JButton delBtn;
   private JButton addBtn;
   private JList userList;
   private DefaultListModel userListModel;
   private JPanel userListPanel;
   private JToolBar toolBar;
   private boolean dirty = false;
   private Vector<ChangeListener> listeners;
   private static boolean addingNewUser = false; //set to true when we are adding a new user

   private UserManager userMan;
   
   public UserManagerDialog() {
      this.userMan = UserManager.getUserManager();
      
      JPanel cp = new JPanel(new RiverLayout(2,1)); 
      cp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      cp.add("tab center vfill", getToolBar());
      cp.add("tab center vfill", getUserListPanel());
      cp.add("tab center hfill", Box.createVerticalBox());
      
      // Grey out the delete button if there is nothing to delete.
      if (userListModel.size() == 1) {
         delBtn.setEnabled(false);
      }
      
      setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
      setLayout(new BorderLayout(12,12));
        
      add(cp, BorderLayout.CENTER); 
      selectCurrentUser();

   }
   
   public String getTitle() { return "User Manager"; }
   public String getSubtitle() { return "Manager Users"; }
   public String getInfoString() { 
      return  "<div align=\"justify\" width=\"180\"><br>" +
              "CRON-o-meter allows you to have multiple users, "
            + "each user has their own profile."
            + "<br><br>"
            + "Here you can add and remove users from CRON-o-meter."
            + "</div>";
   }

   public boolean showSidebar() { 
      return true;
   }
   
   public ImageIcon getIcon() {
      return new ImageIcon(ImageFactory.getInstance().loadImage("/img/apple-100x100.png"));
   }
   
   public static boolean showDialog(JComponent parent) {
      try {
         UserManagerDialog usd = new UserManagerDialog();
         return WrapperDialog.showDialog(parent, usd, true);
      } catch (Exception e) {
         ErrorReporter.showError(e, parent);
      }
      return false;
   }

   private static Border makeTitle(String str) {
      return BorderFactory.createCompoundBorder(
         BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(), str), 
            BorderFactory.createEmptyBorder(2,26,2,26));
   }
 
   public void addChangeListener(ChangeListener cl) {
      getListeners().add(cl);
   }
   
   public void removeChangeListener(ChangeListener cl) {
      getListeners().remove(cl);
   }
   
   private Vector<ChangeListener> getListeners() {
      if (listeners == null) {
         listeners = new Vector<ChangeListener>();
      }
      return listeners;
   }

   private JComponent getUserListPanel() {
      if (null == userListPanel) {
         //Create and populate the list model.
         userListModel = new DefaultListModel();

         Iterator<User> it = UserManager.getUserList().iterator();
         while (it.hasNext()) {
            userListModel.addElement(it.next().getUsername());
         }

         //Create the list and put it in a scroll pane inside a border.
         userList = new JList(userListModel);
         userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         userList.setSelectedIndex(0);
         userList.addListSelectionListener(this);
         userListPanel = new JPanel(new GridLayout(1,1));
         userListPanel.setPreferredSize(new Dimension(150, 100));
         userListPanel.setBorder(BorderFactory.createTitledBorder("Users"));
         JScrollPane listScrollPane = new JScrollPane(userList);
         userListPanel.add(listScrollPane);
      }
      return userListPanel;
  }
   
   public boolean isCancellable() {
      return false;
   }

   public void doCancel() {
       // nothing needed
   }

   /**
    * If we are adding a new user, set this to true, else false.
    */
   public static void setAddNewUser(boolean val) {
      addingNewUser = val;
   }
   
   /**
    * Check to see if we are adding a new user.
    * @return
    */
   public static boolean isAddingNewUser() {
      return addingNewUser;
   }
   
   private void addUser() {
      setAddNewUser(true);
      userMan.addUser(this);
      userListModel.addElement(UserManager.getCurrentUser().getUsername());
      selectCurrentUser();
      delBtn.setEnabled(true);
      setDirty(true);
   }

   public void fireChangeEvent() {
      ChangeEvent ce = new ChangeEvent(this);
      Iterator iter = getListeners().iterator();
      while (iter.hasNext()) {
         ((ChangeListener)iter.next()).stateChanged(ce);
      }
   }

   private void setDirty(boolean val) {
      dirty = val;
      if (dirty) {
         fireChangeEvent();
      }
   }

   /**
    * @return the toolbar.
    */
   private JToolBar getToolBar() {
       if (null == toolBar) {
           toolBar = new JToolBar();
           toolBar.setRollover(true);
           toolBar.setOrientation(JToolBar.VERTICAL);
           toolBar.setFloatable(false);
           toolBar.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
           toolBar.add(getAddButton());
           toolBar.add(getDeleteButton());
           toolBar.add(Box.createGlue());
       }
       return toolBar;
   }

   private JButton getAddButton() {
       if (null == addBtn) {
           ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/add.gif"));
           addBtn = new JButton(icon);
           addBtn.setToolTipText("Add a new user.");
           addBtn.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   addUser();
               }
           });
           CRONOMETER.fixButton(addBtn);
       }
       return addBtn;
   }

   private JButton getDeleteButton() {
       if (null == delBtn) {
           ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/trash.gif"));
           delBtn = new JButton(icon);
           delBtn.setToolTipText("Delete the selected user.");
           delBtn.addActionListener(new DeleteButtonListener());
           CRONOMETER.fixButton(delBtn);
       }
       return delBtn;
   }

   class DeleteButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {

          ListSelectionModel lsm = userList.getSelectionModel();
          int firstSelected = lsm.getMinSelectionIndex();
          deleteSelectedUser(firstSelected);

          int size = userListModel.size();

          if (size == 1) {
          //List is empty: disable delete, up, and down buttons.
              delBtn.setEnabled(false);
          }
      }
  }
   
   private boolean isOkToDeleteUser() {
      int choice = JOptionPane.showConfirmDialog(this, 
            "This will delete all this user's settings and history information.\n" +
            "Are you sure you want to do delete '" + 
            UserManager.getCurrentUser().getUsername() + "'?",
            "Delete User?", 
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (choice == JOptionPane.YES_OPTION) {
         return true;
      }
      return false;
   } 
   
   /** 
    * Delete the user selected in the userListModel
    * @param row the row item to delete
    */
   private void deleteSelectedUser(int row) {
      String username = (String)userListModel.get(row);
      
      if ( ! isOkToDeleteUser()) {
         return;
      }
      
      if (userListModel.size() == 1) {
         Logger.error("Not allowed to delete last user.");
         return;
      }
      if (!userMan.deleteUser(username)) {
         Logger.error("Unable to delete user.");
         return;
      }
      userListModel.remove(row);
      selectCurrentUser();
      setDirty(true);
   }
   
   public boolean doAccept() {
      try {
         userMan.saveUserProperties();
      } catch (Exception e) { 
         e.printStackTrace();
         ErrorReporter.showError(e, this); 
      }
      return true;
   }

   //Listener method for list selection changes.
   public void valueChanged(ListSelectionEvent e) {
      
       if (e.getValueIsAdjusting() == false) {

           if (userList.getSelectedIndex() == -1) {
           //No selection: disable delete button.
              Logger.debug("Nothing Selected.");
               selectCurrentUser();
               delBtn.setEnabled(false);
           } else {
           //Single selection: permit all operations.
              if (userListModel.getSize() > 1) {
                 delBtn.setEnabled(true);
              } else {
                 delBtn.setEnabled(false);
              }
              Logger.debug("Selection: " + (String)userList.getSelectedValue());
               userMan.setCurrentUser((String)userList.getSelectedValue());
           }
       }
   }

   /**
    * Select the current user in the list.
    *
    */
   public void selectCurrentUser() {
      userList.setSelectedValue(UserManager.getCurrentUser().getUsername(), true);   
      if (userListModel.getSize() == 1) {
         delBtn.setEnabled(false);
      }
   }

}
