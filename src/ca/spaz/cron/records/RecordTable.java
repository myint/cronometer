package ca.spaz.cron.records;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.foods.ServingSelection;
import ca.spaz.gui.PrettyTable;
import ca.spaz.util.ImageFactory;

public abstract class RecordTable extends JPanel {

   protected JTable table; 
   protected RecordTableModel model;
   private Vector listeners = new Vector();
   private Vector recordListeners = new Vector();
   private JToolBar toolBar;
   private JButton addBtn, delBtn, printBtn;
   private String title = "Untitled";
   
   public RecordTable(RecordTableModel entryModel) {
      setMinimumSize(new Dimension(400,250));
      setPreferredSize(new Dimension(560,300));
      model = entryModel;
      setLayout(new BorderLayout(4,4));
      add(getToolBar(), BorderLayout.NORTH);
      add(makeJScrollPane(), BorderLayout.CENTER);
   }

   public void setTitle(String str) {
      this.title = str;
   }
   public String getTitle() {
      return title;
   }
   
   public void addChangeListener(ChangeListener listener) {
      listeners.add(listener);
   }

   public void removeChangeListener(ChangeListener listener) {
      listeners.remove(listener);
   }
   
   public void addSelectionListener(RecordSelectionListener listener) {
      recordListeners.add(listener);
   }

   public void removeEntrySelectionListener(RecordSelectionListener listener) {
      recordListeners.remove(listener);
   }
   
   protected void fireStateChangedEvent() {
      ChangeEvent e = new ChangeEvent(this);
      Iterator iter = listeners.iterator();
      while (iter.hasNext()) {
         ((ChangeListener)iter.next()).stateChanged(e);
      }
      getDeleteButton().setEnabled(table.getSelectedRow() != -1);
   }
 
