/*
 * Created on 31-Dec-2005
 */
package ca.spaz.cron.actions;
 
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.datasource.FoodProxy;
import ca.spaz.cron.user.UserManager;
import ca.spaz.util.ImageFactory;


public class DeleteFoodAction extends AbstractAction {
   private FoodProxy food;
   private Component parent;
   
   public DeleteFoodAction(FoodProxy food, Component parent) {
      super("Delete Food");
      this.food = food;
      this.parent = parent;
      putValue(SMALL_ICON, new ImageIcon(ImageFactory.getInstance().loadImage("/img/trash.gif")));
      putValue(SHORT_DESCRIPTION, "Delete this food from the food database");
      if (!food.getSource().isMutable()) {
         setEnabled(false);
      }
   }      
   
   public void actionPerformed(ActionEvent e) {
      doDeleteFood(food, parent);     
   }
   
   public static void doDeleteFood(FoodProxy fp, Component parent) {
      assert (fp != null);

      List servings = UserManager.getCurrentUser().getFoodHistory().getServings(fp);
      int rc = 0;
      if (servings.size() == 0) {
         rc = JOptionPane.showConfirmDialog(parent,
            "Are you sure you want to delete '"+
            fp.getDescription() + "'?", 
            "Delete Food?", JOptionPane.YES_NO_OPTION);
      } else {
         rc = JOptionPane.showConfirmDialog(parent,
               "Are you sure you want to delete '"+
               fp.getDescription() + "' and the "+servings.size()+" references to it?", 
               "Delete Food?", JOptionPane.YES_NO_OPTION);
      }
      if (rc == JOptionPane.YES_OPTION) {
         if (fp.getSource().isMutable()) {
            fp.getFood().delete();
            UserManager.getCurrentUser().getFoodHistory().deleteServings(servings);
            // TODO: must also delete from recipes that refer to it
            CRONOMETER.getInstance().refreshDisplays();            
         }
      }
   }
     
}