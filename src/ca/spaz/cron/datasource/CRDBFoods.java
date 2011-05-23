/*
 * Created on 12-Nov-2005
 */
package ca.spaz.cron.datasource;

import java.awt.Color;

import ca.spaz.cron.foods.Food;

public class CRDBFoods extends JarXMLFoodDataSource {
   
   private static final Color CRDB_COL = new Color(0x00, 0x00, 0x70);
   
   public String getZipFileName() {
      return "crdb_005.jar";
   }

   public String getBaseName() {
      return "crdb_005";
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

   public Color getDisplayColor() { 
      return CRDB_COL;
   }

   
}
