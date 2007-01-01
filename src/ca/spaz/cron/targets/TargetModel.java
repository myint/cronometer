/*
 * Created on 4-Jun-2005
 */
package ca.spaz.cron.targets;

import ca.spaz.cron.foods.NutrientInfo;
import ca.spaz.cron.user.User;

/**
 * A Target Model can suggest targets for a User
 * 
 * This lets us add different expert systems that 
 * suggest nutrient target values for a user,
 * based on their parameters (age, weight, sex, etc...)
 *  
 * @author Aaron Davidson
 */
public interface TargetModel {

   public double getTargetMinimum(User user, NutrientInfo ni);
   public double getTargetMaximum(User user, NutrientInfo ni);   
   
}
