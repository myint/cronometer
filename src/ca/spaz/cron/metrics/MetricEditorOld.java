/*
 * Created on 19-Nov-2005
 */
package ca.spaz.cron.metrics;

import java.awt.event.*;
import java.util.Iterator;

import javax.swing.*;

import org.jfree.ui.RefineryUtilities;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.user.UserManager;
import ca.spaz.gui.DoubleField;
import ca.spaz.gui.ErrorReporter;
import ca.spaz.util.ImageFactory;

/**
 * A panel for editing a biomarker metric.  
 */
public class MetricEditorOld extends JPanel {
   private Biomarker biomarker;
   private JCheckBox toggle;
   private JLabel label;
   private DoubleField entryField;
   private String metricType;
   private Metric curMetric;
   private JButton saveBtn;
   private JButton deleteBtn;   
   private JButton plotBtn;

   private BiomarkerPanelOld bmp;
//
//   public MetricEditor(BiomarkerPanelOld bmp, String type) {
//      this.metricType = type;
//      this.bmp = bmp;
//   }   

   public MetricEditorOld(BiomarkerPanelOld bmp, Biomarker biomarker) {
      this.metricType = biomarker.getName();
      this.biomarker = biomarker;
      this.bmp = bmp;
   } 
   
   public JButton getSaveButton() {
      if (saveBtn == null) {
         saveBtn = new JButton("Save");
         saveBtn.setEnabled(false);         
         saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               saveValue();
            }
         });
      }
      return saveBtn;
   }  

   public JButton getDeleteButton() {
      if (deleteBtn == null) {
         deleteBtn = new JButton("Delete");
         deleteBtn.setEnabled(false);           
         deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               Metric metric = getMetric();
               // Set the value to null so if we re-save it later it will be considered 
               // new and will be added
               metric.setValue((Number)null);
               UserManager.getCurrentUser().removeMetric(metric);
               saveBtn.setEnabled(false);
               deleteBtn.setEnabled(false);
               entryField.setValue("");
            }
         });
      }
      return deleteBtn;
   }   

   public JLabel getLabel() {
      if (label == null) {
         label = new JLabel(metricType);
      }
      return label;
   }    

   public JButton getPlotButton() {
      if (plotBtn == null) {
         ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/graph.gif"));
         plotBtn = new JButton("Chart", icon);
         plotBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               plotMetric();
            }
         });
      }
      return plotBtn;
   }   

   public void plotMetric() {
      try {
         MetricChart chart = new MetricChart(metricType);
         chart.setBiomarker(biomarker);
         chart.pack();
         chart.setIconImage(CRONOMETER.getWindowIcon());
         RefineryUtilities.centerFrameOnScreen(chart);
         chart.setVisible(true);
      } catch (Exception e) {
         ErrorReporter.showError(e, this);
      }
   }

   public Metric getMetric() {
      if (curMetric == null) {
         curMetric = new Metric(metricType, bmp.getDate());
      }
      return curMetric;
   }

   public DoubleField getEntryField() {
      if (entryField == null) {         
         entryField = new DoubleField(0,8); 
         entryField.setColumns(8);
         entryField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
               entryField.selectAll();   
               saveBtn.setEnabled(true);  
            }
            public void focusLost(FocusEvent e) {
               saveBtn.setEnabled(true);
            }
         });             
         entryField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               saveValue();
            }
         });  
      }
      return entryField;
   }

   public void setMetrics(java.util.List metrics) {
      saveBtn.setEnabled(false);
      deleteBtn.setEnabled(false); 
      entryField.setValue("");       
      // loop through array and find an appropriate metric to install
      Iterator iter = metrics.iterator();
      while (iter.hasNext()) {
         Metric m = (Metric)iter.next();
         if (m.getName().equals(metricType) && m.getValue() != null) {
            curMetric = m;
            if (m.getValue().doubleValue() == 0.0) {
               entryField.setValue("");
            }
            else {
               entryField.setValue(Double.parseDouble(m.getValue().toString()));
               saveBtn.setEnabled(false);
               deleteBtn.setEnabled(true); 
            }          
            return;
         }
      }
      curMetric = null;     
   }

   private void saveValue() {
      if (entryField.getValue() == 0.0) {
         ErrorReporter.showError("0.0 is not a valid biomarker value", bmp);
      }
      else {
         Metric metric = getMetric();
         boolean isNewEntry = metric.getValue() == null;
         metric.setValue(Double.toString(entryField.getValue()));          
         if (isNewEntry) {
            UserManager.getCurrentUser().addMetric(metric);
         }
         else {
            UserManager.getCurrentUser().updateMetric(metric);
         }
         saveBtn.setEnabled(false);
         deleteBtn.setEnabled(true);
      }
   }
}
