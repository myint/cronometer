/*
 * Created on Jan 20, 2007 by davidson
 */
package ca.spaz.cron.datasource;

/**
 * In USDA sr19, they decided to 'delete' many old foods that are
 * considered out of date or are no longer sold on the market.
 * 
 * This lets us keep those foods for backwards compatability, but 
 * lets the program handle them as deprecated food items.
 * 
 * @author adavidson
 */
public class DeprecatedFoodProxy extends FoodProxy {

   public DeprecatedFoodProxy(String description, FoodDataSource source, String sourceid) {
      super(description, source, sourceid);
   }

   public boolean isDeprecated() {
      return true;
   }
   
}