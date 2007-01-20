/*
 * Created on 12-Aug-2005
 */
package ca.spaz.cron.ui;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import ca.spaz.cron.user.*;

/**
 * A panel containing a MetricEditor for each enabled Biomarker.
 */
public class BiomarkerPanel extends JPanel {
   private Date curDate = new Date();
   private List curMetrics;
   private MetricEditor[] editors;
   private List biomarkers = new ArrayList();

   public BiomarkerPanel() {
      biomarkers = new BiomarkerDefinitions().getEnabledBiomarkers();
      // Create an editor for each enabled biomarker
      editors = new MetricEditor[biomarkers.size()];
      for (int i = 0; i < editors.length; i++) {
         Biomarker biomarker = (Biomarker)biomarkers.get(i);
         editors[i] = new MetricEditor(this, biomarker);
      }

      JPanel fp = new JPanel(new GridLayout(4, 4));      
      fp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));  
      JPanel ed = new JPanel(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0.0;
      c.insets = new Insets(8,8,8,8);   	   
      for (int i=0; i<editors.length; i++) {   	   
         c.gridx = 0;
         c.gridy = i;
         ed.add(editors[i].getLabel(), c);
         c.gridx = 1;
         c.gridy = i;	   
         ed.add(editors[i].getEntryField(), c);
         c.gridx = 2;
         c.gridy = i;	   
         ed.add(editors[i].getSaveButton(), c);
         c.gridx = 3;
         c.gridy = i;	   
         ed.add(editors[i].getDeleteButton(), c);   		   
         c.gridx = 4;
         c.gridy = i;	   
         ed.add(editors[i].getPlotButton(), c);	   
      }
      JPanel x = new JPanel(new BorderLayout());
      x.add(ed, BorderLayout.NORTH);
      //setBorder(BorderFactory.createEtchedBorder());
      setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
      setLayout(new BorderLayout(4, 4));
      add(x, BorderLayout.WEST);
   }

   public void setDate(Date d) {
      this.curDate = d;
      curMetrics = null;
      for (int i=0; i<editors.length; i++) {
         editors[i].setMetrics(getMetrics());
      }
   }

   public Date getDate() {
      return curDate;
   }

   private List getMetrics() {
      if (curMetrics == null) {
         curMetrics = User.getUser().getBiometrics(curDate);
      }
      return curMetrics;
   }
}
