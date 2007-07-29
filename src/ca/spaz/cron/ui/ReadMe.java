package ca.spaz.cron.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import ca.spaz.gui.*;

public class ReadMe extends WrappedPanel {
   private String title;
   
   public ReadMe(JFrame parent, String title, URL url) {
      this.title = title;
      setLayout(new BorderLayout());
      WebViewer wv = new WebViewer(url);
      wv.setPreferredSize(new Dimension(600,400));
      add(wv, BorderLayout.CENTER);
      WrapperDialog.showDialog(parent, this);      
   }
   
   public String getTitle() {
      return title;
   }

   public String getSubtitle() {
      return title;
   }

   public String getInfoString() {
      return null;
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

   public boolean doAccept() {
      return true;
   }

}