package ca.spaz.cron.user;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.text.BadLocationException;

public class NoteEditor extends JPanel {

   private JScrollPane jsp;
   private JTextArea edit;
   
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
      }
      return edit;
   }
   
   private void clear() {
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
   
   public void setContents(String txt) {
      getEditor().setText(txt);
      getEditor().setCaretPosition(0);
   }
   
}
