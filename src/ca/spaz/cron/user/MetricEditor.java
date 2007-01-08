/*
 * Created on 19-Nov-2005
 */
package ca.spaz.cron.user;

import java.awt.event.*;
import java.util.Iterator;

import javax.swing.*;

import org.jfree.ui.RefineryUtilities;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.chart.TimeSeriesTest;
import ca.spaz.cron.ui.BiomarkerPanel;
import ca.spaz.gui.DoubleField;
import ca.spaz.gui.ErrorReporter;
import ca.spaz.util.ImageFactory;

/**
 * A panel for editing a biomarker metric.  
 */
public class MetricEditor extends JPanel {
   private JCheckBox toggle;
   private JLabel label;
   private DoubleField entryField;
   private String metricType;
   private Metric curMetric;
   private JButton saveBtn;
   private JButton deleteBtn;   
   private JButton plotBtn;

   private BiomarkerPanel bmp;

   public MetricEditor(BiomarkerPanel bmp, String type) {
      this.metricType = type;
      this.bmp = bmp;
   }   

   public JButton getSaveButton() {
      if (saveBtn == null) {
         saveBtn = new JButton("Save");
         saveBtn.setEnabled(false);         
         saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (entryField.getValue() == 0.0) {
                  ErrorReporter.showError("0.0 is not a valid biomarker value (unless you are deceased)", bmp);
               }
               else {
                  getMetric().setValue(Double.toString(entryField.getValue()));     
                  User.getUser().addMetric(getMetric());
                  saveBtn.setEnabled(false);
                  deleteBtn.setEnabled(true);
               }
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
               User.getUser().removeMetric(getMetric());
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

   public DoubleField getEntryField() {
      if (entryField == null) {         
         entryField = new DoubleField(0,8); 
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

            }
         });  
      }
      return entryField;
   }

   public void setMetrics(java.util.List metrics) {
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
            }
            saveBtn.setEnabled(false);
            deleteBtn.setEnabled(true);            
            return;
         }
      }
      // no matches found, so disable the control
      curMetric = null;
   }

   public boolean validateMetric() {
      return true;
//    try {
//    getEntryField().commitEdit();
//    return true;
//    } catch (ParseException e) {
//    e.printStackTrace();
//    return false;
//    }
   }

}
