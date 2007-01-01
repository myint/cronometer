/*
 * Created on Nov 1, 2006 by davidson
 */
package ca.spaz.gui;


import java.awt.Color;
import java.awt.Dimension;

import javax.swing.*;

public class StatusBar extends JPanel  {
   private JLabel statusLabel, hoverLabel;
   
   public StatusBar() {
      setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      setForeground(Color.LIGHT_GRAY);
      add(Box.createHorizontalStrut(10));
      add(getStatusLabel());
      add(Box.createHorizontalStrut(10));
      add(makeSeparator()); 
      add(Box.createHorizontalStrut(10));
      add(getHoverLabel());
      add(Box.createHorizontalStrut(10));
      add(makeSeparator()); 
      add(Box.createHorizontalStrut(10));
      add(Box.createHorizontalGlue()); 
   }

   private JSeparator makeSeparator() {
      JSeparator sep = new JSeparator(JSeparator.VERTICAL);
      sep.setMaximumSize(new Dimension(3,12));
      return sep;
   }

   private JLabel getStatusLabel() {
      if (statusLabel == null) {
         statusLabel = new JLabel("Status");
         statusLabel.setFont(statusLabel.getFont().deriveFont(10.0f));
         statusLabel.setForeground(Color.DARK_GRAY);
      }
      return statusLabel;
   }
    
   private JLabel getHoverLabel() {
      if (hoverLabel == null) {
         hoverLabel = new JLabel("<hover>");
         hoverLabel.setFont(hoverLabel.getFont().deriveFont(10.0f));
         hoverLabel.setForeground(Color.DARK_GRAY);
      }
      return hoverLabel;
   }
    
   
}
