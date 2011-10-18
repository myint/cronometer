/*
 * Created on 12-Nov-2005
 */
package ca.spaz.cron.datasource;

import java.awt.Color;

import ca.spaz.cron.foods.Food;


public class USDAFoods extends JarXMLFoodDataSource {
   

   public String getZipFileName() {
      return "usda_sr24.jar";
   }

   public String getBaseName() {
      return "usda_sr24";
   }
   
   public String getName() {
      return "USDA";
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
      return Color.BLACK;
   }

}
