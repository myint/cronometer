package ca.spaz.cron.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.actions.DeleteFoodAction;
import ca.spaz.cron.actions.ExportFoodAction;
import ca.spaz.cron.datasource.FoodProxy;
import ca.spaz.cron.foods.Food;
import ca.spaz.cron.foods.Serving;
import ca.spaz.util.ImageFactory;
import ca.spaz.util.ToolBox;
import ca.spaz.cron.user.BiomarkerDefinitions;
import ca.spaz.cron.user.Biomarker;
import ca.spaz.cron.user.Metric;

public class AddMetricDialog extends JDialog implements BiomarkerSelectionListener { //implements ServingEditorListener,  {

   private JPanel listPanel;
   private MetricEditor metricEditor;
   private JPanel mainPanel; 
   private boolean abort = true;
   private Metric metric = null;    
   private JButton deleteButton;  
   private JButton editButton;
   private JButton exportButton;
   private JButton addButton;
   private JButton importButton; 

   public AddMetricDialog(Frame parent) {
      super(parent);
      init(parent);
   }

   public AddMetricDialog(Dialog parent) {
      super(parent);
      init(parent);
   }

   private void init(Window parent) {
      this.setTitle("CRON-o-Meter Biomarkers");
      this.getContentPane().add(getMainPanel());
      this.pack(); 
      ToolBox.centerOver(this, parent);
      this.setModal(true);

      // add escape listener to dismiss window
      getRootPane().registerKeyboardAction( new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            setVisible(false);
         }
      }, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), 
      JComponent.WHEN_IN_FOCUSED_WINDOW );  
   }

   public void display(boolean addable) {
      getMetricEditor().getAddButton().setVisible(addable);
      getListPanel();
      this.setVisible(true);
   }

   public Metric getSelectedMetric() {
      if (abort) return null;
      return metric;
   }

   public JPanel getListPanel() {
      if (null == listPanel) {
         listPanel = new JPanel(); 
         Object[] data = BiomarkerDefinitions.getEnabledBiomarkers().toArray();
         final JList list = new JList(data); //data has type Object[]
         list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
//         if (data.length > 0) {
//            list.setSelectedIndex(0);
//         }
         list.setVisibleRowCount(-1);
         list.getSelectionModel().addListSelectionListener(
               new ListSelectionListener() {
                  public void valueChanged(ListSelectionEvent e) {
                     if (e.getValueIsAdjusting()) return;
                     ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                     if (!lsm.isSelectionEmpty()) {
                        int selectedRow = lsm.getMinSelectionIndex();
                        Biomarker biomarker = (Biomarker)list.getSelectedValue();                    
                        biomarkerSelected(biomarker);
                     }
                  }
               });
//       list.addMouseListener(new MouseAdapter() {
//       public void mouseClicked(MouseEvent e) {
//       int sel = resultTable.getSelectedRow();
//       if (sel != -1) {               
//       if (e.getClickCount() == 2) {
//       foodDoubleClicked();
//       } else {
//       handleMouseClick(e);
//       }
//       }
//       }
//       public void mousePressed(MouseEvent e) {
//       if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {               
//       int index = resultTable.rowAtPoint(e.getPoint());
//       if (index >= 0) {
//       resultTable.getSelectionModel().setSelectionInterval(index,index);
//       handleMouseClick(e);
//       } 
//       }
//       }
//       });


         JScrollPane listScroller = new JScrollPane(list);
         listScroller.setPreferredSize(new Dimension(275, 120));
         listPanel.add(listScroller, BorderLayout.CENTER);

//       searchPanel.addFoodSelectionListener(this);
      }
      return listPanel;
   }

//   public void addBiomarkerSelectionListener(BiomarkerSelectionListener listener) {
//      listeners.add(listener);
//   }
//   
//   public void removeFoodSelectionListener(FoodSelectionListener listener) {
//      listeners.remove(listener);
//   }
   
   public void biomarkerSelected(Biomarker biomarker) {   
      if (biomarker != null) {
         metric = new Metric(biomarker);
      } else {
         metric = null;
      }
      getMetricEditor().setMetric(metric);
      getMetricEditor().getValueField().requestFocus();         
      getEditButton().setEnabled(metric != null);
      getDeleteButton().setEnabled(metric != null);
   }

   public void biomarkerDoubleClicked(Biomarker biomarker) {
      if (biomarker != null) {
//       FoodEditor.editFood(food.getFood()); 
      }
   } 


   public JPanel getMainPanel() {
      if (null == mainPanel) {
         mainPanel = new JPanel(new BorderLayout(4,4));
         mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 
         mainPanel.add(getToolBar(), BorderLayout.NORTH);
         mainPanel.add(getListPanel(), BorderLayout.CENTER); 
         mainPanel.add(getMetricEditor(), BorderLayout.SOUTH);         
      }
      return mainPanel;
   }

   private JToolBar toolBar;
   public JToolBar getToolBar() {
      if (toolBar == null) {
         toolBar = new JToolBar();
         toolBar.setFloatable(false);
         toolBar.setRollover(true);      
         toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
         toolBar.setOpaque(false);
         toolBar.setBorder(BorderFactory.createEmptyBorder(4,2,4,2)); 
         toolBar.add(Box.createHorizontalStrut(4));
         toolBar.add(getAddButton());
         toolBar.add(Box.createHorizontalGlue());
         toolBar.add(getEditButton());
         toolBar.add(Box.createHorizontalStrut(4));
         toolBar.add(getDeleteButton());
         toolBar.add(Box.createHorizontalStrut(4));
         toolBar.add(Box.createHorizontalStrut(4));
//       foodSelected(null);
      }
      return toolBar;
   }

   public MetricEditor getMetricEditor() {
      if (metricEditor == null) {
         metricEditor = new MetricEditor();
         metricEditor.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
//       metricEditor.addMetricEditorListener(this);
      }
      return metricEditor;
   }

   public void biomarkerChosen(Biomarker biomarker) {
//      this.biomarker = biomarker;
      abort = false;
      setVisible(false);
   }

   private JButton getEditButton() {
      if (null == editButton) {
         ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/edit.gif"));
         editButton = new JButton(icon);         
         CRONOMETER.fixButton(editButton);    
         editButton.setToolTipText("Edit Biomarker");
         editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doEditBiomarker();
            }
         });
      }
      return editButton;
   }

   private void doEditBiomarker() {
//    if (serving != null) {
//    FoodEditor.editFood(serving.getFood());
//    getSearchPanel().doDBSearch();
//    }
   }


   private JButton getDeleteButton() {
      if (null == deleteButton) {
         deleteButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/trash.gif")));
         deleteButton.setToolTipText("Delete Biomarker");
         CRONOMETER.fixButton(deleteButton);
//       deleteButton.addActionListener(new ActionListener() {
//       public void actionPerformed(ActionEvent e) {
//       assert(serving != null);
//       DeleteFoodAction.doDeleteFood(serving.getFoodProxy(), deleteButton);
//       getSearchPanel().doDBSearch();
//       foodSelected(null);
//       }
//       });
      }
      return deleteButton;
   }

   private JButton getAddButton() {
      if (null == addButton) {
         addButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/add.gif")));
         addButton.setToolTipText("Create New Biomarker");
         CRONOMETER.fixButton(addButton);    
         addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
               doAddNewBiomarker();
            }
         });
      }
      return addButton;
   }

   public void doAddNewBiomarker() {
//    FoodEditor.editFood(new Food());
//    getSearchPanel().doDBSearch();
   }


}

