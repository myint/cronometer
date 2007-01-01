/*
 * Created on 27-Jan-2006
 */
package ca.spaz.cron.chart;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import ca.spaz.cron.datasource.Datasources;
import ca.spaz.cron.user.Metric;

 
public class TimeSeriesTest extends JFrame {
   private String metricName;
   
    /**
     * A demonstration application showing how to create a simple time series 
     * chart.  This example uses monthly data.
     *
     * @param title  the frame title.
     */
    public TimeSeriesTest(String metricName) {
        super(metricName + " plot");
        this.metricName = metricName;
        JFreeChart chart = createChart(createDataset());
        ChartPanel chartPanel = new ChartPanel(chart); 
        chartPanel.setPreferredSize(new java.awt.Dimension(550, 300));
        chartPanel.setMouseZoomable(true, false);
        chartPanel.setDisplayToolTips(true);
        setContentPane(chartPanel);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
    
 
    /**
     * Creates a chart.
     * 
     * @param dataset  a dataset.
     * 
     * @return A chart.
     */
    private JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            metricName+" Plot",  // title
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
        
        return chart;

    }
    
    /**
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return The dataset.
     */
    private XYDataset createDataset() {
        TimeSeries s1 = new TimeSeries(metricName, Day.class);

        List metrics = Datasources.getBiometricsHistory().getMetricsOfType(metricName);
        for (int i=0; i<metrics.size(); i++) {
           Metric m = (Metric)metrics.get(i);
           s1.add(new Day(m.getDate()), m.getValue());
       }
      
        
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        //dataset.setDomainIsPointsInTime(true);
        
        return dataset;

    }
  
}
