/*
 * Created on 10-May-2005
 */
package ca.spaz.gui;

import java.awt.*;

import javax.swing.JToolBar;

public class TranslucentToolBar extends JToolBar {
   double transparency = 1;
   
   public TranslucentToolBar(double transparency) {
      super();
      this.transparency = transparency;
      setFloatable(false);
      setRollover(true);
      setOpaque(false);
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
