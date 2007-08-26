/*
 *******************************************************************************
 * Copyright (c) 2007 Chris Rose and AIMedia
 * All rights reserved. CRONUser and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Simon Werner
 *******************************************************************************/
package ca.spaz.cron.user;

import java.awt.*;
import java.text.DateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;

import org.jdesktop.swingx.JXDatePicker;

import se.datadosen.component.RiverLayout;
import ca.spaz.cron.CRONOMETER;
import ca.spaz.gui.*;
import ca.spaz.util.Logger;

public class UserDatePickerDialog extends WrappedPanel implements ListSelectionListener {

   private JList userList;
   private DefaultListModel userListModel;
   private Vector<ChangeListener> listeners;
   private DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
   private JXDatePicker datePicker;
   private JPanel userListPanel;
   
   private User selectedUser = null;
   private boolean cancelled = false;

   public UserDatePickerDialog() {
      JPanel cp = new JPanel(new RiverLayout(1,1)); 
      cp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      cp.add("p center hfill", getDatePicker());
      cp.add("p center hfill", getUserListPanel());
      cp.add("p center vfill", Box.createVerticalBox());
      
      setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
      setLayout(new BorderLayout(12,12));
        
      add(cp, BorderLayout.CENTER);
      selectDefaultUser();
   }
   
   public String getTitle() { return "Copy servings to another user"; }
   public String getSubtitle() { return "Select a user and date"; }
   public String getInfoString() { return  ""; }
   public boolean showSidebar() { return false; }
   public ImageIcon getIcon() { return null; }
   
   public static boolean showDialog(JComponent parent) {
      try {
         UserDatePickerDialog usd = new UserDatePickerDialog();
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
      // Create and populate the list model.
      userListModel = new DefaultListModel();

      Iterator<User> it = UserManager.getOtherUsers().iterator();
      while (it.hasNext()) {
         userListModel.addElement(it.next().getUsername());
      }

      // Create the list and put it in a scroll pane inside a border.
      userList = new JList(userListModel);
      userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      userList.addListSelectionListener(this);
      userListPanel = new JPanel(new GridLayout(1,1));
      userListPanel.setPreferredSize(new Dimension(150, 100));
      userListPanel.setBorder(makeTitle("User:"));
      JScrollPane listScrollPane = new JScrollPane(userList);
      userListPanel.add(listScrollPane);

      return userListPanel;
  }
   
   public boolean isCancellable() {
      return true;
   }

   public void doCancel() {
      selectedUser = null;
      cancelled = true;
   }
   
   public boolean doAccept() {
      return true;
   }

   //Listener method for list selection changes.
   public void valueChanged(ListSelectionEvent e) {
      
       if (e.getValueIsAdjusting() == false) {

           if (userList.getSelectedIndex() == -1) {
           //No selection
               selectedUser = null;
               Logger.debug("Nothing Selected.");
           } else {
           //Single selection
               Logger.debug("Selection: " + (String)userList.getSelectedValue());
               selectedUser = UserManager.getUserManager().getUser((String)userList.getSelectedValue());
               UserManager.setLastSelectedUser(selectedUser);
           }
       }
   }

   /**
    * Select the default user in the list. This is not the current user.
    */
   public void selectDefaultUser() {
      userList.setSelectedValue(UserManager.selectOtherUser().getUsername(), true);   
   }

   public boolean cancelPressed() {
      return cancelled;
   }
   
   public Date getDate() {
      return datePicker.getDate();
   }

   public User getUser() {
      return selectedUser;
   }

   /**
    * @return the title label for this component
    */
   private JXDatePicker getDatePicker() {
      DateFormat[] formats = {df};
      datePicker = new JXDatePicker();
      datePicker.setFormats(formats);
      datePicker.setDate(CRONOMETER.getDailySummary().getDate());
      datePicker.setFont(new Font("Application", Font.BOLD, 12)); 
      datePicker.getEditor().setEnabled(false);
      datePicker.getEditor().setFocusable(false);
      datePicker.getEditor().setOpaque(false);
      datePicker.getEditor().setBorder(null);
      datePicker.setBorder(makeTitle("Date:"));
      datePicker.setFocusable(false); 
      return datePicker;
   }
}
