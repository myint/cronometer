package ca.spaz.cron.exercise;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.actions.*;
import ca.spaz.cron.ui.DailySummary;
import ca.spaz.cron.user.User;
import ca.spaz.cron.user.UserManager;
import ca.spaz.gui.PrettyTable;
import ca.spaz.util.ImageFactory;

public class ExerciseTable extends JPanel {

   private JXTable table;
   private JComboBox measureBox = new JComboBox();
   private ExerciseTableModel model;
   private Vector listeners = new Vector();
   private Vector changeListeners = new Vector();
   private JToolBar toolBar;
   private JButton addBtn, delBtn, printBtn;
   private String title = "Untitled";
   private static ExerciseTable instance;
   
   public ExerciseTable() {
      setMinimumSize(new Dimension(400,250));
      setPreferredSize(new Dimension(400,300));
      model = new ExerciseTableModel(this);
      setLayout(new BorderLayout(4,4));
      add(getToolBar(), BorderLayout.NORTH);
      add(makeJScrollPane(), BorderLayout.CENTER);
   }

   public static ExerciseTable getExerciseTable() {
      if (instance == null) {
         instance = new ExerciseTable();
      } 
      return instance;
   }
   
   public void setTitle(String str) {
      this.title = str;
   }

   public String getTitle() {
      return title;
   }
   
   public void addChangeListener(ChangeListener listener) {
      changeListeners.add(listener);
   }

   public void removeChangeListener(ChangeListener listener) {
      changeListeners.remove(listener);
   }
   
