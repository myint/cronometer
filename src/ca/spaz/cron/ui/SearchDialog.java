/*
 * Created on 26-Nov-2005
 */
package ca.spaz.cron.ui;

import java.awt.BorderLayout;

import javax.swing.*;

import ca.spaz.cron.datasource.FoodProxy;
import ca.spaz.cron.foods.Serving;

public class SearchDialog extends JDialog implements ServingEditorListener {

   private SearchPanel searchPanel;
   private ServingEditor servingEditor;
   private JPanel mainPanel; 
   private boolean abort = true;
   private Serving serving = null;
   
   public SearchDialog(JDialog parent) {
      super(parent);
      setLocationRelativeTo(parent);
      init();
   }   
   
   public SearchDialog(JFrame parent) {
     super(parent);
     setLocationRelativeTo(parent);
     init();
   }
   
   private void init() {      
       this.setTitle("Select a Food");
       this.getContentPane().add(getMainPanel());
       this.pack();
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
            }
            public void foodDoubleClicked(FoodProxy food) {
               /*abort = false;
               dispose();*/
            }            
         });       
      }
      return searchPanel;
   }
   
   private JPanel getMainPanel() {
      if (null == mainPanel) {
         mainPanel = new JPanel(new BorderLayout(4,4));
         mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
         mainPanel.add(getServingEditor(), BorderLayout.SOUTH);
         mainPanel.add(getSearchPanel(), BorderLayout.CENTER); 
      }
      return mainPanel;
   }

   public ServingEditor getServingEditor() {
      if (servingEditor == null) {
         servingEditor = new ServingEditor();
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
