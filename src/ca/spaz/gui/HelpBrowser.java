package ca.spaz.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;

public class HelpBrowser extends JFrame {

   private WebViewer viewer;
   private File base;
  
   public HelpBrowser(String title, File base) {
      this.setTitle(title);
      this.base = base;
      setLayout(new BorderLayout());      
      add(getViewer(), BorderLayout.CENTER); 
      setPage("index.html");
      setVisible(true);
   }
   
   public void setPage(String name) {
      getViewer().setHTML(new File(base, name));
   }
   
   public WebViewer getViewer() {
      if (viewer == null) {
         viewer = new WebViewer();
         viewer.setPreferredSize(new Dimension(600,400));
      }
      return viewer;
   }
}
