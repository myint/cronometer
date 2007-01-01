/*
 * Created on 11-May-2005
 */
package ca.spaz.cron.ui;

import java.awt.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.*;

import ca.spaz.cron.foods.Food;
import ca.spaz.gui.PrettyTable;

public class NutrientEditorTable extends JScrollPane {

   NutrientTableModel model;
   PrettyTable nutrientTable;
   
   public NutrientEditorTable(List nutrients) {
      model = new NutrientTableModel(nutrients);
      setViewportView(getNutrientTable());
      getViewport().setBackground(Color.WHITE);
      setPreferredSize(new Dimension(350, 185));     
   }
   
   public void setMultiplier(double val) {
      model.setMultiplier(val);
   }
   
   public void setFood(Food f) {
      model.setFood(f);
   }
   
   private JTable getNutrientTable() {
      if (null == nutrientTable) {
         nutrientTable = new PrettyTable();
         nutrientTable.setModel(model);
         nutrientTable.getSelectionModel().setSelectionMode(
               ListSelectionModel.SINGLE_SELECTION);
         nutrientTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
         nutrientTable.getTableHeader().setReorderingAllowed(false);
        /* nutrientTable.getSelectionModel().addListSelectionListener(
               new ListSelectionListener() {
                  public void valueChanged(ListSelectionEvent e) {
                     if (e.getValueIsAdjusting())
                        return;
                     ListSelectionModel lsm = (ListSelectionModel) e
                           .getSource();
                     if (!lsm.isSelectionEmpty()) {
                        int selectedRow = lsm.getMinSelectionIndex();
                     }
                  }
               });*/
         // right align last column
         TableColumnModel tcm = nutrientTable.getColumnModel();
         TableColumn column = tcm.getColumn(0);
         column.setMinWidth(140);
      }
      return nutrientTable;
   }

   public void setEditable(boolean val) {
      nutrientTable.setEnabled(val);      
   }



}
