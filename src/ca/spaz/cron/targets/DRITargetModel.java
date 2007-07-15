/*
 * Created on 21-Jan-2006
 */
package ca.spaz.cron.targets;

import java.util.Iterator;
import java.util.List;

import ca.spaz.cron.foods.NutrientInfo;
import ca.spaz.cron.user.User;

/** 
 * @author Aaron Davidson
 */
public class DRITargetModel implements TargetModel {   
   
   public double getTargetMinimum(User user, NutrientInfo ni) {
      if (ni.getUSDA() != null && ni.getUSDA().equals("208")) {
         return getCalories(user);
      }
      DRI dri = findMatch(user, ni.getDRIs());
      if (dri != null) {
         return dri.getRDA();
      }
      return ni.getReferenceDailyIntake();
   }

   public double getTargetMaximum(User user, NutrientInfo ni) {
      if (ni.getUSDA() != null) {
         if (ni.getUSDA().equals("208")) {
            return Math.round(getCalories(user)*1.25);
         }
      }
      DRI dri = findMatch(user, ni.getDRIs());
      if (dri != null) {
         double TUL = dri.getTUL();
         return TUL > 0 ? TUL : dri.getRDA() * 5;
      }   
      return ni.getReferenceDailyIntake()*3;
   }

   public DRI findMatch(User user, List DRIs) {
      Iterator iter = DRIs.iterator();
      while (iter.hasNext()) {
         DRI dri = (DRI)iter.next();
         if (dri.matches(user)) {
            return dri;
         }
      }
      return null;
   }
   
   public String toString() {
      return "Dietary Reference Intakes";
   }   
   
   static final int CALORIES[][][][] = {
         { // short ( ~150cm)
            { // male
               {1848, 2009, 2215, 2554}, // low bmi
               {2080, 2267, 2506, 1898}, // high bmi
            },
            { // female
               {1625, 1803, 2025, 2291}, // low bmi
               {1762, 1956, 2198, 2489}, // high bmi    
            },
         },
         { // medium ( ~165cm)
            { // male
               {2068, 2254, 2490, 2880}, // low bmi
               {2349, 2566, 2842, 3296}, // high bmi       
            },
            { // female
               {1816, 2016, 2267, 2567}, // low bmi
               {1982, 2202, 2477, 2807}, // high bmi          
            },
         },
         { // tall ( ~180cm)
            { // male
               {2301, 2513, 2782, 3225}, // low bmi
               {2635, 2884, 3200, 3720}, // high bmi          
            },
            { // female
               {2015, 2239, 2519, 2855}, // low bmi
               {2211, 2459, 2769, 3141}, // high bmi          
            },
         },
   };
    
   public double getCalories(User user) {
      int PAL = user.getActivityLevel();
      if (PAL < 0) PAL = 0;
      if (PAL > 3) PAL = 3;
      
      double cm = user.getHeightInCM();
      int height = Math.abs(cm-150) < Math.abs(cm-165) ? 0 : ((Math.abs(cm-165) < Math.abs(cm-180) ? 1 : 2));
      int sex = user.isMale() ? 0 : 1;
      double BMI = user.getBMI();
      
      int calories_BMI_L = CALORIES[height][sex][0][PAL];
      int calories_BMI_H = CALORIES[height][sex][1][PAL];
      double calories = calories_BMI_L; 
      if (BMI > 18.5 && BMI < 25){ 
         double blend = Math.abs(BMI-18.5)/6.5;
         calories = calories_BMI_L*(1.0-blend) + calories_BMI_H*(blend);
      }
      if (BMI >= 25) {
         calories = calories_BMI_H;
      }  
      calories += (user.isMale()?10:7)*(30 - user.getAge());
      return Math.round(calories);
   }


}
