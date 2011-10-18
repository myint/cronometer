/*
 * Created on 24-Apr-2005
 */
package ca.spaz.cron.summary;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.table.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.foods.NutrientInfo;
import ca.spaz.cron.foods.Serving;
import ca.spaz.cron.targets.NutrientInfoPanel;
import ca.spaz.cron.targets.Target;
import ca.spaz.cron.user.UserChangeListener;
import ca.spaz.cron.user.UserManager;
import ca.spaz.gui.*;

public class NutrientTable extends PrettyTable implements UserChangeListener {
   
   private List servings; 
   private List nutrients, master;
   private NutrientTableModel model;
      
   public NutrientTable(List nutrients) {
      super();
      setNutrients(nutrients);
      UserManager.getUserManager().addUserChangeListener(this);
      this.setEnabled(true);
      this.setFocusable(false);
      this.setCellSelectionEnabled(false);
      this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
      this.getTableHeader().setReorderingAllowed(false);
      this.setCellSelectionEnabled(false);
      this.getSelectionModel().setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);
      this.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            if (getSelectedRow() != -1) {               
               if (e.getClickCount() == 2) {
                  NutrientInfo ni = model.getNutrientInfo(getSelectedRow());
                  WrapperDialog.showDialog(CRONOMETER.getInstance(), new NutrientInfoPanel(ni));
                  model.fireTableDataChanged(); 
               }
            }
         }
      });
      
      setModel(getNutrientTableModel());
      
      //setColumnAlignment(0, SwingConstants.RIGHT);
      setColumnAlignment(1, SwingConstants.RIGHT);
      setColumnAlignment(3, SwingConstants.RIGHT);
      getColumnModel().getColumn(0).setMaxWidth(180);
      getColumnModel().getColumn(0).setMinWidth(140);
      getColumnModel().getColumn(1).setMaxWidth(64);
      getColumnModel().getColumn(1).setMinWidth(64);
      getColumnModel().getColumn(2).setMaxWidth(36);
      getColumnModel().getColumn(2).setMinWidth(36);
      
      TableColumn tc = getColumnModel().getColumn(3);
      tc.setCellRenderer(new TargetRenderer());
   }   
   
   private void setNutrients(List list) {
      this.master = list;     
      this.nutrients = UserManager.getCurrentUser().getTracked(list);
      getNutrientTableModel().fireTableDataChanged();
   }

   private void setColumnAlignment(int col, int alignment) {
      TableColumnModel tcm = getColumnModel();
      DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
      TableColumn column = tcm.getColumn(col);
      renderer.setHorizontalAlignment(alignment);
      column.setCellRenderer(renderer);
   }
   
   public double getAmount(NutrientInfo ni) {
      // TODO: cache this value!
      double total = 0;
      if (servings != null) {
         for (Iterator iter = servings.iterator(); iter.hasNext();) {
            Serving serving = (Serving) iter.next();
            double weight = serving.getGrams()/100.0;
            total += weight * serving.getFood().getNutrientAmount(ni);
        }
      }
     return total;
   }

   private NutrientTableModel getNutrientTableModel() {
      if (model == null) {
         model = new NutrientTableModel();
      }
      return model;
   }
   
   public class NutrientTableModel extends PrettyTableModel {
      private DecimalFormat df = new DecimalFormat("######0.0");
      private DecimalFormat nf = new DecimalFormat("######0%");
      private String[] columnNames = { "Nutrient", "Amount", "Unit" , "% Target"};


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
         if (ni != null) {
            switch (col) {
            case 0:
               if (ni.getParent() != null) {
                  return "    "+ni.getName();
               } else {
                  return ni.getName();
               }
            case 1:
               return df.format(getAmount(ni));
            case 2:
               return " " + ni.getUnits();
            case 3: 
               Target target = UserManager.getCurrentUser().getTarget(ni);
               if (target.getMin() > 0) {
                  return nf.format(getAmount(ni)/target.getMin());
               }
            }
         }
         return "";
      }

      public boolean isCellEditable(int row, int col) {
         return false;
      }

      public String getToolTipText(int r, int c) {          
         return null;
      }

      public void sort() {}

   }

   public void update(List consumed) {    
      this.servings = consumed;
      getNutrientTableModel().fireTableDataChanged();
   }

   public class TargetRenderer extends TargetBar implements TableCellRenderer {

      public TargetRenderer() { }

      public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
         setValue(0);
         setMin(0);
         setMax(0); 
         
         NutrientInfo ni = model.getNutrientInfo(row);
         if (ni != null) {
            Target target = UserManager.getCurrentUser().getTarget(ni);
            if (target.getMin() > 0) {
               setValue(getAmount(ni));
               setMin(target.getMin());
               setMax(target.getMax()); 
            } 
         }
         return this;
      }

   }

   public void userChanged(UserManager userMan) {
      setNutrients(master);
      getNutrientTableModel().fireTableDataChanged();
   }

   

}
