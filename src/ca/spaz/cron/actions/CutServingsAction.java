/*
 * Created on 31-Dec-2005
 */
package ca.spaz.cron.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import ca.spaz.cron.foods.ServingTable;
import ca.spaz.util.ImageFactory;


public class CutServingsAction extends AbstractAction {
   private ServingTable servingTable;
   
   public CutServingsAction(ServingTable servingTable) {
      super("Cut Servings");
      this.servingTable = servingTable;
      putValue(SMALL_ICON, new ImageIcon(ImageFactory.getInstance().loadImage("/img/cut.gif")));
      putValue(SHORT_DESCRIPTION, "Cut the selected servings from this list");
   }
   
   public void actionPerformed(ActionEvent e) {
      assert (servingTable != null);
      servingTable.cutSelectedServings();      
   }
}