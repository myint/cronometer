/*
 * Created on 4-Jun-2005
 */
package ca.spaz.cron.targets;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.JOptionPane;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.foods.NutrientInfo;
import ca.spaz.cron.user.User;
import ca.spaz.gui.PrettyTable;
import ca.spaz.gui.PrettyTableModel;

/** 
 */
public class TargetEditorTableModel extends PrettyTableModel {

   private static final int TRACK_COLUMN = 0;
   private static final int NAME_COLUMN = 1;
   private static final int TARGET_MIN = 2;
   private static final int TARGET_MAX = 3;   
   private static final int UNIT_COLUMN = 4;

   private static final String[] columnNames = { "Track", "Nutrient", "Minimum", "Maximum", "Units"};
   private static final int[] columnMaxWidth = { 40, 100, 60, 60, 60 };
   
   private List nutrients;
   private User user;

   /**
    * @param user the user whos targets we're displaying/editing
    * @param nutrientInfo list of nutrients to display targets for
    */
   public TargetEditorTableModel(User user, List nutrientInfo) {
      this.user = user;
      this.nutrients = nutrientInfo;
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

   public int getColumnMaxWidth(int col) {
      return columnMaxWidth[col];
   }

   
   public NutrientInfo getNutrientInfo(int i) {
      return (NutrientInfo) nutrients.get(i);
   }

   public int getRowCount() {
      return nutrients.size();
   }

   
   public Object getValueAt(int row, int col) {
      NutrientInfo ni = getNutrientInfo(row);
      if (ni != null) {
         Target target = user.getTarget(ni);
         switch (col) {
            case TRACK_COLUMN:
               return new Boolean(user.isTracking(ni));
            case NAME_COLUMN:
               return (ni.getParent() != null) ? ("   "+ni.getName()) : ni.getName();
            case TARGET_MIN:
               return new Double(target.getMin());
            case TARGET_MAX:
               return new Double(target.getMax());
            case UNIT_COLUMN:
               return ni.getUnits();
         }
      }
      return "";
   }

   public boolean isCellEditable(int row, int col) {
      NutrientInfo ni = getNutrientInfo(row);
      if (ni != null) {
         if (col == TRACK_COLUMN || col == TARGET_MIN || col == TARGET_MAX) {
            return true;
         }
      }     
      return false;
   }

   public void setValueAt(Object value, int row, int col) {
      NutrientInfo ni = getNutrientInfo(row);
      if (ni == null || value == null) return;      
      if (col == TARGET_MIN && value != null) {        
         double val = ((Double) value).doubleValue();
         Target target = user.getTarget(ni);
         target.setMin(val);
         user.setTarget(ni, target);     
         fireTableCellUpdated(row, col); 
         user.setCustomTargets(true);
      } else if (col == TARGET_MAX && value != null) {         
         double val = ((Double) value).doubleValue();
         Target target = user.getTarget(ni);
         target.setMax(val);
         user.setTarget(ni, target);         
         fireTableCellUpdated(row, col); 
         user.setCustomTargets(true);
      } else if (col == TRACK_COLUMN && value != null) {
         boolean val = ((Boolean)value).booleanValue();
         if (val && (ni.getUSDA() == null || ni.isSparseData())) {
            val = promptForSparseData(ni);
         }
         user.setTracking(ni, val);
         fireTableRowsUpdated(row,row);    
      }       
   }
 
   /**
    * @return true if edit should be aborted because of warning
    */
   private boolean promptForSparseData(NutrientInfo ni) {
      String data = (ni.getUSDA() == null) ? " does not have any data" : " has limited data available" ;  
      int rc = JOptionPane.showConfirmDialog(CRONOMETER.mainFrame, 
           ni.getName() + data + " in the USDA food database.\n" +           
           "Are you sure you want to track it?",
           "Track " + ni.getName()+"?",
           JOptionPane.YES_NO_OPTION);  
      return rc == JOptionPane.YES_OPTION;
   }

   public Component customRender(Component c, PrettyTable table, int row, int col) {
      c.setForeground(Color.BLACK);
      NutrientInfo ni = getNutrientInfo(row);
      if (ni != null) {
         if (!user.isTracking(ni)) {
            c.setForeground(Color.GRAY);
         }         
      }
      return c;
   }
   
   public String getToolTipText(int r, int c) {
      return null;
   }

   public void sort() {
   }

}
