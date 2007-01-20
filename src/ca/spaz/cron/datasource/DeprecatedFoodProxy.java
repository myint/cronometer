/*
 * Created on Jan 20, 2007 by davidson
 */
package ca.spaz.cron.datasource;

public class DeprecatedFoodProxy extends FoodProxy {

   public DeprecatedFoodProxy(String description, FoodDataSource source, String sourceid) {
      super(description, source, sourceid);
   }


   public boolean isDeprecated() {
      return true;
   }
   
}