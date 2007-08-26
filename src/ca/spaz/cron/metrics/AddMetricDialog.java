package ca.spaz.cron.metrics;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.user.UserManager;
import ca.spaz.util.ImageFactory;
import ca.spaz.util.ToolBox;

public class AddMetricDialog extends JDialog implements MetricSelectionListener {     

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
      this.setTitle("Biomarkers");
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
         Object[] data = UserManager.getCurrentUser().getBiomarkerDefinitions().getEnabledBiomarkers().toArray();
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
                       // int selectedRow = lsm.getMinSelectionIndex();
                        Biomarker biomarker = (Biomarker)list.getSelectedValue();                    
                        biomarkerSelected(biomarker);
                     }
                  }
               });
 
         JScrollPane listScroller = new JScrollPane(list);
         listScroller.setPreferredSize(new Dimension(275, 120));
         listPanel.add(listScroller, BorderLayout.CENTER);
 
      }
      return listPanel;
   }
 
   
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
         metricEditor.addMetricEditorListener(this);
      }
      return metricEditor;
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
 
   }


   private JButton getDeleteButton() {
      if (null == deleteButton) {
         deleteButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/trash.gif")));
         deleteButton.setToolTipText("Delete Biomarker");
         CRONOMETER.fixButton(deleteButton);
 
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
   }

   public void metricChosen(Metric metric) {
      abort = false;
      this.metric = metric;
      dispose();
   }

   public void metricDoubleClicked(Metric metric) { }

   public void metricSelected(Metric metric) {   }


}

