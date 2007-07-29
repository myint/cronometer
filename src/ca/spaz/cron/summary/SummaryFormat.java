/*
 * Created on 28-Jan-2006
 */
package ca.spaz.cron.summary;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.*;

import ca.spaz.cron.foods.NutrientInfo;
import ca.spaz.cron.foods.Serving;
import ca.spaz.cron.targets.Target;
import ca.spaz.cron.user.UserManager;

public abstract class SummaryFormat {
   protected DecimalFormat df = new DecimalFormat("######0.0");
   protected DecimalFormat nf = new DecimalFormat("######0%");
   protected DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);   

   public abstract String getFormatName();
   
   public String toString() {
      return getFormatName();
   }
   
   public abstract String export(List servings, Date start, Date end, int days, boolean targetsOnly);
   public abstract String exportCategory(String category, List servings, int days, boolean targetsOnly);   
   public abstract String export(NutrientInfo ni, List servings, int days, boolean targetsOnly);
   
   public double getAmount(List servings, NutrientInfo ni) { 
      double total = 0;
      for (Iterator iter = servings.iterator(); iter.hasNext();) {
         Serving serving = (Serving) iter.next();
         double weight = serving.getGrams()/100.0;
         total += weight * serving.getFood().getNutrientAmount(ni);
     }
     return total;
   }
    

   /**
    * Look through all nutrients and see what overall percentage of the targets
    * are completed.
    */
   public double getTargetCompletion(List servings, List nutrients, int days, boolean average) {
      double total = 0;
      double value = 0;
      double valueFull = 0; 
      
      Iterator iter = nutrients.iterator();
      while (iter.hasNext()) {
         NutrientInfo ni = (NutrientInfo)iter.next();
         Target target = UserManager.getCurrentUser().getTarget(ni);
         if (target.getMin() > 0 && UserManager.getCurrentUser().isTracking(ni)) {
            double amount = getAmount(servings, ni) / (double) days;
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
         return valueFull / total;
      } else {
         return value / total;
      }
   }
   
  
   
}
