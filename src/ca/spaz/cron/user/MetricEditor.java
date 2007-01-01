/*
 * Created on 19-Nov-2005
 */
package ca.spaz.cron.user;

import java.awt.event.*;
import java.text.ParseException;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import org.jfree.ui.RefineryUtilities;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.chart.TimeSeriesTest;
import ca.spaz.cron.ui.BiomarkerPanel;
import ca.spaz.util.ImageFactory;

public class MetricEditor extends JPanel {
   private JCheckBox toggle;
   private JSpinner spinner;
   private String metricType;
   private Metric curMetric;
   private JButton plotBtn;

   private BiomarkerPanel bmp;
   
   public MetricEditor(BiomarkerPanel bmp, String type) {
      this.metricType = type;
      this.bmp = bmp;
   }   
   
   public JCheckBox getToggle() {
      if (toggle == null) {
         toggle = new JCheckBox(metricType);
         toggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               getSpinner().setVisible(toggle.isSelected());
               if (toggle.isSelected()) {
                  User.getUser().addMetric(getMetric());
               } else {
                  User.getUser().removeMetric(getMetric());
               }
            }
         });
      }
      return toggle;
   }   
   
   public JButton getPlotButton() {
      if (plotBtn == null) {
         ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/Graph24.png"));
         plotBtn = new JButton(icon);
         plotBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               plotMetric();
            }
         });
      }
      return plotBtn;
   }   
   
   
   public void plotMetric() {
      TimeSeriesTest demo = new TimeSeriesTest(metricType);
      demo.pack();
      demo.setIconImage(CRONOMETER.getWindowIcon());
      RefineryUtilities.centerFrameOnScreen(demo);
      demo.setVisible(true);
   }
   
   public Metric getMetric() {
      if (curMetric == null) {
         validateMetric();
         curMetric = new Metric(metricType, bmp.getDate());
      }
      return curMetric;
   }
   
   private SpinnerNumberModel getSpinnerModel() {
      if (metricType.equals(Metric.WEIGHT)) {
         return new SpinnerNumberModel(150,0,1000,0.5);
      }
      if (metricType.equals(Metric.BODY_TEMPERATURE)) {
         return new SpinnerNumberModel(37,0.0,1000.0,0.1);
      }
      return new SpinnerNumberModel(0,0,1000000,0.1);
   }
   
   public JSpinner getSpinner() {
      if (spinner == null) {         
         spinner = new JSpinner(getSpinnerModel());
         spinner.setVisible(false);
         spinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
               if (spinner.isEnabled()) {
                  getMetric().setValue((Number)spinner.getValue());
               }
            }
         });               
      }
      return spinner;
   }

   public void setMetrics(List metrics) {
      // loop through array and find an appropriate metric to install
      Iterator iter = metrics.iterator();
      while (iter.hasNext()) {
         Metric m = (Metric)iter.next();
         if (m.getName().equals(metricType) && m.getValue() != null) {
            curMetric = m;
            spinner.setValue(m.getValue());
            getToggle().setSelected(true);
            getSpinner().setVisible(toggle.isSelected());
            return;
         }
      }
      // no matches found, so disable the control
      curMetric = null;
      getToggle().setSelected(false);
      getSpinner().setVisible(toggle.isSelected());
   }
   
   public boolean validateMetric() {
      try {
         getSpinner().commitEdit();
         return true;
      } catch (ParseException e) {
         e.printStackTrace();
         return false;
      }
   }
   
}
