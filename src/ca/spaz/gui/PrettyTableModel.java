/*
 * Created on 2-May-2006
 */
package ca.spaz.gui;

import java.awt.Component;

import javax.swing.table.AbstractTableModel;

public abstract class PrettyTableModel extends AbstractTableModel {

   public abstract void sort(PrettyTable table);

   public abstract String getToolTipText(int r, int c);
 
   /**
    * Allows custom rendering for a row and column. Can just return c, if no
    * changes to default are desired.
    * @param c the component used for rendering the cell
    * @param row the row to render
    * @param col the column to render
    * @return a custom rendering component
    */
   public Component customRender(Component c, PrettyTable table, int row, int col) {
      return c;
   }
   
}
