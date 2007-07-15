/*
 * Created on Jun 30, 2007 by davidson
 */
package ca.spaz.cron.records;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ca.spaz.gui.PrettyTable;
import ca.spaz.gui.PrettyTableModel;

public abstract class RecordTableModel extends PrettyTableModel {
   private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
   
   private List entries = new ArrayList();
    
   public void setUserEntrys(List list) {
      entries = list;
      fireTableDataChanged();
   }
   
   public List getUserEntrys() {
      return entries;
   }
   
   public Class getColumnClass(int col) {
      Object o = getValueAt(0, col);
      if (o != null) {
         return o.getClass();
      }
      return String.class;
   } 

   public Record getUserEntry(int i) {
      if (i<0 || i >= entries.size()) return null;
      return (Record) entries.get(i);
   }

   public int getNumUserEntrys() {
      return entries.size();
   }
   
   public int getRowCount() {
      return entries.size();
   }

   public void delete(Record UserEntry) {
      entries.remove(UserEntry);
      fireTableDataChanged();      
   }

   public void addUserEntry(Record userEntry) {
      entries.add(userEntry);
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
      //Record entry = getUserEntry(row);
      return c;
   } 

}
