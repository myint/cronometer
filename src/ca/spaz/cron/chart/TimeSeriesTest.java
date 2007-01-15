/*
 * Created on 27-Jan-2006
 */
package ca.spaz.cron.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.*;
import org.jfree.ui.RectangleInsets;

import ca.spaz.cron.datasource.Datasources;
import ca.spaz.cron.ui.DateChooser;
import ca.spaz.cron.user.Metric;


public class TimeSeriesTest extends JFrame {
   private JPanel mainPanel;
   private String metricName;
   private JPanel toolbar;
   private TimeSeriesCollection dataset;
   private TimeSeries s1;
   private TimeSeries s2;   
   private JFreeChart chart;
   private ChartPanel chartPanel;

   private Date curDate = new Date();
   private JButton startDateBtn;
   private Date startDate = new Date();
   private JButton endDateBtn;
   private Date endDate = new Date();
   private DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);   

   /**
    * A demonstration application showing how to create a simple time series 
    * chart.  This example uses monthly data.
    *
    * @param title  the frame title.
    */
   public TimeSeriesTest(String metricName) {
      super(metricName);
      this.metricName = metricName;

      mainPanel = new JPanel(new BorderLayout());
      createDataset();
      createChart();
      chartPanel = createChartPanel();

      mainPanel.add(getToolbar(), BorderLayout.NORTH);
      mainPanel.add(chartPanel, BorderLayout.CENTER);        
      setContentPane(mainPanel);
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
   }


   private ChartPanel createChartPanel() {
      ChartPanel chartPanel = new ChartPanel(chart); 
      chartPanel.setPreferredSize(new java.awt.Dimension(550, 300));
      chartPanel.setMouseZoomable(true, false);
      chartPanel.setDisplayToolTips(true);
      return chartPanel;
   }

   /**
    * Creates a dataset, consisting of two series of monthly data.
    *
    * @return The dataset.
    */
   private void createDataset() {
      s1 = new TimeSeries(metricName, Day.class);

      getData();

      dataset = new TimeSeriesCollection();
      dataset.addSeries(s1);
      dataset.addSeries(s2);        
      //dataset.setDomainIsPointsInTime(true);
   }


   private void getData() {
      s1.clear();
      List metrics = Datasources.getBiometricsHistory().getMetricsOfType(metricName);
      for (int i=0; i<metrics.size(); i++) {
         Metric m = (Metric)metrics.get(i);
         // If the initial start date has not been changed, assume we want to start from the beginning
         if (i == 0 && startDate.equals(curDate)) {
            startDate = m.getDate();
         }
         if ((startDate.equals(m.getDate()) || startDate.before(m.getDate())) &&
               (endDate.equals(m.getDate()) || endDate.after(m.getDate()))) {
            s1.add(new Day(m.getDate()), m.getValue());
         }
      }
      TimeSeries movingAverage = MovingAverage.createMovingAverage(
            s1, "7 Day Moving Average", 7, 0
      );
      if (s2 == null) {
         s2 = movingAverage;
      }
      else {
         // Copy the new moving average data into the existing TimeSeries
         s2.clear();
         Iterator iter = movingAverage.getItems().iterator();
         while (iter.hasNext()) {
            s2.add((TimeSeriesDataItem)iter.next());
         }
      }
   }

   /**
    * Creates a chart.
    * 
    * @param dataset  a dataset.
    * 
    * @return A chart.
    */
   private void createChart() {
      chart = ChartFactory.createTimeSeriesChart(
            metricName,  // title
            "Date",             // x-axis label
            metricName,   // y-axis label
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

   }



   private JPanel getToolbar() {
      if (toolbar == null) {
         toolbar = new JPanel();
         toolbar.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));

//         toolbar.add(Box.createHorizontalGlue());
         toolbar.add(getStartDateButton()); 
         toolbar.add(new JLabel(" to ")); 
         toolbar.add(getEndDateButton());
//       toolbar.add(Box.createHorizontalGlue());
//       toolbar.add(getFormatBox());
//       toolbar.add(Box.createHorizontalGlue());
//       toolbar.add(getTargetsOnlyBox());
//       toolbar.add(Box.createHorizontalGlue());
//       toolbar.add(getSaveButton()); 
      }
      return toolbar;
   }  

   private JButton getStartDateButton() {
      if (startDateBtn == null) {
         startDateBtn = new JButton(df.format(startDate));
         startDateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               updateStartDate();
            }
         });
      }
      return startDateBtn;
   }

   private void updateStartDate() {
      startDate = pickStartDate(startDate);
      if (startDate.after(curDate)) {
         startDate = curDate;                    
      }      
      startDateBtn.setText(df.format(startDate));      
      if (startDate.after(endDate)) {
         endDate = startDate;
         endDateBtn.setText(df.format(endDate));
      }
      getData();
   }   

   private JButton getEndDateButton() {
      if (endDateBtn == null) {
         endDateBtn = new JButton(df.format(endDate));
         endDateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               updateEndDate();                
            }            
         });
      }
      return endDateBtn;
   }

   private void updateEndDate() {
      endDate = pickEndDate(endDate);
      if (endDate.after(curDate)) {
         endDate = curDate;                    
      }
      endDateBtn.setText(df.format(endDate));      
      if (endDate.before(startDate)) {
         startDate = endDate;
         startDateBtn.setText(df.format(startDate));
      }
      getData();
   }

   public Date pickStartDate(Date startDate) {
      return DateChooser.pickDate(mainPanel, startDate, "Pick a start date");
   }  

   public Date pickEndDate(Date startDate) {
      return DateChooser.pickDate(mainPanel, startDate, "Pick an end date");
   }      
}
