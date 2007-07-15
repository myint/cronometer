/*
 * Created on 31-Dec-2005
 */
package ca.spaz.cron.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import ca.spaz.cron.datasource.FoodProxy;
import ca.spaz.cron.foods.FoodEditor;
import ca.spaz.util.ImageFactory;


public class EditFoodAction extends AbstractAction {
    
   private FoodProxy food;
   private Component parent;
   
   public EditFoodAction(FoodProxy food, Component parent) {
      super("Edit Food");
      this.food = food;
      this.parent = parent;
      putValue(SMALL_ICON, new ImageIcon(ImageFactory.getInstance().loadImage("/img/edit.gif")));
      putValue(SHORT_DESCRIPTION, "Edit this food");
      if (!food.getSource().isMutable()) {
         putValue(SHORT_DESCRIPTION, "Edit a copy of this food");
         putValue(NAME, "Edit a Copy of Food");
      }
   }      
   
   public void actionPerformed(ActionEvent e) {
      assert (food != null);
      FoodEditor.editFood(food.getFood());
   }
   
}