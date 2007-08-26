package ca.spaz.cron.notes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Date;

import javax.swing.*;
import javax.swing.text.BadLocationException;

import ca.spaz.cron.user.UserManager;

public class NoteEditor extends JPanel implements FocusListener {

   private JScrollPane jsp;
   private JTextArea edit;
   private Date curDate = null;
   
   public NoteEditor() {      
      setLayout(new BorderLayout(4,4));
      setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
      jsp = new JScrollPane(getEditor());
      jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      jsp.setPreferredSize(new Dimension(200,200));
      add(jsp, BorderLayout.CENTER);
   }
   
   private JTextArea getEditor() {
      if (edit == null) {
         edit = new JTextArea();
         edit.setWrapStyleWord(true);
         edit.setLineWrap(true);
         edit.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
         edit.addFocusListener(this);
      }
      return edit;
   }
   
   public void clear() {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            try {
               getEditor().getDocument().remove(0, getEditor().getDocument().getLength());
            } catch (BadLocationException e) { 
               e.printStackTrace();
            }
         }
      });
   }
   
   public String getContents() {
      return getEditor().getText();
   }
   
   public void setContents(final String txt) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            getEditor().setText(txt);
            getEditor().setCaretPosition(0);
         }
      });
   }

   public void saveCurrentNote() {
      if (curDate != null) {
         UserManager.getCurrentUser().setNotes(getContents(), curDate);
      }
   }
   
   private String curNote = null;
   
   public void setDate(Date d) {      
      curDate = d;
      clear();
      curNote = UserManager.getCurrentUser().getNotes(curDate);
      if (curNote == null) {
         curNote = "";
      }
      setContents(curNote);         
   }
    
   public void focusGained(FocusEvent arg0) {
      // Do nothing
   }

   /** 
    * Invoked when the text area loses the keyboard focus.
    * This will not be invoked when the user clicks on the next/previous day button.
    */
   public void focusLost(FocusEvent e) {
      if (curDate != null) {
         saveCurrentNote(); 
      }
   }
}
