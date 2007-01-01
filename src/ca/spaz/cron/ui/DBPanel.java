/*
 * Created on 25-Nov-2005
 */
package ca.spaz.cron.ui;

import java.awt.*;

import javax.swing.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.datasource.FoodProxy;
import ca.spaz.cron.foods.Serving;
import ca.spaz.gui.TranslucentPanel;

public class DBPanel extends JPanel {

   private ServingEditor info;
   private SearchPanel searchPanel;
   private FoodDBToolBar toolBar;
   
   public DBPanel() {
      setLayout(new BorderLayout(8, 8));
      setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 6));
      add(makeNorthPanel(), BorderLayout.NORTH);
      add(getSearchPanel(), BorderLayout.CENTER);
   }
   
   private JPanel makeNorthPanel() {
      JPanel jp = new TranslucentPanel(0.15);
      jp.setBackground(Color.BLACK);
      jp.setLayout(new BorderLayout(3,3));
      jp.setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
      jp.add(getToolBar(), BorderLayout.NORTH);
      jp.add(getServingEditor(), BorderLayout.SOUTH);      
      return jp;
   }
   
   public SearchPanel getSearchPanel() {
      if (searchPanel == null) {
         searchPanel = new SearchPanel();
         searchPanel.addFoodSelectionListener(new FoodSelectionListener() {
            public void foodSelected(FoodProxy food) {
               CRONOMETER.getInstance().getDailySummary().getServingTable().deselect();
               getServingEditor().setServing(new Serving(food));
               getServingEditor().getMeasure().setFocus();
               getToolBar().setSelectedFood(food);
            }
            public void foodDoubleClicked(FoodProxy food) {
               doEditFood(food);
            }            
         });
      }
      return searchPanel;
   }
   
   public ServingEditor getServingEditor() {
      if (null == info) {
         info = new ServingEditor();
         info.addServingEditorListener(new ServingEditorListener() {
            public void servingChosen(Serving s) {
               CRONOMETER.getInstance().getDailySummary().addServing(s);               
            }           
         });
      }
      return info;
   }
   
   public FoodDBToolBar getToolBar() {
      if (toolBar == null) {
         toolBar = new FoodDBToolBar();
      }
      return toolBar;
   }
   
   private void doEditFood(FoodProxy food) {
      if (food != null) {
         FoodEditor.editFood(food.getFood());
//         foodSelected(selectedFood);
      }
   }

}
