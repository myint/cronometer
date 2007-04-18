/*
 * Created on 18-Jul-2005
 */
package ca.spaz.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.table.*;

import org.jdesktop.swingx.JXTable;

public class PrettyTable extends JXTable implements TableCellRenderer {
   public static final Color SHADED = new Color(240, 245, 255);
   private TableCellRenderer oldRender;
   private boolean allowHorizScroll = false;

   public PrettyTable(PrettyTableModel model) {
      super(model);
      init();
   } 
   
   public PrettyTable() {
      super();
      init();
   }
   
   public String getToolTipText(MouseEvent e) {
      PrettyTableModel ptm = getPrettyTableModel();
      if (ptm != null) {
         return ptm.getToolTipText(rowAtPoint(e.getPoint()),columnAtPoint(e.getPoint()));
      }
      return null;
   }
   
   private void init() { 
      super.setSortable(false); // disable JXTabl sorter for now
      setTerminateEditOnFocusLost(true);
      getTableHeader().setReorderingAllowed(false);
      getTableHeader().addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            JTableHeader h = (JTableHeader) e.getSource();
            TableColumnModel columnModel = h.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int column = columnModel.getColumn(viewColumn).getModelIndex();
            if (column != -1 && getPrettyTableModel().getAllowSorting()) {
               getPrettyTableModel().sort(column);
               getTableHeader().repaint();
            }            
        }
      });    
      
      oldRender = getTableHeader().getDefaultRenderer();
      getTableHeader().setDefaultRenderer(this);      
      setShowGrid(false);
   }
 
   public PrettyTableModel getPrettyTableModel() {      
      return (PrettyTableModel)getModel();
   }
     
   public Component prepareRenderer(TableCellRenderer r, int row, int col) {
      Component c = super.prepareRenderer(r, row, col);      
      if (row % 2 == 0) {
         c.setBackground(Color.WHITE);
      } else {
         c.setBackground(SHADED);
      }
      if (getSelectionModel().isSelectedIndex(row)) {
         c.setBackground(c.getBackground().darker());
      }
      PrettyTableModel ptm = getPrettyTableModel();
      if (ptm != null) {
         c = ptm.customRender(c, this, row, col);
      }
      return c;
   }
   
   public Component getTableCellRendererComponent(JTable table, Object value,
         boolean isSelected, boolean hasFocus, int row, int column) {
     
      Component c = oldRender.getTableCellRendererComponent(table,
            value, isSelected, hasFocus, row, column);
     
      if (c instanceof JLabel) {
         JLabel l = (JLabel) c;
         l.setHorizontalTextPosition(JLabel.LEFT);
         int modelColumn = table.convertColumnIndexToModel(column);
         if (modelColumn == getPrettyTableModel().getSortOnColumn()) {
            l.setIcon(new Arrow(getPrettyTableModel().isAscending(), l.getFont().getSize()));
         } else {
            l.setIcon(null);
         }
      }
      
      return c;
   }
   
   public static class Arrow implements Icon {
      private boolean descending;
      private int size;
   
      public Arrow(boolean descending, int size) {
          this.descending = descending;
          this.size = size;
      }
   
      public void paintIcon(Component c, Graphics g, int x, int y) {
          Color color = c == null ? Color.GRAY : c.getBackground();             
          int dx = (size/2);
          int dy = descending ? dx : -dx;
          // Align icon (roughly) with font baseline. 
          y = y + 5*size/6 + (descending ? -dy : 0);
          int shift = descending ? 1 : -1;
          g.translate(x, y);
   
          // Right diagonal. 
          g.setColor(color.darker());
          g.drawLine(dx / 2, dy, 0, 0);
          g.drawLine(dx / 2, dy + shift, 0, shift);
          
          // Left diagonal. 
          g.setColor(color.brighter());
          g.drawLine(dx / 2, dy, dx, 0);
          g.drawLine(dx / 2, dy + shift, dx, shift);
          
          // Horizontal line. 
          if (descending) { g.setColor(color.darker().darker()); } 
          else { g.setColor(color.brighter().brighter()); }
          g.drawLine(dx, 0, 0, 0);
   
          g.setColor(color);
          g.translate(-x, -y);
      }
   
      public int getIconWidth() {
          return size;
      }
   
      public int getIconHeight() {
          return size;
      }
   }  
   
   /**
    * Returns false to indicate that horizontal scrollbars are required
    * to display the table while honoring perferred column widths. Returns
    * true if the table can be displayed in viewport without horizontal
    * scrollbars.
    * 
    * @return true if an auto-resizing mode is enabled 
    *   and the viewport width is larger than the table's 
    *   preferred size, otherwise return false.
    * @see Scrollable#getScrollableTracksViewportWidth
    */
   public boolean getScrollableTracksViewportWidth() {      
      if (allowHorizScroll) {
         if (autoResizeMode != AUTO_RESIZE_OFF) {
            if (getParent() instanceof JViewport) {
               return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
            }
         }    
         return false;
      } else {
         return super.getScrollableTracksViewportWidth();
      }
   }

   public boolean allowsHorizontalScrollbar() {
      return allowHorizScroll;
   }

   public void setAllowsHorizontalScrollbar(boolean allowHorizScroll) {
      this.allowHorizScroll = allowHorizScroll;
   }
   
}
