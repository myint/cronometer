/*
 * Created on 31-Dec-2005
 */
package ca.spaz.cron.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.*;

import ca.spaz.cron.datasource.FoodProxy;
import ca.spaz.cron.ui.FoodEditor;
import ca.spaz.util.ImageFactory;


public class EditFoodAction extends AbstractAction {
    
   private FoodProxy food;
   private Component parent;
   
   public EditFoodAction(FoodProxy food, Component parent) {
      super("Edit Food");
      this.food = food;
      this.parent = parent;
      putValue(SMALL_ICON, new ImageIcon(ImageFactory.getInstance().loadImage("/img/Edit24.gif")));
      putValue(SHORT_DESCRIPTION, "Edit this food");      
   }      
   
   public void actionPerformed(ActionEvent e) {
      assert (food != null);
      FoodEditor.editFood(food.getFood());
   }
   
}