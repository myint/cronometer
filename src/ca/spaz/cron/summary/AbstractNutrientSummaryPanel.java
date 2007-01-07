/*
 * Created on 14-May-2005
 */
package ca.spaz.cron.summary;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import ca.spaz.cron.foods.NutrientInfo;
import ca.spaz.cron.targets.Target;
import ca.spaz.cron.user.User;

public abstract class AbstractNutrientSummaryPanel extends JPanel {
   
   protected NutrientTable nutrientTable;
   protected JScrollPane scrollPane;
   protected abstract List getNutrientList();
   protected abstract String getCategoryName();
   
   protected NutrientTable getNutrientTable() {
      if (nutrientTable == null) {
         nutrientTable = new NutrientTable(getNutrientList());
      } 
      return nutrientTable;
   }
   
   protected JScrollPane getNutrientTablePane() {
      if (scrollPane == null) {
         scrollPane = new JScrollPane(getNutrientTable());
         scrollPane.setPreferredSize(new Dimension(500, 160));
         scrollPane.getViewport().setBackground(Color.WHITE);
         scrollPane.setBorder(BorderFactory.createEtchedBorder());
         scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      }
      return scrollPane;
   }


   public Dimension getPreferredSize() {
      return new Dimension(500, 180);
   }


   public Dimension getMinimumSize() {
      return new Dimension(500, 180);
   }
   
   
   public void update(List consumed) {
      getNutrientTable().update(consumed);
   }
   
   /**
    * Look through all nutrients and see what overall percentage of the targets
    * are completed.
    */
   public double getTargetCompletion(boolean average) {
      double total = 0;
      double value = 0;
      double valueFull = 0; 
      
      Iterator iter = getNutrientList().iterator();
      while (iter.hasNext()) {
         NutrientInfo ni = (NutrientInfo)iter.next();
         Target target = User.getUser().getTarget(ni);
         if (target.getMin() > 0 && User.getUser().isTracking(ni)) {
            double amount = getNutrientTable().getAmount(ni);
            valueFull += amount/target.getMin();
            if (amount < target.getMin()) {
               value += amount/target.getMin();
            } else {
               value++;
            }
            total++;
         }
      }
      if (average) {
         return valueFull/total;
      } else {
         return value / total;
      }
   }


}