   public JToolBar getToolBar() {
      if (null == toolBar) {
          toolBar = new JToolBar();
          toolBar.setRollover(true);
          toolBar.setOrientation(JToolBar.HORIZONTAL);
          toolBar.setFloatable(false);
          toolBar.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
          toolBar.add(getAddButton());
          toolBar.add(Box.createHorizontalStrut(10));
          toolBar.add(getDeleteButton());
          toolBar.add(Box.createHorizontalStrut(10));
//          toolBar.add(Box.createGlue());
//          toolBar.add(getPrintButton()); 
          //toolBar.add(Box.createGlue());
      }
      return toolBar;
  }
   
   
   private JButton getDeleteButton() {
      if (null == delBtn) {
          ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/trash.gif"));
          delBtn = new JButton("Delete Measurement", icon);
          delBtn.setEnabled(false);
          delBtn.setToolTipText("Delete the selected biomarker measurement");
          delBtn.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                 deleteSelected();
              }
          });
          CRONOMETER.fixButton(delBtn);
      }
      return delBtn;
  }
   
   private JButton getAddButton() {
      if (null == addBtn) {
          ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/add.gif"));
          addBtn = new JButton("Add Measurement", icon);
          addBtn.setToolTipText("Add a new biomarker measurement");
          addBtn.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                 doAddNewEntry();
              }
          });
          CRONOMETER.fixButton(addBtn);
      }
      return addBtn;
  }
 
   public abstract void doAddNewEntry();
   
   private JComponent makeJScrollPane() {
      JScrollPane jsp = new JScrollPane(getTable());
      jsp.setPreferredSize(new Dimension(400, 250));
      jsp.getViewport().setBackground(Color.WHITE);
      jsp.setBorder(BorderFactory.createEtchedBorder());
      return jsp;
   }

   protected JTable getTable() {
      if (null == table) {
         table = new PrettyTable(model)  {
            public String getToolTipText(MouseEvent e) {
               return model.getToolTipText(
                     rowAtPoint(e.getPoint()),
                     columnAtPoint(e.getPoint()));
            }
         }; 
         table.setColumnSelectionAllowed(false);
         table.getSelectionModel().setSelectionMode(
               ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
         table.getTableHeader().setReorderingAllowed(false);

         table.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
               public void valueChanged(ListSelectionEvent e) {
                 /* if (e.getValueIsAdjusting()) return;*/
                  ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                  if (!lsm.isSelectionEmpty()) {
                     int sel = table.getSelectedRow();
                     if (sel != -1) {
                        fireEntrySelected(model.getUserEntry(sel));
                     }
                  }
                  fireStateChangedEvent();
               }
            });
         addTableClickListener();
  
         
         table.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               deleteSelected();
            }
         }, "Clear", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false), JComponent.WHEN_FOCUSED);
         
         table.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               deleteSelected();
            }
         }, "Clear", KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0, false), JComponent.WHEN_FOCUSED);
         
         table.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doCopy();
            }
         }, "Copy", KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false), JComponent.WHEN_FOCUSED);
         
         table.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doPaste();
            }
         }, "Paste", KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false), JComponent.WHEN_FOCUSED);
         
         table.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doCut();
            }
         }, "Cut", KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK, false), JComponent.WHEN_FOCUSED);
       
      }
      return table;
   } 
    

   public void doClear() {
      deleteSelected();
   }

   public void doCut() {
      doCopy();
      doClear();
   }
   
   public void doCopy() {
      copySelected();
   }   

   /**
    * Add a list of entries to the daily listing
    * Ugly because this table and model listens to the parent, which is 
    * backwards from normal patterns...
    * @param list
    */
   public void addEntries(Record[] list) {
      for (int i=0; i<list.length; i++) {
         add(list[i].copy());
      }
   }
   
   public void doPaste() {
      Transferable clipboardContent = CRONOMETER.getClipboard().getContents(this);
      if (clipboardContent != null) {
         if (clipboardContent.isDataFlavorSupported(ServingSelection.servingFlavor)) {         
            try {               
               Record[] list = (Record[])clipboardContent.getTransferData(ServingSelection.servingFlavor);
               if (list.length > 0) {
                  //int sel = table.getSelectedRow();
                  //doClear();
                  addEntries(list); 
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
   }
   
   public void deleteSelected() {
      List sel = getSelectedEntries();
      // TODO: "Are you sure" ?
      if (sel.size() > 0) {
         Iterator iter = sel.iterator();
         while (iter.hasNext()) {
            model.delete((Record)iter.next());
         }
         deselect();
         fireStateChangedEvent();
      }
   }
   
   
   public void copySelected() {  
      // TODO: implement
   }
   
   /**
    * Installs a click listener to handle contextual 
    * pop-up menus on row selections
    */
   private void addTableClickListener() {     
      table.addMouseListener(new MouseAdapter() {
         int last = -1;
         public void mouseClicked(MouseEvent e) {
            int index = table.rowAtPoint(e.getPoint());
            
            if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {
               if (index >= 0) {                
                  if (!table.isRowSelected(index)) {
                     table.getSelectionModel().setSelectionInterval(index,index);
                  }
                  handleMouseClick(e);               
               }
            } else {
               if (e.getClickCount() == 2) {
                  fireEntryDoubleClicked(model.getUserEntry(index));
               } else {
                  if (last == index) {
                     table.getSelectionModel().clearSelection();
                     last = -1;
                  } else {
                     last = index;
                  }
               }
            }
         }        
      });          
   }
  
   protected void fireEntrySelected(Record entry) {
      if (entry == null) return;
      getDeleteButton().setEnabled(!getSelectedEntries().isEmpty());
      Iterator iter = recordListeners.iterator();
      while (iter.hasNext()) {
         ((RecordSelectionListener)iter.next()).recordSelected(entry);
      }
      getTable().requestFocus();      
   }
   
   protected void fireEntryDoubleClicked(Record entry) {
      if (entry == null) return;
      Iterator iter = recordListeners.iterator();
      while (iter.hasNext()) {
         ((RecordSelectionListener)iter.next()).recordDoubleClicked(entry);
      }
   }
   
   protected void fireUserEntryChosen(Record entry) {
      if (entry == null) return;
      Iterator iter = recordListeners.iterator();
      while (iter.hasNext()) {
         ((RecordSelectionListener)iter.next()).recordChosen(entry);
      }
   }
   
   public void deselect() {
      getTable().getSelectionModel().clearSelection();
   }
   
   public List getSelectedEntries() {
      List entries = new ArrayList();
      if (table.getSelectedRow() != -1) {
         int[] rows = table.getSelectedRows();
         for (int i=0; i<rows.length; i++) {
            entries.add(model.getUserEntry(rows[i]));
         }
      }
      return entries;
   }

   public void setEntries(List entrys) {
      model.setUserEntrys(entrys); 
      fireStateChangedEvent();
   }

   public List getEntries() {
      return model.getUserEntrys();
   }
   
   public void add(Record entry) {
      model.addUserEntry(entry);
      fireStateChangedEvent();
   }
   
   protected void handleMouseClick(MouseEvent e) {
      //JPopupMenu menu = new JPopupMenu();
      // TODO: implement
   }
  

   private JButton getPrintButton() {
      if (null == printBtn) {
         ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/print.gif"));
         printBtn = new JButton(icon);
         printBtn.setToolTipText("Print the listing");
         printBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doPrint();
            }
         });
         CRONOMETER.fixButton(printBtn);
      }
      return printBtn;
   }
   
   /**
    * Does a very simple print-out of the recipe.
    */
   public void doPrint() {
      try {         
         MessageFormat headerFormat = new MessageFormat(getTitle());
         MessageFormat footerFormat = new MessageFormat("- {0} -");
         getTable().print(JTable.PrintMode.FIT_WIDTH, headerFormat, footerFormat);          
      } catch (PrinterException e) {
         e.printStackTrace();
         JOptionPane.showMessageDialog(this, e.getMessage());
      }
   }     
   
  
}
