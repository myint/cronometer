/*
 * Created on 24-Nov-2005
 */
package ca.spaz.cron.foods;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ca.spaz.gui.PrettyTable;
import ca.spaz.gui.PrettyTableModel;

public class ServingTableModel extends PrettyTableModel {
   public static final int FOOD_COL = 0;
   public static final int AMOUNT_COL = 1;
   public static final int MEASURE_COL = 2;
   public static final int CALORIES_COL = 3;
      
   public static String[] columnNames = { "Food", "Amount", "Measure", "Calories" };


   private static final DecimalFormat kcalf = new DecimalFormat("######0.0");
   private static final DecimalFormat amountf = new DecimalFormat("######0.##");
   
   private List servings = new ArrayList();
   private ServingTable table;
   
   public ServingTableModel(ServingTable table) {
      this.table = table;
   }
   
   public void UpdateTableModel(ServingTable table) {
      this.table = table;
   }

   public void setServings(List list) {
      servings = list;
      fireTableDataChanged();
   }
   
   public List getServings() {
      return servings;
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

   public Serving getServing(int i) {
      if (i<0 || i >= servings.size()) return null;
      return (Serving) servings.get(i);
   }

   public int getNumServings() {
      return servings.size();
   }
   
   public int getRowCount() {
      return servings.size();
   }

   public Object getValueAt(int row, int col) {
      Serving c = getServing(row);
      if (c != null) {
         switch (col) {
            case FOOD_COL:
               return c.getFoodProxy().getDescription();
            case CALORIES_COL:
               double kcals = c.getFood().getCalories();
               return kcalf.format(kcals * c.getGrams()/100.0);
            case AMOUNT_COL:
               return amountf.format(c.getAmount());
            case MEASURE_COL:
               return c.getMeasure();
         }
      }
      return "";
   }

   public String getToolTipText(int row, int col) {
      Serving c = getServing(row);
      if (c != null) {
         return "<html><table width=\"250\"><tr><td align=\"center\">" +
            amountf.format(c.getAmount()) + " " +c.getMeasure()+  " of " +
            c.getFoodProxy().getDescription() +
            "</td></tr></table></html>";
      }
      return "";
   }
   
   public boolean isCellEditable(int row, int col) {
      if (col == AMOUNT_COL) return true;
      if (col == MEASURE_COL) return true;
      if (col == CALORIES_COL) return true;
      return false;
   }

   public void setValueAt(Object value, int row, int col) {
      if (row < 0 || row >= getRowCount()) return;
      Serving s = getServing(row);
      if (value == null || s == null) return;
      if (col == AMOUNT_COL) {
         try {
            double val = Double.parseDouble((String)value);
            s.setAmount(val);
         } catch (NumberFormatException e) {}
      } else if (col == MEASURE_COL) {
         s.setMeasure((Measure)value);
      } else if (col == CALORIES_COL) {
         try {
            // if enter calories, computes proper amount
            double val = Double.parseDouble((String)value);
            double kcals = s.getFood().getCalories();
            double grams = val / (kcals/100.0);
            s.setGrams(grams);
         } catch (NumberFormatException e) {}
      }        
      s.update();
      fireTableRowsUpdated(row, row);         
      table.fireStateChangedEvent();
   }

   public void delete(Serving s) {
      servings.remove(s);
      fireTableDataChanged();      
   }

   public void addServing(Serving s) {
      servings.add(s);
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
      Serving s = getServing(row);
      if (s != null) {
         if (s.getFoodProxy().isDeprecated()) {
            c.setForeground(Color.LIGHT_GRAY);               
         } else if (col == 0) {
            c.setForeground(s.getFoodProxy().getSource().getDisplayColor());               
         } else {
            c.setForeground(Color.BLACK);
         }
      }
      return c;
   }

}
