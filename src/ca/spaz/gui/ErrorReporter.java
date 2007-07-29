/*
 * Created on 7-Jan-2006
 */
package ca.spaz.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.*;

import ca.spaz.util.ImageFactory;

public class ErrorReporter extends WrappedPanel {
   private String message;
   
   public ErrorReporter(Exception e) {
      StringBuffer sb = new StringBuffer();
      if (e != null) {
         sb.append("<html><div align=\"center\" width=\"600\">");
         sb.append("<u>"+e.toString()+"</u><br><br>");
         sb.append("<div align=\"left\"><code>");
         for (int i=0; i<e.getStackTrace().length && i < 8; i++) {
            sb.append(e.getStackTrace()[i].toString() +"<br>");
         }
         sb.append("</code></div>");
         sb.append("</div></html>");
      }
      
      JLabel lbl = new JLabel(sb.toString(),JLabel.CENTER);
      setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
      setLayout(new BorderLayout(4,4));
      add(lbl, BorderLayout.CENTER);
   }
   
   public static void showError(Exception e, Component parent) {
      e.printStackTrace();
      ErrorReporter er = new ErrorReporter(e);
      WrapperDialog.showDialog(parent, er);
   }   
   
   public static void showError(String message, Exception e, Component parent) {
      e.printStackTrace();
      ErrorReporter er = new ErrorReporter(e);
      er.message = message;
      WrapperDialog.showDialog(parent, er);
   }   
   
   public static void showError(String message, Component parent) {
      ErrorReporter er = new ErrorReporter(null);
      er.message = message;
      WrapperDialog.showDialog(parent, er);
   }  
   
   public static void showNotYetImplemented(Component parent) {
      ErrorReporter er = new ErrorReporter(null);
      er.message = "Not Yet Implemented";
      WrapperDialog.showDialog(parent, er);
   }    
    
   public String getTitle() {
      return "Error";
   }
   
   public String getSubtitle() {
      if (message != null) {
         return message;
      }
      return "An Unexpected Error Occurred";
   }
   
   public String getInfoString() {
      return "Unexpected Error";
   }

   public ImageIcon getIcon() {
      return  new ImageIcon(ImageFactory.getInstance().loadImage("/img/apple-100x100.png"));
   }

   public boolean isCancellable() {
      return false;
   }

   public void doCancel() { }

   public boolean doAccept() { return true; }

}
