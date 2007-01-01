/*
 * Created on 12-Nov-2005
 */
package ca.spaz.cron.datasource;

import ca.spaz.cron.foods.Food;


public class CRDBFoods extends ZipXMLFoodDataSource {
   

   public String getZipFileName() {
      return "crdb_r002.zip";
   }

   public String getName() {
      return "CRDB";
   }   
   
   public String toString() {
      return getName();
   }   
   
   public boolean isMutable() {
      return false;
   }

   public void updateFood(Food f) {
   }

   public void addFood(Food f) {
      
   }

   public void removeFood(Food f) {
      
   }
   
   
}
