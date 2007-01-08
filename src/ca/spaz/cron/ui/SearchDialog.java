/*
 * Created on 26-Nov-2005
 */
package ca.spaz.cron.ui;

import java.awt.*;

import javax.swing.*;

import ca.spaz.cron.datasource.FoodProxy;
import ca.spaz.cron.foods.Serving;
import ca.spaz.util.ToolBox;

public class SearchDialog extends JDialog implements ServingEditorListener {

   private SearchPanel searchPanel;
   private ServingEditor servingEditor;
   private JPanel mainPanel; 
   private boolean abort = true;
   private Serving serving = null;
   
  
   public SearchDialog(Frame parent) {
     super(parent);
     init(parent);
   }
   
   public SearchDialog(Dialog parent) {
      super(parent);
      init(parent);
    }
   
   private void init(Window parent) {
      this.setTitle("Select a Food");
      this.getContentPane().add(getMainPanel());
      this.pack(); 
      ToolBox.centerOver(this, parent);
      this.setModal(true);
   }

   public void display() {
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
         
         searchPanel.addFoodSelectionListener(new FoodSelectionListener() {
            public void foodSelected(FoodProxy food) {                  
               getServingEditor().setServing(new Serving(food));
               getServingEditor().getMeasure().setFocus();
               getToolBar().setSelectedFood(food);
            }
            public void foodDoubleClicked(FoodProxy food) {
               if (food != null) {
                  FoodEditor.editFood(food.getFood()); 
               }
            } 
         });

         searchPanel.addFoodSelectionListener(new FoodSelectionListener() {
            public void foodSelected(FoodProxy food) {
                getServingEditor().setServing(new Serving(food));
            }
            public void foodDoubleClicked(FoodProxy food) {
               /*abort = false;
               dispose();*/
            }            
         });       
      }
      return searchPanel;
   }
   
   public JPanel getMainPanel() {
      if (null == mainPanel) {
         mainPanel = new JPanel(new BorderLayout(4,4));
         mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
         //mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
         
         mainPanel.add(getToolBar(), BorderLayout.NORTH);
         mainPanel.add(getServingEditor(), BorderLayout.SOUTH);
         mainPanel.add(getSearchPanel(), BorderLayout.CENTER); 
      }
      return mainPanel;
   }
   
   // TODO: pre-0.7 add cancel (esc?)
   // TODO: pre-0.7 move this in here, add refreshing on edit/delete
   private FoodDBToolBar toolBar;
   public FoodDBToolBar getToolBar() {
      if (toolBar == null) {
         toolBar = new FoodDBToolBar();
      }
      return toolBar;
   }
   
   public ServingEditor getServingEditor() {
      if (servingEditor == null) {
         servingEditor = new ServingEditor();
         servingEditor.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
         servingEditor.addServingEditorListener(this);
      }
      return servingEditor;
   }
    
   public void servingChosen(Serving s) {
      serving = s;
      abort = false;
      dispose();
   }
      
}
