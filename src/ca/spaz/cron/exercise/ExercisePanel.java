/*
 * Created on Feb 25, 2007 by davidson
 */
package ca.spaz.cron.exercise;

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;

public class ExercisePanel extends JPanel {

   public ExercisePanel() {
      setLayout(new BorderLayout(4,4));
      setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
      JLabel label = new JLabel(
            "<html><div align=\"center\">Just a teaser!<br>" +
            "This feature is planned for a future version....</div></html>",
            JLabel.CENTER);
      label.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));      
      add(label,  BorderLayout.CENTER);
   }
   
} 
