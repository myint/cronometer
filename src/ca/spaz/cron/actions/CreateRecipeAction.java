/*
 * Created on 31-Dec-2005
 */
package ca.spaz.cron.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import ca.spaz.cron.foods.FoodEditor;
import ca.spaz.cron.foods.Recipe;
import ca.spaz.util.ImageFactory;


public class CreateRecipeAction extends AbstractAction {
   
   private List servings;
   private Component parent;
   
   public CreateRecipeAction(List servings, Component parent) {
      super("Create Recipe");
      this.servings = servings;
      this.parent = parent;
      putValue(SMALL_ICON, new ImageIcon(ImageFactory.getInstance().loadImage("/img/add.gif")));
      putValue(SHORT_DESCRIPTION, "Create a new Recipe from the selection");      
   }      
   
   public void actionPerformed(ActionEvent e) {
      execute(servings);
   }
   
   public static void execute(List servings) {
      assert (servings != null);
      Recipe r = new Recipe();
      r.addServings(servings);
      r.setDescription("New Recipe");
      r.setSourceUID("a"+servings.size()+"-"+r.getCalories());
      FoodEditor.editFood(r);
   }
   
}