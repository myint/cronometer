/*
 * Created on 12-Aug-2005
 */
package ca.spaz.cron.ui;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import ca.spaz.cron.user.*;

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
      
      JPanel lp = new JPanel(new GridLayout(editors.length,2,8,8));
      for (int i=0; i<editors.length; i++) {
         lp.add(editors[i].getToggle());
      }
      
      JPanel rp = new JPanel(new GridLayout(editors.length,2,8,8));
      for (int i=0; i<editors.length; i++) {
         rp.add(editors[i].getSpinner());
      }
      
      JPanel ep = new JPanel(new GridLayout(editors.length,2,8,8));
      for (int i=0; i<editors.length; i++) {
         ep.add(editors[i].getPlotButton());
      }
      
      JPanel fp = new JPanel(new BorderLayout(4, 4));      
      fp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
      fp.add(lp, BorderLayout.WEST);
      fp.add(rp, BorderLayout.CENTER);
      fp.add(ep, BorderLayout.EAST);

      setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
      setLayout(new BorderLayout(4, 4));
      add(fp, BorderLayout.NORTH);
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
