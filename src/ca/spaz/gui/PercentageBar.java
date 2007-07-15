/*
 * Created on 1-Jan-2006
 */
package ca.spaz.gui;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.border.Border;

public class PercentageBar extends JComponent {
   private double value;
   private float alpha = 0.65f;
    
   public PercentageBar() {
      setForeground(new Color(50,120,160));
      setPreferredSize(new Dimension(50, 8));
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
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      g.setColor(getBackground());
      g.fillRect(xo,yo,w,h);
      
      g.setColor(getForeground());      
      int bar = (int)Math.round(w*value);
      if (bar > 1) {
         if (bar > w) {
            bar = w;
            g.setColor(getForeground().brighter());           
         }
         g.fill3DRect(xo,yo,bar,h-1,true);         
      }
         
      g.setColor(Color.BLACK);       
   }

}
