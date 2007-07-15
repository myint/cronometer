/*
 * Created on 31-Dec-2005
 */
package ca.spaz.cron.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.*;

import javax.swing.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.datasource.FoodProxy;
import ca.spaz.cron.foods.Food;
import ca.spaz.gui.ErrorReporter;
import ca.spaz.util.ImageFactory;
import ca.spaz.util.Logger;


public class ExportFoodAction extends AbstractAction {
     
   private FoodProxy food;
   private Component parent;
   
   public ExportFoodAction(FoodProxy food, Component parent) {
      super("Export Food");
      this.food = food;
      this.parent = parent;
      putValue(SMALL_ICON, new ImageIcon(ImageFactory.getInstance().loadImage("/img/export.gif")));
      putValue(SHORT_DESCRIPTION, "Exports this food to a file which can be imported by other users");      
   }      
   
   public void actionPerformed(ActionEvent e) {
      assert (food != null);
      doExportFood(food, parent);     
   }
   
   public static void doExportFood(FoodProxy fp, Component parent) {
      JFileChooser fd = new JFileChooser();
      fd.setSelectedFile(new File(fp.getDescription()+".xml"));
      if (fd.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
         File f = fd.getSelectedFile();
         if (f != null) {
            try {
               Food food = fp.getFood();
               PrintStream ps = new PrintStream(
                     new BufferedOutputStream(new FileOutputStream(f)));
               food.writeXML(ps, true);
               ps.close();
            } catch (IOException ie) {
               Logger.error(ie);
               ErrorReporter.showError(ie, CRONOMETER.getInstance()); 
            }
         }
      }
   }
   
   
}