/*
 * Created on 23-April-2005
 */
package ca.spaz.gui;

import java.awt.*;

import javax.swing.Icon;
import javax.swing.JLabel;

public class TranslucentLabel extends JLabel {
   double transparency = 1;
   
   public TranslucentLabel(double transparency, String title) {
      super(title);
      this.transparency = transparency;
      setOpaque(false);
   }
   
   public TranslucentLabel(double transparency, String title, int alignment) {
      super(title, alignment);
      this.transparency = transparency;
      setOpaque(false);
   }
   
   
   public TranslucentLabel(double transparency, Icon icon) {
      super(icon);
      this.transparency = transparency;
      setOpaque(false);
   }
   
   public void setTransparency(double val) {
      this.transparency = val;
   }

   public void paint(Graphics g) {
      Graphics2D g2d = (Graphics2D)g;
      g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);
      
      Composite c = g2d.getComposite();
      g2d.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, (float)transparency));
      g2d.setColor(getBackground());
      g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
      g2d.setComposite(c);
      super.paint(g);
   }
   
}
