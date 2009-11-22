package ca.spaz.cron.exercise;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ca.spaz.gui.PrettyTable;
import ca.spaz.gui.PrettyTableModel;

public class ExerciseTableModel extends PrettyTableModel {
   public static final int NAME_COL = 0;
   public static final int TIME_COL = 1;
   public static final int CALORIES_COL = 2;
      
   public static String[] columnNames = { "Exercise", "Minutes", "Calories" };


   private static final DecimalFormat kcalf = new DecimalFormat("######0.0");
   private static final DecimalFormat amountf = new DecimalFormat("######0.##");
   
   private List exercises = new ArrayList();
   private ExerciseTable table;
   
   public ExerciseTableModel(ExerciseTable table) {
      this.table = table;
   }
   
   public void UpdateTableModel(ExerciseTable table) {
      this.table = table;
   }

   public void setExercises(List list) {
      exercises = list;
      fireTableDataChanged();
   }
   
   public List getExercises() {
      return exercises;
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

   public Exercise getExercise(int i) {
      if (i<0 || i >= exercises.size()) return null;
      return (Exercise) exercises.get(i);
   }

   public int getNumExercises() {
      return exercises.size();
   }
   
   public int getRowCount() {
      return exercises.size();
   }

   public Object getValueAt(int row, int col) {
      Exercise e = getExercise(row);
      if (e != null) {
         switch (col) {
            case NAME_COL:
               return e.getName();
            case CALORIES_COL:
               double kcals = e.getCalories();
               return kcalf.format(kcals);
            case TIME_COL:
               return amountf.format(e.getMinutes());
         }
      }
      return "";
   }

   public String getToolTipText(int row, int col) {
      Exercise e = getExercise(row);
      if (e != null) {
         return "<html><table width=\"250\"><tr><td align=\"center\">" +
            amountf.format(e.getMinutes()) + " minutes of " +
            e.getName() +
            "</td></tr></table></html>";
      }
      return "";
   }
   
   public boolean isCellEditable(int row, int col) {
      if (col == NAME_COL) return true;
      if (col == TIME_COL) return true;
      if (col == CALORIES_COL) return true;
      return false;
   }

   public void setValueAt(Object value, int row, int col) {
      if (row < 0 || row >= getRowCount()) return;
      Exercise s = getExercise(row);
      if (value == null || s == null) return;
      if (col == TIME_COL) {
         try {
            double val = Double.parseDouble((String)value);
            s.setMinutes(val);
         } catch (NumberFormatException e) {}
      } else if (col == NAME_COL) {
         s.setName((String) value);
      } else if (col == CALORIES_COL) {
         try {
            // if enter calories, computes proper amount
            double val = Double.parseDouble((String)value);
            s.setCalories(val);
         } catch (NumberFormatException e) {}
      }        
      s.update();
      fireTableRowsUpdated(row, row);         
      table.fireStateChangedEvent();
   }

   public void delete(Exercise s) {
      exercises.remove(s);
      fireTableDataChanged();      
   }

   public void addExercise(Exercise s) {
      exercises.add(s);
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
      Exercise s = getExercise(row);
      if (s != null) {
         c.setForeground(Color.BLACK);
      }
      return c;
   }

}