   protected void fireStateChangedEvent() {
      ChangeEvent e = new ChangeEvent(this);
      Iterator iter = changeListeners.iterator();
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
          toolBar.add(Box.createGlue());
          toolBar.add(getPrintButton()); 
          //toolBar.add(Box.createGlue());
      }
      return toolBar;
  }
   
   
   private JButton getDeleteButton() {
      if (null == delBtn) {
          ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/trash.gif"));
          delBtn = new JButton("Delete Exercise", icon);
          delBtn.setEnabled(false);
          delBtn.setToolTipText("Delete the selected exercise");
          delBtn.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                 deleteSelectedExercise();
              }
          });
          CRONOMETER.fixButton(delBtn);
      }
      return delBtn;
  }
   
   private JButton getAddButton() {
      if (null == addBtn) {
          ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/add.gif"));
          addBtn = new JButton("Add Exercise", icon);
          addBtn.setToolTipText("Add a new exercise");
         
          addBtn.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                 doAddExercise();
              }
          });
          CRONOMETER.fixButton(addBtn);
          
          addBtn.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
               if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                  doAddExercise();
               }
            }
         }); 
      }
      return addBtn;
  }

   
   public void doAddExercise() {
      ExerciseDialog sd = new ExerciseDialog(JOptionPane.getFrameForComponent(this));
      sd.display(true);

      Exercise s = sd.getSelectedExercise();
      if (s != null) {
         fireExerciseChosen(s);
      }
   }
   
   private JComponent makeJScrollPane() {
      JScrollPane jsp = new JScrollPane(getTable());
      jsp.setPreferredSize(new Dimension(400, 250));
      jsp.getViewport().setBackground(Color.WHITE);
      jsp.setBorder(BorderFactory.createEtchedBorder());
      return jsp;
   }

   protected JXTable getTable() {
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

         table.getColumnModel().getColumn(ExerciseTableModel.NAME_COL).setMinWidth(100);
         table.getColumnModel().getColumn(ExerciseTableModel.TIME_COL).setMinWidth(60);
         table.getColumnModel().getColumn(ExerciseTableModel.CALORIES_COL).setMinWidth(60);
         table.getColumnModel().getColumn(ExerciseTableModel.CALORIES_COL).setMaxWidth(60);

         // right align last column
         TableColumnModel tcm = table.getColumnModel();
         DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
         renderer.setHorizontalAlignment(SwingConstants.RIGHT);
         tcm.getColumn(ExerciseTableModel.TIME_COL).setCellRenderer(renderer);
         tcm.getColumn(ExerciseTableModel.CALORIES_COL).setCellRenderer(renderer);

         addTableClickListener();
  
         
         table.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               deleteSelectedExercise();
            }
         }, "Clear", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false), JComponent.WHEN_FOCUSED);
         
         table.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               deleteSelectedExercise();
            }
         }, "Clear", KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0, false), JComponent.WHEN_FOCUSED);
         
         table.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               copySelectedExercises();
            }
         }, "Copy", KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false), JComponent.WHEN_FOCUSED);
         
         table.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doPaste();
            }
         }, "Paste", KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false), JComponent.WHEN_FOCUSED);
         
         table.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               cutSelectedExercises();
            }
         }, "Cut", KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false), JComponent.WHEN_FOCUSED);
       
         table.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
               public void valueChanged(ListSelectionEvent e) {
                 /* if (e.getValueIsAdjusting()) return;*/
                  ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                  if (!lsm.isSelectionEmpty()) {
                     int sel = table.getSelectedRow();
                     if (sel != -1) {
                        fireExerciseSelected(model.getExercise(sel));
                     }
                  }
                  fireStateChangedEvent();
               }
            });
      }
      return table;
   } 
 
   /**
    * Add a list of servings to the daily listing
    * Ugly because this table and model listens to the parent, which is 
    * backwards from normal patterns...
    * @param list
    */
   public void addExercises(Exercise[] list) {
      addExercisesToUser(list, UserManager.getCurrentUser(), CRONOMETER.getDailySummary().getDate());
   }

   public void addExercisesToUser(Exercise[] list, User user, Date date) {
      DailySummary ds = CRONOMETER.getDailySummary(); 
      for (int i=0; i<list.length; i++) {
         ds.addExerciseToUser(new Exercise(list[i]), user, date);
      }
   }
   
   public void doPaste() {
      Transferable clipboardContent = CRONOMETER.getClipboard().getContents(this);
      if (clipboardContent != null) {
         if (clipboardContent.isDataFlavorSupported(ExerciseSelection.exerciseFlavor)) {         
            try {               
               Exercise[] list = (Exercise[])clipboardContent.getTransferData(ExerciseSelection.exerciseFlavor);
               if (list.length > 0) {
                  //int sel = table.getSelectedRow();
                  //doClear();
                  addExercises(list); 
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
   }
   
   private void fireExerciseSelected(Exercise e) {
      if (e == null) return;
      getDeleteButton().setEnabled(!getSelectedExercises().isEmpty());
      getTable().requestFocus();      
   }
   
   public void deleteSelectedExercise() {
      List sel = getSelectedExercises();
      if (sel.size() > 0 && isOkToDeleteExercise(sel.size())) {
         Iterator iter = sel.iterator();
         while (iter.hasNext()) {
            Exercise s = (Exercise)iter.next();
            model.delete(s);
            s.delete(); // TODO: is this safe for recipes?
         }
         deselect();
         fireStateChangedEvent();
      }
   }


   private boolean isOkToDeleteExercise(int numExercises) {
      String msg;
      if (numExercises > 1) {
         msg = "Are you sure you want to do delete the selected exercises?";
      } else {
         msg = "Are you sure you want to do delete the selected exercise?";
      }
      int choice = JOptionPane.showConfirmDialog(this, msg, "Delete Exercise?", JOptionPane.YES_NO_OPTION);
      if (choice == JOptionPane.YES_OPTION) {
         return true;
      }
      return false;
   }
   
   public void copySelectedExercises() {    
      CRONOMETER.getClipboard().setContents (new ExerciseSelection(this), CRONOMETER.getInstance());
      TransferHandler.getCopyAction().actionPerformed(new ActionEvent(getTable(), 0, "Copy"));
   }

   public void copySelectedServingsToUser(User user, Date date) {
      List<Exercise> sel = getSelectedExercises();
      if (sel.size() > 0) {
         Exercise[] exercises = new Exercise[sel.size()];
         sel.toArray(exercises);
         addExercisesToUser(exercises, user, date);
      } else {
         CRONOMETER.okDialog("Please select at least one serving", "No servings selected");
      }
   }
   
   public void cutSelectedExercises() {    
      copySelectedExercises();
      deleteSelectedExercise();
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


   public void deselect() {
      getTable().getSelectionModel().clearSelection();
   }
   
   public List getSelectedExercises() {
      List exercises = new ArrayList();
      if (table.getSelectedRow() != -1) {
         int[] rows = table.getSelectedRows();
         for (int i=0; i<rows.length; i++) {
            exercises.add(model.getExercise(rows[i]));
         }
      }
      return exercises;
   }

   public void setExercises(List consumed) {
      model.setExercises(consumed); 
      fireStateChangedEvent();
   }

   public List getExercises() {
      return model.getExercises();
   }
   
   public void addExercise(Exercise s) {
      model.addExercise(s);
      fireStateChangedEvent();
   }
   
   private void handleMouseClick(MouseEvent e) {
      JPopupMenu menu = new JPopupMenu();
      
      // actions that apply to both single and multiple selections:
      menu.addSeparator();
      menu.add(new CutExercisesAction(this));
      menu.add(new CopyExercisesAction(this));
      menu.add(new DeleteExercisesAction(this));
      
      if (menu.getComponents().length > 0) {
         menu.show(table, e.getX(), e.getY());
      }      
   }
  

   private JButton getPrintButton() {
      if (null == printBtn) {
         ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage(
               "/img/print.gif"));
         printBtn = new JButton(icon);
         printBtn.setToolTipText("Print the exercise listing");
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
   
   private void fireExerciseChosen(Exercise e) {
      if (e == null) return;
      Iterator iter = listeners.iterator();
      while (iter.hasNext()) {
         ((ExerciseEditorListener)iter.next()).exerciseChosen(e);
      }
   }
   
   public void addExerciseEditorListener(ExerciseEditorListener listener) {
      listeners.add(listener);
   }

   public void removeExerciseEditorListener(ExerciseEditorListener listener) {
      listeners.remove(listener);
   }
}
