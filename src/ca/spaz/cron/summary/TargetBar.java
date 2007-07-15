/*
 * Created on 1-Jan-2006
 */
package ca.spaz.cron.summary;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * Displays a visual representation of the nutritional target.
 * 
 * @author adavidson
 */
public class TargetBar extends JComponent {
   private double value;
   private double max;
   private double min;
   
   public TargetBar() {
      setForeground(new Color(150,150,250));
      setPreferredSize(new Dimension(100, 20));
   }
   
   public TargetBar(double minVal, double maxVal) {
      this.min = minVal;
      this.max = maxVal;
      setForeground(Color.ORANGE);
      setPreferredSize(new Dimension(100, 20));
   }
   
   public double getMax() {
      return max;
   }
   
   public void setMax(double max) {
      this.max = max;
   }
   
   public double getMin() {
      return min;
   }
   
   public void setMin(double min) {
      this.min = min;
   }
   
   public double getValue() {
      return value;
   }
   
   public void setValue(double value) {
      this.value = value;
   }
   
   public void paint(Graphics g) {
      int w = getWidth();
      int h = getHeight();
      int xo = 0;
      int yo = 0;
      Border border = getBorder();
      if (border != null) {
         Insets insets = border.getBorderInsets(this);
         w -= insets.left + insets.right;
         h -= insets.top + insets.bottom;
         xo = insets.left;
         yo = insets.top;
      }
      
      Graphics2D g2d = (Graphics2D)g;      
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.80f));
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      g.setColor(getBackground());
      g.fill3DRect(xo,yo,w,h,false);
      
      if (min <= 0) {
         g.setFont(g.getFont().deriveFont(Font.ITALIC));
         g.setColor(Color.BLACK);
         if (value > max) {
            g.setColor(Color.YELLOW);
         }
         String str = "unspecified target";
         g.drawString(str,
               xo+(w-g.getFontMetrics().stringWidth(str))/2, 
               (yo-1) + (h-1 + g.getFontMetrics().getAscent())/2);
         return;
      }
      
      g.setColor(getForeground());      
      int bar = (int)Math.round(w*value/min);
      if (bar > 1) {
         if (bar > w) {
            bar = w;
            g.setColor(mixColor(Color.RED,getForeground(), value/max));            
         }
         g.fill3DRect(xo,yo,bar,h-1,true);         
      }
         
      g.setColor(Color.BLACK);
      if (value > max) {
         g.setColor(Color.YELLOW);
      }
      String str = (int)Math.round(100*value/min) +"%";
      g.setFont(g.getFont().deriveFont(Font.BOLD));
      g.drawString(str,
            xo+(w-g.getFontMetrics().stringWidth(str))/2, 
            (yo-1) + (h-1 + g.getFontMetrics().getAscent())/2);
   }

   private Color mixColor(Color a, Color b, double val) { 
      if (val > 1) val = 1;
      if (val < 0) val = 0;
      return new Color(
            (int)(a.getRed()*val + b.getRed()*(1.0-val)),
            (int)(a.getGreen()*val + b.getGreen()*(1.0-val)),
            (int)(a.getBlue()*val + b.getBlue()*(1.0-val)) );
   }

}
