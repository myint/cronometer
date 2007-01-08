/*
 * Created on 31-Dec-2005
 */
package ca.spaz.cron.actions;

import java.awt.event.ActionEvent;

import javax.swing.*;

import ca.spaz.cron.ui.ServingTable;
import ca.spaz.util.ImageFactory;


public class CopyServingsAction extends AbstractAction {
   private ServingTable servingTable;
   
   public CopyServingsAction(ServingTable servingTable) {
      super("Copy");
      this.servingTable = servingTable;
      putValue(SMALL_ICON, new ImageIcon(ImageFactory.getInstance().loadImage("/img/copy.gif")));
      putValue(SHORT_DESCRIPTION, "Copy the selected servings from this list");
       
   }      
   
   public void actionPerformed(ActionEvent e) {
      assert (servingTable != null);
      servingTable.copySelectedServings();      
   }
}