/*
 * Created on 2-May-2006
 */
package ca.spaz.gui;

import java.awt.Component;

import javax.swing.table.AbstractTableModel;

public abstract class PrettyTableModel extends AbstractTableModel {
   private int sortOnColumn = -1;
   private boolean ascending = false;
   private boolean allowSorting = false;
   
   public abstract void sort();

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
   
   public void setAllowSorting(boolean val) {
      allowSorting = val;
   }
   
   public boolean getAllowSorting() {
      return allowSorting;
   }
   
   public void sort(int col) {
      if (getSortOnColumn() == col) {
         setAscending(!isAscending());
      } else {
         setAscending(isAscending());
      }
      setSortOnColumn(col);
      sort();      
   }  
   
   public boolean isAscending() {
      return ascending;
   }

   public void setAscending(boolean ascending) {
      this.ascending = ascending;
   }

   public int getSortOnColumn() {
      return sortOnColumn;
   }

   public void setSortOnColumn(int sortOnColumn) {
      this.sortOnColumn = sortOnColumn;
   }
   
}
