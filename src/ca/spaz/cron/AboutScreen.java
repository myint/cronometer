/*
 * Created on 7-Jan-2006
 */
package ca.spaz.cron;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import zappini.designgridlayout.DesignGridLayout;
import zappini.designgridlayout.Row;
import ca.spaz.gui.*;
import ca.spaz.util.ImageFactory;

public class AboutScreen extends WrappedPanel {

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

        /* + "<br><b>Libraries</b> "
         + "<table width=\"475\"> "
         + "<tr><td><a href=\"http://jfreechart.org/\">JFreeChart</a></td><td>1.0.3</td><td>http://jfreechart.org/</td><td>LGPL</td></tr>"
         + "<tr><td>JCommon</td>   <td>1.0.8</td><td>http://jfreechart.org/</td><td>LGPL</td></tr>"
         + "<tr><td>JCalendar</td> <td>1.3.2</td><td>http://www.toedter.com/en/jcalendar/</td><td>LGPL</td></tr>"
         + "<tr><td>Design Grid Layout</td> <td>0.1.1</td><td>https://designgridlayout.dev.java.net/</td><td>LGPL</td></tr>"
         + "</table>" 


         + "<br><b>Datasets</b> "
         + "<table width=\"475\"> "
         + "<tr><td>USDA National Nutrient Database for Standard Reference</td><td>sr19</td><td>http://www.ars.usda.gov/Services/docs.htm?docid=8964</td><td>Public Domain</td></tr>"
         + "</table>" */
         
         + "</html>",
         JLabel.CENTER);
      setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      setLayout(new BorderLayout(5,5));
      add(info, BorderLayout.NORTH);
       
      JPanel jp = new JPanel();
      DesignGridLayout layout = new DesignGridLayout( jp );
      jp.setLayout( layout );
      add(jp, BorderLayout.CENTER);
      
      
      layout.row().add(new JSeparator());
      Row row = layout.row();
      row.add( new JLabel("<html><b>Food Datasources: </b></html>"));
      row = layout.row();
      row.add( makeLink("USDA National Nutrient Database for Standard Reference sr19", "http://www.ars.usda.gov/Services/docs.htm?docid=8964"));
             
      layout.row().add(new JSeparator());
      row = layout.row();
      row.add( new JLabel("<html><b>Support Libraries:</b></html>"));      
      row = layout.row();
      row.add( makeLink("JFreeChart 1.0.3", "http://jfreechart.org/"));
      row.add(makeLink("JCommon 1.0.8", "http://jfreechart.org/"));
      row.add( makeLink("JCalendar 1.3.2", "http://www.toedter.com/en/jcalendar/"));
      row.add( makeLink("Design Grid Layout 0.1.1", "https://designgridlayout.dev.java.net/"));
      layout.row().add(new JSeparator());

   }
   
   private HyperLabel makeLink(String title, final String url) {
      final HyperLabel hl = new HyperLabel(title);
      hl.setToolTipText(url);
      hl.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            CRONOMETER.launchURL(hl, url);
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

   public void doAccept() {}

}
