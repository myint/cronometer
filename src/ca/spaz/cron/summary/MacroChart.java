/*
 * Created on 24-Apr-2005
 */
package ca.spaz.cron.summary;

import java.awt.*;

import javax.swing.JComponent;

public class MacroChart extends JComponent {

   private double protein;
   private double carbs;
   private double fat;
   
   public double getCarbs() {
      return carbs;
   }
   
   public void setCarbs(double carbs) {
      this.carbs = carbs;
      repaint();
   }
   
   public double getFat() {
      return fat;
   }
   
   public void setFat(double fat) {
      this.fat = fat;
      repaint();
   }
   
   public double getProtein() {
      return protein;
   }
   
   public void setProtein(double protein) {
      this.protein = protein;
      repaint();
   }
   

   public void paint(Graphics g) {
      Graphics2D g2d = (Graphics2D)g;
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      double total = protein + carbs + fat;
      int w = getWidth();
      int h = getHeight();
      int min = w<h?w:h;
      g.setColor(Color.BLACK);
   //   g.fillArc(0,0,w,h, 0, 360);
      
      g.setColor(Color.GREEN);
      int amount = 0;
      g.fillArc(2,2,min-4,min-4, amount, (int)(360*(protein/total)));
      amount += (int)(360*(protein/total));
      
      g.setColor(Color.BLUE);
      g.fillArc(2,2,min-4,min-4, amount, (int)(360*(carbs/total)));
      amount += (int)(360*(carbs/total));

      g.setColor(Color.RED);
      g.fillArc(2,2,min-4,min-4, amount, (int)(360*(fat/total))); 
      
      g.setColor(Color.GREEN);
      g.drawString(Integer.toString((int)(100*(protein/total))), 5, h/2);
   }
   

}
