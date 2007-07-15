/*
 * Created on 24-Apr-2005
 */
package ca.spaz.cron.summary;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;

import ca.spaz.cron.foods.NutrientInfo;
import ca.spaz.cron.foods.Serving;

public class MacroNutrientSummaryPanel extends AbstractNutrientSummaryPanel {
   public MacroNutrientSummaryPanel() {      
      setLayout(new BorderLayout());
      setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
      add(getNutrientTablePane(), BorderLayout.CENTER);
   }
   
   protected String getCategoryName() {
      return "General";
   }
 
   protected List getNutrientList() {
      return NutrientInfo.getMacroNutrients();
   }
  
   private double getAmount(List servings, NutrientInfo ni) {
      double total = 0;
      for (Iterator iter = servings.iterator(); iter.hasNext();) {
         Serving serving = (Serving) iter.next();
         double weight = serving.getGrams()/100.0;
         total += weight * serving.getFood().getNutrientAmount(ni);
     }
     return total;
   }

   protected NutrientTable getNutrientTable() {
      if (nutrientTable == null) {
         nutrientTable = new NutrientTable(NutrientInfo.getMacroNutrients());
      } 
      return nutrientTable;
   }
   
}
