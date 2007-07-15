/*
 * Created on 26-Nov-2005
 */
package ca.spaz.cron.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.actions.DeleteFoodAction;
import ca.spaz.cron.actions.ExportFoodAction;
import ca.spaz.cron.datasource.*;
import ca.spaz.cron.foods.*;
import ca.spaz.util.ImageFactory;
import ca.spaz.util.ToolBox;

public class SearchDialog extends JDialog implements ServingEditorListener, FoodSelectionListener, UserFoodsListener {

   private SearchPanel searchPanel;
   private ServingEditor servingEditor;
   private JPanel mainPanel; 
   private boolean abort = true;
   private Serving serving = null;    
   private JButton deleteButton;  
   private JButton editButton;
   private JButton exportButton;
   private JButton addButton;
   private JButton importButton; 
   
   public SearchDialog(Frame parent) {
     super(parent);
     init(parent);
   }
   
   public SearchDialog(Dialog parent) {
      super(parent);
      init(parent);
    }
   
   private void init(Window parent) {
      this.setTitle("CRON-o-Meter Food Database");
      this.getContentPane().add(getMainPanel());
      this.pack(); 
      ToolBox.centerOver(this, parent);
      this.setModal(true);
      
      // add escape listener to dismiss window
      getRootPane().registerKeyboardAction( new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            dispose();
         }
      }, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), 
      JComponent.WHEN_IN_FOCUSED_WINDOW );  
      
      Datasources.getUserFoods().addUserFoodsListener(this);
   }

   public void display(boolean addable) {
      getServingEditor().getAddButton().setVisible(addable);
      getSearchPanel().focusQuery();
      this.setVisible(true);
   }
   
   public Serving getSelectedServing() {
      if (abort) return null;
      return serving;
   }
   
   public SearchPanel getSearchPanel() {
      if (null == searchPanel) {
         searchPanel = new SearchPanel(); 
         
         searchPanel.addFoodSelectionListener(this);
      }
      return searchPanel;
   }
 
   public void foodSelected(FoodProxy food) {   
      if (food != null) {
         serving = new Serving(food);
      } else {
         serving = null;
      }
      getServingEditor().setServing(serving);
      getServingEditor().getMeasure().setFocus();         
      getEditButton().setEnabled(food != null);
      getDeleteButton().setEnabled(food != null && food.getSource().isMutable());
      getExportButton().setEnabled(food != null);
   }
   
   public void foodDoubleClicked(FoodProxy food) {
      if (food != null) {
         FoodEditor.editFood(food.getFood());
      }
   } 

   public JPanel getMainPanel() {
      if (null == mainPanel) {
         mainPanel = new JPanel(new BorderLayout(4,4));
         mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4)); 
         mainPanel.add(getToolBar(), BorderLayout.NORTH);
         mainPanel.add(getServingEditor(), BorderLayout.SOUTH);
         mainPanel.add(getSearchPanel(), BorderLayout.CENTER); 
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
         toolBar.add(Box.createHorizontalStrut(4));
         toolBar.add(getImportButton());
         toolBar.add(Box.createHorizontalGlue());
         toolBar.add(Box.createHorizontalStrut(4));
         toolBar.add(getEditButton());
         toolBar.add(Box.createHorizontalStrut(4));
         toolBar.add(Box.createHorizontalStrut(4));
         toolBar.add(getExportButton());
         toolBar.add(Box.createHorizontalStrut(4));
         toolBar.add(Box.createHorizontalStrut(4));
         toolBar.add(getDeleteButton());
         toolBar.add(Box.createHorizontalStrut(4));
         toolBar.add(Box.createHorizontalStrut(4));
         foodSelected(null);
      }
      return toolBar;
   }
   
   public ServingEditor getServingEditor() {
      if (servingEditor == null) {
         servingEditor = new ServingEditor();
         servingEditor.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
         servingEditor.addServingEditorListener(this);
         servingEditor.linkToSearchResults(getSearchPanel());
      }
      return servingEditor;
   }

   public void servingChosen(Serving s) {
      serving = s;
      abort = false;
      dispose();
   }
   
   public void dispose() {
      super.dispose();
      Datasources.getUserFoods().removeUserFoodsListener(this);
   }
 
   private JButton getEditButton() {
      if (null == editButton) {
         ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/edit.gif"));
         editButton = new JButton(icon);         
         CRONOMETER.fixButton(editButton);    
         editButton.setToolTipText("Edit Food");
         editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doEditFood();
            }
         });
      }
      return editButton;
   }
   
   private void doEditFood() {
     if (serving != null) {
         FoodEditor.editFood(serving.getFood()); 
      }
   }   
    
   private JButton getDeleteButton() {
      if (null == deleteButton) {
         deleteButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/trash.gif")));
         deleteButton.setToolTipText("Delete Food");
         CRONOMETER.fixButton(deleteButton);
         deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               assert(serving != null);
               DeleteFoodAction.doDeleteFood(serving.getFoodProxy(), deleteButton);
               getSearchPanel().doDBSearch();               
            }
         });
      }
      return deleteButton;
   }

   private JButton getImportButton() {
      if (null == importButton) {
         importButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/import.gif")));
         importButton.setToolTipText("Import Food");
         CRONOMETER.fixButton(importButton);
         importButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               CRONOMETER.getInstance().doImportFood();               
            }
         });
      }
      return importButton;
   }

   private JButton getExportButton() {
      if (null == exportButton) {
         exportButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/export.gif")));
         exportButton.setToolTipText("Export Food");
         CRONOMETER.fixButton(exportButton);     
         exportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doExportFood();
            }
         });
      }
      return exportButton;
   }
   
   public void doExportFood() {
      assert(serving != null);
      ExportFoodAction.doExportFood(serving.getFoodProxy(), this);
   }
   
   private JButton getAddButton() {
      if (null == addButton) {
         addButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/add.gif")));
         addButton.setToolTipText("Create New Food");
         addButton.requestFocusInWindow();
         CRONOMETER.fixButton(addButton);    
         addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
               doAddNewFood();
            }
         });
      }
      return addButton;
   }

   public void doAddNewFood() {
      FoodEditor.editFood(new Food()); 
   }

   public void userFoodAdded(FoodProxy fp) {
      getSearchPanel().doDBSearch();      
   }

   public void userFoodDeleted(FoodProxy fp) {
      getSearchPanel().doDBSearch();
      foodSelected(null);
   }

   public void userFoodModified(FoodProxy fp) {
      getSearchPanel().doDBSearch();      
   }

   
}
