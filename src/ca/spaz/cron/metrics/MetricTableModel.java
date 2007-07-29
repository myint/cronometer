package ca.spaz.cron.metrics;

import java.text.*;
import java.util.List;

import ca.spaz.cron.user.UserManager;
import ca.spaz.cron.records.RecordTableModel;

/**
 * MetricTableModel is the model for displaying a table of biomarker measurements.
 */
public class MetricTableModel extends RecordTableModel {
   public static final int TIME_COL = 0;
   public static final int BIOMARKER_COL = 1;
   public static final int VALUE_COL = 2;
   public static final int UNITS_COL = 3;
      
   public static String[] columnNames = { "Time", "Biomarker", "Value", "Units" };

   private static final DecimalFormat kcalf = new DecimalFormat("######0.0");
   private static final DecimalFormat amountf = new DecimalFormat("######0.##");
   private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
      
   public void setMetrics(List list) {
      setUserEntrys(list);
   }
   
   public List getMetrics() {
      return getUserEntrys();
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
      return (Metric)getUserEntry(i);
   }

   public int getNumMetrics() {
      return getUserEntrys().size();
   }
   
   public int getRowCount() {
      return getUserEntrys().size();
   }

   public Object getValueAt(int row, int col) {
      Metric metric = getMetric(row);
      if (metric != null) {
         switch (col) {
            case TIME_COL:
               return timeFormat.format(metric.getDate());
            case UNITS_COL:
               return (UserManager.getCurrentUser().getBiomarkerDefinitions().getBiomarker(metric.getName()).getUnits());
            case BIOMARKER_COL:
               return metric.getName();
            case VALUE_COL:
               return metric.getValue();
         }
      }
      return "";
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
            metric.setValue((Double)value);
         } catch (NumberFormatException e) {}
      }
      fireTableRowsUpdated(row, row);
   }

   public String getToolTipText(int r, int c) {
      return null;
   }

}
