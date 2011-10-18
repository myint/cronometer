/*
 * Created on 11-May-2005
 */
package ca.spaz.cron.foods;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import ca.spaz.cron.user.UserManager;
import ca.spaz.gui.PrettyTable;
import ca.spaz.gui.PrettyTableModel;

public class NutrientTableModel extends PrettyTableModel {
   
   private static final int NAME_COLUMN = 0;
   private static final int AMOUNT_COLUMN = 1;
   private static final int UNIT_COLUMN = 2;
   private static final int RDI_COLUMN = 3;

   private String[] columnNames = { "Nutrient", "Amount", "Units", "%DV" };

   private List nutrients;

   private Food food;   
   private double multiplier = 1;
   
   public NutrientTableModel(List nutrientInfo) {
      this.nutrients = UserManager.getCurrentUser().getTracked(nutrientInfo);
   }
   
   public void setMultiplier(double val) {
      this.multiplier = val;
      fireTableDataChanged();
   }
   
   public void setFood(Food f) {
      this.food = f;
      fireTableDataChanged();
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

   public NutrientInfo getNutrientInfo(int i) {
      return (NutrientInfo) nutrients.get(i);
   }

   public int getRowCount() {
      return nutrients.size();
   }
   public Object getValueAt(int row, int col) {
      NutrientInfo ni = getNutrientInfo(row);
      if (ni != null && food != null) {
         double val = multiplier * food.getNutrientAmount(ni);
         switch (col) {
            case NAME_COLUMN:
               if (ni.getParent() != null) {
                  return "   "+ni.getName();
               } else {
                  return ni.getName();
               }
            case AMOUNT_COLUMN:
               val = Math.round(100000 * val) / 100000.0;
               return new Double(val);
            case UNIT_COLUMN:
               return ni.getUnits();
            case RDI_COLUMN:
               double RDI = ni.getReferenceDailyIntake();
               if (RDI > 0) {
                  return new Integer((int)(100*val/RDI));
               }
               return null;
         }
      }
      return "";
   }

   public boolean isCellEditable(int row, int col) {
      NutrientInfo ni = getNutrientInfo(row);
      if (food != null && ni != null) {
         if (col == AMOUNT_COLUMN) {
            return true;
         }
         if (col == RDI_COLUMN) {
            if (ni.getReferenceDailyIntake() > 0) {
               return true;
            }
         }
      }
      return false;
   }

   public void setValueAt(Object value, int row, int col) {
      if (col == AMOUNT_COLUMN && value != null) {
         NutrientInfo ni = getNutrientInfo(row);
         if (ni != null) {
            double val = ((Double) value).doubleValue();
            if (multiplier != 0) {
               food.setNutrientAmount(ni, val / multiplier);
            }
            fireTableRowsUpdated(row, row);
         }
      }
      
      if (col == RDI_COLUMN && value != null) {
         NutrientInfo ni = getNutrientInfo(row);
         if (ni != null) {
            double val = ((Integer) value).intValue();
            double RDI = ni.getReferenceDailyIntake();
            val = (val/100.0) * RDI;
            if (multiplier != 0) {
               food.setNutrientAmount(ni, val / multiplier);
            }
            fireTableRowsUpdated(row, row);
         }
      }
   }

   public String getToolTipText(int r, int c) { 
      return null;
   }

   public void sort() { 
      // no sorting for this table
   }

   public Component customRender(Component c, PrettyTable table, int row, int col) {
      NutrientInfo ni = getNutrientInfo(row);
      if (ni != null) {
         c.setForeground(Color.BLACK);
         if (!food.hasDataFor(ni)) {
            c.setForeground(Color.GRAY);
         }
      }
      return c;
   }
   
}
