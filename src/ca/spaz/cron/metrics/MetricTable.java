/*
 * Created on 24-Nov-2005
 */
package ca.spaz.cron.metrics;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.records.RecordTable;
import ca.spaz.cron.user.UserManager;

public class MetricTable extends RecordTable {
   
   public MetricTable() {
      super(new MetricTableModel());
      setTitle("BioMarkers");
   }

   public void doAddNewEntry() {
      AddMetricDialog md = new AddMetricDialog(JOptionPane.getFrameForComponent(this));
      md.display(true);
      md.getListPanel().requestFocus();

      Metric metric = md.getSelectedMetric();
      if (metric != null) {
         metric.setDate(CRONOMETER.getInstance().getDailySummary().getDate());
         addMetric(metric);
         fireUserEntryChosen(metric);
      }      
   }
   
   private JComponent makeJScrollPane() {
      JScrollPane jsp = new JScrollPane(getTable());
      jsp.setPreferredSize(new Dimension(400, 250));
      jsp.getViewport().setBackground(Color.WHITE);
      jsp.setBorder(BorderFactory.createEtchedBorder());
      return jsp;
   }

   protected void initTable() {
      table.setColumnSelectionAllowed(false);
      table.getSelectionModel().setSelectionMode(
            ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
      table.getTableHeader().setReorderingAllowed(false);

      table.getColumnModel().getColumn(MetricTableModel.TIME_COL).setMinWidth(60);
      table.getColumnModel().getColumn(MetricTableModel.BIOMARKER_COL).setMinWidth(100);
      table.getColumnModel().getColumn(MetricTableModel.VALUE_COL).setMaxWidth(60);
      table.getColumnModel().getColumn(MetricTableModel.UNITS_COL).setMinWidth(60);

      TableColumnModel tcm = table.getColumnModel();
      // Center the time column
      DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
      renderer.setHorizontalAlignment(SwingConstants.CENTER);
      tcm.getColumn(MetricTableModel.TIME_COL).setCellRenderer(renderer);
      // Right align value column         
      renderer = new DefaultTableCellRenderer();
      renderer.setHorizontalAlignment(SwingConstants.RIGHT);
      tcm.getColumn(MetricTableModel.VALUE_COL).setCellRenderer(renderer);
   } 

   public void setMetrics(List metrics) {
      getMetricTableModel().setMetrics(metrics); 
      fireStateChangedEvent();
   }

   public List getMetrics() {
      return getMetricTableModel().getMetrics();
   }

   public void addMetric(Metric metric) {
      model.addUserEntry(metric);
      UserManager.getCurrentUser().getBiometricsHistory().addMetric(metric);
      fireStateChangedEvent();
   }
   
   public MetricTableModel getMetricTableModel() {
      return (MetricTableModel)model;
   }
}
