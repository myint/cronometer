/*
 * Created on 4-Jun-2005
 */
package ca.spaz.cron.targets;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.*;
import javax.swing.table.TableColumn;

import ca.spaz.cron.user.User;
import ca.spaz.gui.PrettyTable;

/** 
 * @author Aaron Davidson
 */
public class TargetEditorTable extends JScrollPane {
 
   TargetEditorTableModel model;
   PrettyTable nutrientTable;
   
   public TargetEditorTable(User user, List nutrients) {
      model = new TargetEditorTableModel(user, nutrients);
      setViewportView(getTable());
      getViewport().setBackground(Color.WHITE);
      setPreferredSize(new Dimension(350, 200));
   }

   private JTable getTable() {
      if (null == nutrientTable) {
         nutrientTable = new PrettyTable(model); 
         nutrientTable.getSelectionModel().setSelectionMode(
               ListSelectionModel.SINGLE_SELECTION); 
         nutrientTable.getTableHeader().setReorderingAllowed(false);
         for (int i=0; i<model.getColumnCount(); i++) {            
            TableColumn tc = nutrientTable.getTableHeader().getColumnModel().getColumn(i);
            tc.setPreferredWidth(model.getColumnMaxWidth(i));
         }         
      }
      return nutrientTable;
   }
   
   public void fireTargetsChanged() {
      model.fireTableDataChanged();
   }

}
