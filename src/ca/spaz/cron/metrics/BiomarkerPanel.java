/*
 * Created on 12-Aug-2005
 */
package ca.spaz.cron.metrics;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

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

import ca.spaz.cron.user.UserManager;

/**
 * A panel containing a MetricEditor for each enabled Biomarker.
 */
public class BiomarkerPanel extends JPanel {
   private Date curDate = new Date();
   private List curMetrics;
   private ca.spaz.cron.metrics.MetricEditor[] editors;
   private List biomarkers = new ArrayList();
   private MetricTable metricTable;
   private JSplitPane splitPane;
   private ChartPanel chartPanel;
   
   public BiomarkerPanel() {
      biomarkers = UserManager.getCurrentUser().getBiomarkerDefinitions().getEnabledBiomarkers();
      setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
      setLayout(new BorderLayout());
      add(getSplitPane(), BorderLayout.CENTER);
   }
   
   private JSplitPane getSplitPane() {
      if (splitPane == null) {
         splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
               getMetricTable(), getChartPanel());
         splitPane.setDividerLocation(300);
         splitPane.setBorder(BorderFactory.createEmptyBorder(3,3,3,3)); 
      }
      return splitPane;
   }
   
   public MetricTable getMetricTable() {
      if (null == metricTable) {
         metricTable = new MetricTable();
         metricTable.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
               // TODO: redraw graph?
               regenerateGraphData();
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
   }

   public Date getDate() {
      return curDate;
   }

   private List getMetrics() {
      if (curMetrics == null) {
         curMetrics = UserManager.getCurrentUser().getBiometrics(curDate);
      }
      return curMetrics;
   }
    
   ///////////////////////////////////////////////////////////////////////////////////////////
   
   
   private TimeSeries actualData = new TimeSeries("Weight", Minute.class);
   
   private void regenerateGraphData() {
      List metrics = UserManager.getCurrentUser().getBiometricsHistory().getMetricsOfType("Weight");
      // Sort by date
      Collections.sort(metrics);
      actualData.clear();
      for (int i=0; i<metrics.size(); i++) {
         Metric m = (Metric)metrics.get(i);
         actualData.addOrUpdate(new Day(m.getDate()), m.getValue());
      }
   }
   
   private JPanel getChartPanel() {
      if (chartPanel == null) {
         
         regenerateGraphData();
         
         TimeSeriesCollection dataset = new TimeSeriesCollection();         
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
         chartPanel.setPreferredSize(new Dimension(300, 200));
         chartPanel.setMouseZoomable(true, false);
         chartPanel.setDisplayToolTips(true);
         chartPanel.setMinimumSize(new Dimension(300, 200));
      }
      return chartPanel;
   }
}
