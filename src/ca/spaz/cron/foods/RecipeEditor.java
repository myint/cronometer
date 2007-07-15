/*
 * Created on 24-Nov-2005
 */
package ca.spaz.cron.foods;

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ca.spaz.cron.CRONOMETER;

public class RecipeEditor extends FoodEditor {
  
   private ServingTable servingTable;
   private JLabel gramsLabel;
   private JPanel servingPanel; 
   
   public RecipeEditor(CRONOMETER app, Recipe r) {
      super(app, r);      
   }
   
   /**
    * Copy changes to original and save.
    */
   public void updateOriginal() {
      ((Recipe)original).copy(getRecipe());
      original.update();
   }
   

   public void setFood(Food f) {
      this.original = f;
      this.food = new Recipe((Recipe)f); // edit on a copy
      getServingTable().setServings(getRecipe().getServings());      
   }
   
   protected String getTitle() {
      return "Recipe Editor";
   }
   
   protected void initialize() {
      this.setLayout(new BorderLayout(4, 4));
      this.setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9));
      this.add(getGeneralPanel(), BorderLayout.NORTH);
      this.add(getCenterPanel(), BorderLayout.WEST);
      this.add(getEastPanel(), BorderLayout.CENTER);
      
      getMacroNutrientsTable().setEditable(false);
      getMineralsTable().setEditable(false);
      getVitaminsTable().setEditable(false);
      getAminoAcidsTable().setEditable(false);
      getLipidsTable().setEditable(false);
   }
   
   private void updateNutrients() { 
      getMeasureSelector().updateMeasure();

      getMacroNutrientsTable().setFood(food);
      getMineralsTable().setFood(food);
      getVitaminsTable().setFood(food);
      getAminoAcidsTable().setFood(food);
      getLipidsTable().setFood(food);
      getGramsLabel().setText("Recipe Weight: "+
            Math.round(getRecipe().getTotalGrams()*10)/10.0+"g");
   }
   
   private Recipe getRecipe() {
      return (Recipe)food;
   }
   
   private JLabel getGramsLabel() {
      if (gramsLabel == null) {
         gramsLabel = new JLabel();
      }
      return gramsLabel;
   }

   private JTabbedPane getEastPanel() {
      JTabbedPane jp = new JTabbedPane();
      jp.add("Recipe", getServingPanel());
      jp.add("Nutrients", getNutrientPanel());
      getNutrientPanel().setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
      return jp;
   }
   
   public ServingTable getServingTable() {
      if (null == servingTable) {
         servingTable = new ServingTable();
         servingTable.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
               getRecipe().setServings(servingTable.getServings());
               getMeasureEditor().resetMeasures();               
               updateNutrients(); 
            }
         }); 
         servingTable.addServingSelectionListener(new ServingSelectionListener() {
            public void servingChosen(Serving s) {
               if (s.getFoodProxy().getSource() == food.getSource() && 
                     s.getFoodProxy().getSourceID() == food.getSourceUID()) {
                    JOptionPane.showMessageDialog(servingTable, "A recipe can not contain itself!");
                 } else {
                    getServingTable().addServing(s);
                 }
            }
            public void servingDoubleClicked(Serving s) { }
            public void servingSelected(Serving s) { }
         });
         servingTable.setTitle("Recipe '"+food.getDescription()+"'");
      }
      return servingTable;
   }
   

   private JPanel getServingPanel() {
      if (null == servingPanel) {
         servingPanel = new JPanel(new BorderLayout(4,4));
         servingPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
         servingPanel.add(getServingTable(),BorderLayout.CENTER);
         getServingTable().getToolBar().add(Box.createGlue());
         getServingTable().getToolBar().add(getGramsLabel());
      }
      return servingPanel;
   } 
   
}
