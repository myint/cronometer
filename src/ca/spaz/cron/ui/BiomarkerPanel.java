/*
 * Created on 12-Aug-2005
 */
package ca.spaz.cron.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.*;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.chart.TimeSeriesTest;
import ca.spaz.cron.datasource.Datasources;
import ca.spaz.cron.user.*;
import ca.spaz.gui.ErrorReporter;

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
      biomarkers = Datasources.getBiomarkerDefinitions().getEnabledBiomarkers();
      // Create an editor for each enabled biomarker
      editors = new ca.spaz.cron.user.MetricEditor[biomarkers.size()];
      for (int i = 0; i < editors.length; i++) {
         Biomarker biomarker = (Biomarker)biomarkers.get(i);
         editors[i] = new ca.spaz.cron.user.MetricEditor(this, biomarker);
      }

      setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
      setLayout(new BorderLayout());
      JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getMetricTable(), getChartPanel());
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
   
   ///////////////////////////////////////////////////////////////////////
   // Just a teaser idea?
   
   private ChartPanel chartPanel;
   private JPanel getChartPanel() {
      if (chartPanel == null) {
         
         TimeSeries actualData = new TimeSeries("Weight", Day.class);
         TimeSeriesCollection dataset = new TimeSeriesCollection();
         List metrics = Datasources.getBiometricsHistory().getMetricsOfType("Weight");
         // Sort by date
         Collections.sort(metrics);
         for (int i=0; i<metrics.size(); i++) {
            Metric m = (Metric)metrics.get(i);
            actualData.add(new Day(m.getDate()), m.getValue());
         }
         dataset.addSeries(actualData);
         
         JFreeChart chart;
         chart = ChartFactory.createTimeSeriesChart(
               "Biometrics Chart",  // title
               "Date",             // x-axis label
               "Weight",           // y-axis label
               dataset,            // data
               true,               // create legend?
               true,               // generate tooltips?
               false               // generate URLs?
         );

         chart.setBackgroundPaint(Color.white);
         XYPlot plot = (XYPlot) chart.getPlot();
         plot.setBackgroundPaint(Color.lightGray);
         plot.setDomainGridlinePaint(Color.white);
         plot.setRangeGridlinePaint(Color.white);
         plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
         plot.setDomainCrosshairVisible(true);
         plot.setRangeCrosshairVisible(true);

         XYItemRenderer r = plot.getRenderer();
         if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
         }

         DateAxis axis = (DateAxis) plot.getDomainAxis();
         axis.setDateFormatOverride(new SimpleDateFormat("dd-MMM-yyyy"));
         chartPanel = new ChartPanel(chart); 
         chartPanel.setPreferredSize(new java.awt.Dimension(300, 200));
         chartPanel.setMouseZoomable(true, false);
         chartPanel.setDisplayToolTips(true);
      }
      return chartPanel;
   }
}
