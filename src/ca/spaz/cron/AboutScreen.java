/*
 * Created on 7-Jan-2006
 */
package ca.spaz.cron;

import java.awt.*;

import javax.swing.*;

import ca.spaz.gui.*;
import ca.spaz.util.ImageFactory;

public class AboutScreen extends WrappedPanel {

   public AboutScreen() {
      JLabel info = new JLabel("<html><div align=\"center\">"
         + "<b>Credits</b> "
         + "<table width=\"475\"> "
         + "<tr><td>Aaron Davidson</td><td>aaron@spaz.ca</td><td>Programming</td></tr>"
         + "<tr><td>Chris Rose</td><td>chris@offlineblog.com</td><td>Programming</td></tr>"
         + "<tr><td>Gerald Turnquist</td><td>geraldt@sasktel.net</td><td>Programming</td></tr>"         
         + "<tr><td>Michael Rae</td><td>mikalra@cadvision.com</td><td>Consulting</td></tr>"
         + "<tr><td>Antonio Zamora</td><td>zamora@scientificpsychic.com</td><td>Technical Writing</td></tr>"
         + "</div>" + "</html>",
         JLabel.CENTER);
      setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      setLayout(new BorderLayout(5,5));
      add(info, BorderLayout.NORTH); 
   }
   
   public static void showAbout(JFrame parent) {
      WrapperDialog.showDialog(parent, new AboutScreen());
   }
   
   public String getTitle() {
      return "About " + CRONOMETER.getFullTitle();
   }

   public String getSubtitle() {
      return "About " + CRONOMETER.getFullTitle();
   }

   public String getInfoString() {
      return CRONOMETER.getFullTitle();      
   }


   public ImageIcon getIcon() {
      return  new ImageIcon(ImageFactory.getInstance().loadImage("/img/apple-100x100.png"));
   }
   

   public boolean isCancellable() {
      return false;
   }

   public void doCancel() {}

   public void doAccept() {}

}
