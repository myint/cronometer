/*
 * Created on 7-Jan-2006
 */
package ca.spaz.cron;

import java.awt.BorderLayout;

import javax.swing.*;

import ca.spaz.gui.WrappedPanel;
import ca.spaz.gui.WrapperDialog;
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
         + "</table>" 

         + "<b>Libraries</b> "
         + "<table width=\"475\"> "
         + "<tr><td>JFreeChart</td><td>1.0.3</td><td>http://jfreechart.org/</td><td>LGPL</td></tr>"
         + "<tr><td>JCommon</td>   <td>1.0.8</td><td>http://jfreechart.org/</td><td>LGPL</td></tr>"
         + "<tr><td>JCalendar</td> <td>1.3.2</td><td>http://www.toedter.com/en/jcalendar/</td><td>LGPL</td></tr>"
         + "<tr><td>Design Grid Layout</td> <td>0.1.1</td><td>https://designgridlayout.dev.java.net/</td><td>LGPL</td></tr>"
         + "</table>" 


         + "<b>Datasets</b> "
         + "<table width=\"475\"> "
         + "<tr><td>USDA National Nutrient Database for Standard Reference</td><td>sr19</td><td>http://www.ars.usda.gov/Services/docs.htm?docid=8964</td><td>Public Domain</td></tr>"
         + "</table>" 
         
         + "</div></html>",
         JLabel.CENTER);
      setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      setLayout(new BorderLayout(5,5));
      add(info, BorderLayout.NORTH);
       
      /*DesignGridLayout layout = new DesignGridLayout( this );
      this.setLayout( layout );*/
      
    /*  layout.row().add( new JLabel("<html><b>Credits</b></html>"));  
      layout.row().add( new JLabel("Aaron Davidson")).add(new JLabel("aaron@spaz.ca")).add(new JLabel("Programming"));
      layout.row().add( new JLabel("Chris Rose")).add(new JLabel("chris@offlineblog.com")).add(new JLabel("Programming"));
      layout.row().add( new JLabel("Gerald Turnquist")).add(new JLabel("geraldt@sasktel.net")).add(new JLabel("Programming"));
      layout.row().add( new JLabel("Michael Rae")).add(new JLabel("chris@offlineblog.com")).add(new JLabel("Consulting"));
      layout.row().add( new JLabel("Antonio Zamora")).add(new JLabel("zamora@scientificpsychic.com")).add(new JLabel("Technical Writing"));
      layout.row().add( new JLabel("<html><b>Libraries</b></html>"));  
      layout.row().add( new JLabel("JFreeChart")).add(new JLabel("1.0.3")).add(new JLabel("http://jfreechart.org/")).add(new JLabel("LGPL"));
      layout.row().add( new JLabel("JCommon 1.0.8")).add(new JLabel("http://jfreechart.org/")).add(new JLabel("LGPL"));
      layout.row().add( new JLabel("JCalendar 1.3.2")).add(new JLabel("http://www.toedter.com/en/jcalendar/")).add(new JLabel("LGPL"));
      layout.row().add( new JLabel("Design Grid Layout 0.1.1")).add(new JLabel("https://designgridlayout.dev.java.net/")).add(new JLabel("LGPL"));
      layout.row().add( new JLabel("Design Grid Layout 0.1.1")).add(new JLabel("https://designgridlayout.dev.java.net/")).add(new JLabel("LGPL"));*/
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
