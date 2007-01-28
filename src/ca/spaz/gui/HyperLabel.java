/*
 * Created on 3-May-2006
 */
package ca.spaz.gui;

import java.awt.Cursor;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.JLabel;

public class HyperLabel extends JLabel implements MouseListener {
   private static final String BLUE = "#0000FF";
   private static final String RED = "#FF0000";
   private static final String GRAY = "#AAAAAA";
   
   private String color = BLUE;
   private String text = "";
   
   private Vector listeners = new Vector(); 
   
   public HyperLabel() {
      init();
   }
   
   public HyperLabel(String text) {
      super();      
      setText(text, true);
      init();
   }

   private void init() {
      addMouseListener(this);
      setEnabled(true);
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
   } 
   
   public void setEnabled(boolean val) {
      super.setEnabled(val);
      color = val ? BLUE : GRAY;
      setText(text);
   }
   
   public void setText(String text) {
      setText(text, true);
   }

   public void setText(String text, boolean doBold) {
      this.text = text;
      super.setText("<html>" + (doBold ? "<b>" : "") + "<u><font color=\""+color+"\">"+text+"</font></u>"+(doBold ? "</b>" : "")+"</html>");
   }

   public void mouseClicked(MouseEvent e) {
      if (isEnabled()) {
         ActionEvent ae = new ActionEvent(this, 0, null);
         for (int i=0; i<listeners.size(); i++) {
            ActionListener al = (ActionListener)listeners.get(i);
            al.actionPerformed(ae);
         }
      }
   }


   public String getPlainText() {
      return text;
   }
   
   public void mouseEntered(MouseEvent e) {}

   public void mouseExited(MouseEvent e) {}

   public void mousePressed(MouseEvent e) {}

   public void mouseReleased(MouseEvent e) {}
   
   public void addActionListener(ActionListener al) {
      listeners.add(al);
   }
   
   public void removeActionListener(ActionListener al) {
      listeners.remove(al);
   }

}
