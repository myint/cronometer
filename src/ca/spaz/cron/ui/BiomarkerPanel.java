/*
 * Created on 12-Aug-2005
 */
package ca.spaz.cron.ui;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ca.spaz.cron.user.*;

/**
 * A panel containing a MetricEditor for each enabled Biomarker.
 */
public class BiomarkerPanel extends JPanel {
   private Date curDate = new Date();
   private List curMetrics;
   private ca.spaz.cron.user.MetricEditor[] editors;
   private List biomarkers = new ArrayList();
   private MetricTable metricTable;

   public BiomarkerPanel() {
      biomarkers = new BiomarkerDefinitions().getEnabledBiomarkers();
      // Create an editor for each enabled biomarker
      editors = new ca.spaz.cron.user.MetricEditor[biomarkers.size()];
      for (int i = 0; i < editors.length; i++) {
         Biomarker biomarker = (Biomarker)biomarkers.get(i);
         editors[i] = new ca.spaz.cron.user.MetricEditor(this, biomarker);
      }

      setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
      setLayout(new BorderLayout());
      JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getMetricTable(), new JPanel());
      splitPane.setDividerLocation(300);
      splitPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));      
      add(splitPane, BorderLayout.CENTER);
   }
   
//   private JSplitPane getDietPanel() {
//      if (null == dietPanel) {
//         dietPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
//               getMetricTable(), new JPanel());
//         dietPanel.setDividerLocation(300);
//         dietPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3)); 
//      }
//      return dietPanel;
//   }
   
   public MetricTable getMetricTable() {
      if (null == metricTable) {
         metricTable = new MetricTable();
         metricTable.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
//               List servings = metricTable.getSelectedServings();
//               if (servings.size() == 0) {
//                  servings = metricTable.getServings();
//               }
//               totals.setServings(servings);               
            }           
         });               
      }
      return metricTable;
   }
   

   public void setDate(Date d) {
      this.curDate = d;
      curMetrics = null;
      getMetrics();
      metricTable.setMetrics(curMetrics);
//      for (int i=0; i<editors.length; i++) {
//         editors[i].setMetrics(getMetrics());
//      }
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
