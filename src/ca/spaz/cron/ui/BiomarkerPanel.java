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
 * A panel containing a MetricEditor for each biomarker.
 */
public class BiomarkerPanel extends JPanel {
   private Date curDate = new Date();
   private List curMetrics;
   private MetricEditor[] editors;

   public BiomarkerPanel() {

      editors = new MetricEditor[6];
      editors[0] = new MetricEditor(this, Metric.WEIGHT);
      editors[1] = new MetricEditor(this, Metric.BODY_TEMPERATURE);
      editors[2] = new MetricEditor(this, Metric.SYSTOLIC_BP);
      editors[3] = new MetricEditor(this, Metric.DIASTOLIC_BP);
      editors[4] = new MetricEditor(this, Metric.RESTING_HEART_RATE);
      editors[5] = new MetricEditor(this, Metric.BLOOD_GLUCOSE);

      JPanel fp = new JPanel(new GridLayout(4, 4));      
      fp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));  
      // Note: the grid bag layout is all done here rather than in Metric Editor so that the
      // column alignment works properly across the entire grid.
      JPanel ed = new JPanel(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.HORIZONTAL;
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
         c.weightx = 0.9;   		   
         c.gridx = 5;
         c.gridy = i;	   
         ed.add(new JPanel(), c);   		   
      }
      JPanel x = new JPanel(new BorderLayout());
      x.add(ed, BorderLayout.NORTH);
      //setBorder(BorderFactory.createEtchedBorder());
      setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
      setLayout(new BorderLayout(4, 4));
      add(x, BorderLayout.CENTER);
   }


   private Metric getWeightMetric() {
      Iterator iter = getMetrics().iterator();
      while (iter.hasNext()) {
         Metric m = (Metric)iter.next();
         if (m.isWeight()) {
            return m;
         }
      }
      Metric wm = new Metric(Metric.WEIGHT, curDate);
      User.getUser().addMetric(wm);
      if (!getMetrics().contains(wm)) {
         getMetrics().add(wm);
      }
      return wm;
   }

   public void setDate(Date d) {
      if (!validateMetrics()) return;
      this.curDate = d;
      curMetrics = null;
      for (int i=0; i<editors.length; i++) {
         editors[i].setMetrics(getMetrics());
      }
   }

   private boolean validateMetrics() {
      for (int i=0; i<editors.length; i++) {
         if (!editors[i].validateMetric()) {
            Toolkit.getDefaultToolkit().beep();
            editors[i].requestFocusInWindow();
            return false;
         }
      }
      return true;
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
