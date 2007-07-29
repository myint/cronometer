/*
 * Created on 7-Jan-2006
 */
package ca.spaz.cron.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.gui.*;
import ca.spaz.util.ImageFactory;
import ca.spaz.util.ToolBox;


/**
 * @deprecated
 */
public class AboutScreen  extends WrappedPanel {

   public AboutScreen() {
     JLabel info = new JLabel("<html>"
         + "<br><b>Credits</b> "
         + "<table width=\"475\"> "
         + "<tr><td>Aaron Davidson</td><td>aaron@spaz.ca</td><td>Programming</td></tr>"
         + "<tr><td>Chris Rose</td><td>chris@offlineblog.com</td><td>Programming</td></tr>"
         + "<tr><td>Gerald Turnquist</td><td>geraldt@sasktel.net</td><td>Programming</td></tr>"         
         + "<tr><td>Michael Rae</td><td>mikalra@cadvision.com</td><td>Consulting</td></tr>"
         + "<tr><td>Antonio Zamora</td><td>zamora@scientificpsychic.com</td><td>Technical Writing</td></tr>"
         + "</table>" 
         + "</html>",
         JLabel.CENTER);
      setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      add(info);
      add(Box.createVerticalStrut(10)); 
      add(new JSeparator());
      add(Box.createVerticalStrut(10));
      
      JPanel jp = new JPanel(new GridLayout(2,0,4,4)); 
      jp.add(new JLabel("<html><b>Food Datasources: </b></html>", SwingConstants.LEFT), BorderLayout.CENTER);
      jp.add(makeLink("USDA sr19", "http://www.ars.usda.gov/Services/docs.htm?docid=8964"));
      add(jp); 
      
      add(Box.createVerticalStrut(10));
      add(new JSeparator());
      add(Box.createVerticalStrut(10));
      
      jp = new JPanel(new GridLayout(3,2,4,4));  
      jp.add(new JLabel("<html><b>Support Libraries:</b></html>"));         
      jp.add(new JLabel(""));         
      jp.add(makeLink("JFreeChart 1.0.3", "http://jfree.org/"));
      jp.add(makeLink("JCommon 1.0.8", "http://jfree.org/"));
      jp.add(makeLink("JCalendar 1.3.2", "http://www.toedter.com/en/jcalendar/"));
      jp.add(makeLink("H2 Database Engine 1.0", "http://www.h2database.com/"));      
      add(jp); 
   }
   
   private HyperLabel makeLink(String title, final String url) {
      final HyperLabel hl = new HyperLabel(title);
      hl.setToolTipText(url);
      hl.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ToolBox.launchURL(hl, url);
         }
      });
      return hl;
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

   public boolean doAccept() {return true;}

}
