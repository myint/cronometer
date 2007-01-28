package ca.spaz.cron.ui;

import java.awt.Component;
import java.text.*;
import java.util.ArrayList;
import java.util.List;

import ca.spaz.cron.datasource.Datasources;
import ca.spaz.cron.user.Metric;
import ca.spaz.gui.PrettyTable;
import ca.spaz.gui.PrettyTableModel;

/**
 * MetricTableModel is the model for displaying a table of biomarker measurements.
 * @author Gerald
 */
public class MetricTableModel extends PrettyTableModel {
   public static final int TIME_COL = 0;
   public static final int BIOMARKER_COL = 1;
   public static final int VALUE_COL = 2;
   public static final int UNITS_COL = 3;
      
   public static String[] columnNames = { "Time", "Biomarker", "Value", "Units" };


   private static final DecimalFormat kcalf = new DecimalFormat("######0.0");
   private static final DecimalFormat amountf = new DecimalFormat("######0.##");
   private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
   
   private List metrics = new ArrayList();
   private MetricTable table;
   
   public MetricTableModel(MetricTable table) {
      this.table = table;
   }
   
   public void setMetrics(List list) {
      metrics = list;
      fireTableDataChanged();
   }
   
   public List getMetrics() {
      return metrics;
   }
   
   public Class getColumnClass(int col) {
      Object o = getValueAt(0, col);
      if (o != null) {
         return o.getClass();
      }
      return String.class;
   }

   public int getColumnCount() {
      return columnNames.length;
   }

   public String getColumnName(int col) {
      return columnNames[col].toString();
   }

   public Metric getMetric(int i) {
      if (i<0 || i >= metrics.size()) return null;
      return (Metric) metrics.get(i);
   }

   public int getNumMetrics() {
      return metrics.size();
   }
   
   public int getRowCount() {
      return metrics.size();
   }

   public Object getValueAt(int row, int col) {
      Metric metric = getMetric(row);
      if (metric != null) {
         switch (col) {
            case TIME_COL:
               return timeFormat.format(metric.getDate());
            case UNITS_COL:
               return (Datasources.getBiomarkerDefinitions().getBiomarker(metric.getName()).getUnits());
            case BIOMARKER_COL:
               return metric.getName();
            case VALUE_COL:
               return metric.getValue();
         }
      }
      return "";
   }

   public String getToolTipText(int row, int col) {
      // No additional useful information for a metric
      return null;
   }
   
   public boolean isCellEditable(int row, int col) {
      if (col == VALUE_COL) return true;
      return false;
   }

   public void setValueAt(Object value, int row, int col) {
      if (row < 0 || row >= getRowCount()) return;
      Metric metric = getMetric(row);
      if (value == null || metric == null) return;
      if (col == VALUE_COL) {
         try {
//            double val = Double.parseDouble((String)value);
            metric.setValue((String)value);
         } catch (NumberFormatException e) {}
      }        
//      metric.update();
      fireTableRowsUpdated(row, row);         
      table.fireStateChangedEvent();
   }

   public void delete(Metric metric) {
      metrics.remove(metric);
      fireTableDataChanged();      
   }

   public void addMetric(Metric metric) {
      metrics.add(metric);
      fireTableDataChanged();      
   }

   public void sort() {
      // no sorting in this table for now.
   }
   
   /**
    * Allows custom rendering for a row and column. Can just return c, if no
    * changes to default are desired.
    * @param c the component used for rendering the cell
    * @param row the row to render
    * @param col the column to render
    * @return a custom rendering component
    */
   public Component customRender(Component c, PrettyTable table, int row, int col) {
      Metric metric = getMetric(row);
//      if (s != null) {
//         if (s.getFoodProxy().isDeprecated()) {
//            c.setForeground(Color.LIGHT_GRAY);               
//         } else if (col == 0) {
//            c.setForeground(s.getFoodProxy().getSource().getDisplayColor());               
//         } else {
//            c.setForeground(Color.BLACK);
//         }
//      }
      return c;
   }

}
