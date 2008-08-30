/*
 * Created on Mar 11, 2006 by davidson
 */
package ca.spaz.gui;

import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.jdesktop.swingx.JXMonthView;


public class DateChooser extends WrappedPanel {
   private String title = "Choose Date";
 
   private JXMonthView cal;
   
   public DateChooser(Date curDate) { 
      cal = new JXMonthView();
      cal.setTraversable(true);
      cal.setSelectionInterval(curDate, curDate);

      add(cal); 
   }
      
   public static Date pickDate(JComponent parent, Date d) {
      DateChooser dc = new DateChooser(d);      
      WrapperDialog.showDialog(parent, dc);
      return (Date)dc.cal.getSelection().first(); 
   }
   
   public static Date pickDate(JComponent parent, Date d, String title) {
      DateChooser dc = new DateChooser(d); 
      dc.setTitle(title);
      WrapperDialog.showDialog(parent, dc);
      return (Date)dc.cal.getSelection().first(); 
   }   
 
   private void setTitle(String title) {
      this.title = title;
   }

   public String getTitle() {
      return title;
   }

   public String getSubtitle() {
      return null;
   } 

   public String getInfoString() {
      return title;
   }

   public ImageIcon getIcon() {
      return null;
   }


   public boolean showSidebar() { 
      return false;
   }
   
   public boolean isCancellable() {
      return false;
   }

   public void doCancel() {
   }

   public boolean doAccept() { return true; }
   
   
}
