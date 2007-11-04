/*
 * Created on 20-Mar-2005
 */
package ca.spaz.cron.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import se.datadosen.component.RiverLayout;
import ca.spaz.cron.actions.*;
import ca.spaz.cron.datasource.*;
import ca.spaz.cron.foods.FoodSelectionListener;
import ca.spaz.gui.*;

/**
 * The search panel allows fast and easy searching of the food database, and
 * potentially multiple database sources.
 * 
 * @todo: Add multiple data sources (ex: web searches, CNF2001b)
 * 
 * @author davidson
 */
public class SearchPanel extends JPanel implements ItemListener {
   public static final String SELECTED_FOOD = "SELECTED_FOOD";
   
   private JComboBox sourceBox;
   private JTextField queryField; 
   private ResultsTableModel model = new ResultsTableModel();
   private PrettyTable resultTable;
   private ArrayList result = new ArrayList();  
   private Action searchAction;
   private FoodProxy selectedFood;
   private Vector listeners;
   private boolean updateAsTyping = true;
   private int maxScore = 1, minScore = 0;;
   
   public SearchPanel() {
      listeners = new Vector();
      setLayout(new BorderLayout(6,6));
      setBorder(new CompoundBorder(
            BorderFactory.createEtchedBorder(),                 
            BorderFactory.createEmptyBorder(8,8,8,8)));
      add(makeQueryPanel(), BorderLayout.NORTH);
      add(makeResultPanel(), BorderLayout.CENTER);      
      queryField.requestFocusInWindow();
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            doDBSearch();            
         }
      });
   }
   
   private JPanel makeQueryPanel() {
      JPanel jp = new JPanel(new RiverLayout(3,3));
      jp.add(RiverLayout.CENTER, new JLabel("Search: "));
      jp.add(RiverLayout.CENTER, getSourceBox());
      jp.add(RiverLayout.HFILL, getQueryField()); 
      return jp;
   }
   
   public JTextField getQueryField() {
      if (null == queryField) {
         queryField = new JTextField();
         queryField.setAction(getSearchAction());
                   
         // for live searches as we type:
         queryField.addKeyListener(new KeyAdapter() {
            public void keyTyped(final KeyEvent e) {
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() { 
                     if (e.getKeyChar() != '\n') {
                        if (updateAsTyping()) {
                           doDBSearch();                           
                        }
                     }
                  }
               });
            }

            public void keyPressed(KeyEvent e) { 
               if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                  if (resultTable.getRowCount() > 0) {
                     resultTable.changeSelection(0, 0, false, false);                  
                     foodSelected(model.getSearchHit(0).getFoodProxy());
                  }
               }
               if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_KP_UP) {
                  arrowUp();
               }
               if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_KP_DOWN) {
                 arrowDown();
               }         
            }
         });
      }
      return queryField;
   }

   private boolean updateAsTyping() {
      return updateAsTyping;
   }
   
   private Action getSearchAction() {
      if (null == searchAction) {
         searchAction = new SearchAction();
      }
      return searchAction;
   }

   private JComboBox getSourceBox() {
      if (sourceBox == null) {
         Vector sources = new Vector();
         sources.add("All");
         sources.addAll(Datasources.getDatasources());
         sourceBox = new JComboBox(sources);
         sourceBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
               if (e.getStateChange() == ItemEvent.SELECTED) {
                  clearResults();
                  doDBSearch();
                  model.fireTableDataChanged();               
                  queryField.requestFocusInWindow();
                  queryField.selectAll();
                  System.out.println("Select All");
               }
            }
         });
         sourceBox.setAction(getSearchAction());
      }
      return sourceBox;
   }

   private JComponent makeResultPanel() {
      resultTable = new PrettyTable(model);       
      resultTable.getSelectionModel().setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);
      resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
      resultTable.getTableHeader().setReorderingAllowed(false);    
      
      PercentageRenderer pr = new PercentageRenderer();
      pr.setBackground(resultTable.getBackground());
      TableColumn tc = resultTable.getColumnModel().getColumn(1);
      tc.setCellRenderer(pr);
      tc.setMaxWidth(50);
      tc.setMinWidth(50);
      
      resultTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
               public void valueChanged(ListSelectionEvent e) {
                  if (e.getValueIsAdjusting()) return;
                  ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                  if (!lsm.isSelectionEmpty()) {
                     int selectedRow = lsm.getMinSelectionIndex();
                     FoodProxy f = model.getSearchHit(selectedRow).getFoodProxy();                    
                     foodSelected(f);
                  }
               }
            });
      resultTable.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            int sel = resultTable.getSelectedRow();
            if (sel != -1) {               
               if (e.getClickCount() == 2) {
                  foodDoubleClicked();
               } else {
                  handleMouseClick(e);
               }
            }
         }
         public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {               
               int index = resultTable.rowAtPoint(e.getPoint());
               if (index >= 0) {
                  resultTable.getSelectionModel().setSelectionInterval(index,index);
                  handleMouseClick(e);
               } 
            }
         }
      });
     
      
      JScrollPane jsp = new JScrollPane(resultTable);
      jsp.setPreferredSize(new Dimension(500, 200));
      jsp.getViewport().setBackground(Color.WHITE);
      jsp.setBorder(BorderFactory.createEtchedBorder());
      return jsp;
   }


   public void arrowUp() {
      if (resultTable.getSelectedRow() > 0) {
         resultTable.changeSelection(resultTable.getSelectedRow()-1, 0, false, false);
      }
   }

   public void arrowDown() {
      if (resultTable.getSelectedRow() < resultTable.getRowCount()-1) {
         resultTable.changeSelection(resultTable.getSelectedRow()+1, 0, false, false);
      }
   }
 
   
   protected void foodSelected(FoodProxy f) { 
      selectedFood = f;
      if (f != null) {
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
            ((FoodSelectionListener)iter.next()).foodSelected(f);
         }
      }
   }

   protected void foodDoubleClicked() {      
      Iterator iter = listeners.iterator();
      while (iter.hasNext()) {
         ((FoodSelectionListener)iter.next()).foodDoubleClicked(selectedFood);
      }
   }
   
   public void addFoodSelectionListener(FoodSelectionListener listener) {
      listeners.add(listener);
   }
   
   public void removeFoodSelectionListener(FoodSelectionListener listener) {
      listeners.remove(listener);      
   }
   

   
   public FoodProxy getSelectedFood() {
      return selectedFood;
   }
   
   public void deselect() {
      resultTable.getSelectionModel().clearSelection();
   }

   /**
    * Depending on the selected source, a different database will be searched
    */
   public void doDBSearch() {
      Object sel = getSourceBox().getSelectedItem();
      if (sel instanceof FoodDataSource) {
         doDBSearch((FoodDataSource)sel);
      } else {
         doDBSearch(null);
      }
   }
   
   private void clearResults() {
      synchronized (result) {
         result.clear(); 
         maxScore = Integer.MIN_VALUE;
         minScore = Integer.MAX_VALUE;
      }
   }

   /**
    * Execute a search query for a food.
    */
   public void doDBSearch(FoodDataSource ds) {      
      String query = getQueryField().getText().trim();
      String[] parts = query.split("\\s");
      clearResults();
      ArrayList foods = new ArrayList();  
      synchronized (result) {
         if (query.length() == 0) {
            if (ds != null) { 
               foods.addAll(ds.getAllFoods());
            } else {
               Iterator iter = Datasources.getDatasources().iterator();
               while (iter.hasNext()) {
                  ds = (FoodDataSource)iter.next(); 
                  foods.addAll(ds.getAllFoods());
               }
            } 
         } else {   
            if (ds != null) {
               foods.addAll(ds.findFoods(parts));
            } else {
               Iterator iter = Datasources.getDatasources().iterator();
               while (iter.hasNext()) {
                  ds = (FoodDataSource)iter.next();
                  foods.addAll(ds.findFoods(parts));
               }
            }
         }     

         // score results:
         Iterator iter = foods.iterator(); 
         while (iter.hasNext()) {
            SearchHit hit = new SearchHit((FoodProxy)iter.next());
            hit.computeScore(parts);
            maxScore = Math.max(maxScore, hit.getScore()); 
            minScore = Math.min(minScore, hit.getScore()); 
            result.add(hit);
         }
         model.sort();
      }
      model.fireTableDataChanged();
      
   }   
 
   public class ResultsTableModel extends PrettyTableModel {
 
      public ResultsTableModel() {
         setAllowSorting(true);
         setAscending(true);
      }
      
      private String[] columnNames = { "Description", "%" };

      public String getColumnName(int col) {
         return columnNames[col].toString();
      }
 
      public int getRowCount() {
         synchronized (result) {
            return result.size();
         }
      }

      public SearchHit getSearchHit(int i) {
         synchronized (result) {
            if (i< 0 || i >= result.size()) return null; 
            return (SearchHit) result.get(i);
         }
      }

      public int getColumnCount() {
         return columnNames.length;
      }

      public Object getValueAt(int row, int col) {
         SearchHit hit = getSearchHit(row);
         if (hit != null) {
            switch (col) {
               case 0:
                  return hit.getFoodProxy().getDescription();
               case 1:
                  return hit;
            }
         }
         return "";
      }

      public Class getColumnClass(int col) {
         Object o = getValueAt(0, col);
         if (o != null) {
            return o.getClass();
         }
         return String.class;
      }

      public boolean isCellEditable(int row, int col) {
         return false;
      }

      public void setValueAt(Object value, int row, int col) {
         fireTableCellUpdated(row, col);
      }

      public String getToolTipText(int r, int c) {
         SearchHit hit = getSearchHit(r);
         if (hit != null) {
            FoodProxy f = hit.getFoodProxy();
            if (f != null) {
               if (c == 0) {
               return "<html><table width=\"220\"><tr><td align=\"center\">" +
                  f.getDescription() +
                  "<br>["+f.getSource()+"]" +
                  (f.isDeprecated() ? "<br><font color=\"red\">This Food is Obsolete</font>" : "") +
                  "</td></tr></table></html>";
               }
               if (c == 1) {
                  return "Score: " + (getSearchHit(r).getScore() - minScore);
               }
            }
         }
         return "";
      }
           
      public void sort() {
         final int dir = isAscending() ? 1 : -1;
         if (getSortOnColumn() == 0) {
            Collections.sort(result, new Comparator() {
               public int compare(Object a, Object b) {
                  return dir*((SearchHit)a).compareByName((SearchHit)b);
               }
            });
         } else {
            // default column sort by rank
            if (isAscending()) {
               Collections.sort(result);
            } else {
               Collections.sort(result, Collections.reverseOrder());
            }
         }
         model.fireTableDataChanged();      
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
         FoodProxy f = getSearchHit(row).getFoodProxy();
         if (f != null) {
            if (col == 0) {
               c.setForeground(f.getSource().getDisplayColor());
               if (f.isDeprecated()) {
                  c.setForeground(Color.LIGHT_GRAY);
               }
            }
         }
         return c;
      }
   }

   public void itemStateChanged(ItemEvent e) {
      // Optimization -- eliminated duplicate DB search. This should save a
      // lot of time on large result sets.
      if (e.getStateChange() == ItemEvent.SELECTED) {
         doDBSearch();
      }
   }
   
   /**
    * This Action invokes the search capability.
    * @author Chris Rose
    */
   private class SearchAction extends AbstractAction {
      public SearchAction() {
         super("Search");
      }
      public void actionPerformed(ActionEvent e) {
         if (!updateAsTyping()) {
            doDBSearch();
         }
      }
   }

   public void focusQuery() {
      System.out.println("focusQuery!");
      getQueryField().requestFocusInWindow();    
      getQueryField().selectAll();
      System.out.println("Select All");
   }
 
   private void handleMouseClick(MouseEvent e) {
      if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {  // 3 = right click
         JPopupMenu menu = new JPopupMenu();
         menu.add(new EditFoodAction(getSelectedFood(), this));
         menu.add(new ExportFoodAction(getSelectedFood(), this));
         menu.add(new DeleteFoodAction(getSelectedFood(), this));
         if (menu.getComponents().length > 0) {
            menu.show(resultTable, e.getX(), e.getY());
         }
      }
   }
   
   
   public class PercentageRenderer extends PercentageBar implements TableCellRenderer {

      public PercentageRenderer() {
         super();
      }

      public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
         
         SearchHit hit = (SearchHit)value;
         setValue((hit.getScore() - minScore) / (double)(maxScore - minScore));
         
         return this;
      }

   }

   
}
