/*
 * Created on 7-May-2005
 */
package ca.spaz.cron.datasource.USDAImport;

import java.util.HashMap;

import ca.spaz.cron.foods.Food;
import ca.spaz.cron.foods.Measure;

public class USDAWeight {
   
   String ndb_id;
   double amount;
   double grams;
   String description;
   
   
   public USDAWeight(String str) {
      String[] parts = str.split("\\^");
      for (int i = 0; i < parts.length; i++) {
         parts[i] = parts[i].replaceAll("^~", "");
         parts[i] = parts[i].replaceAll("~$", "");
      }
      ndb_id = parts[0];
      if (parts[2].length() == 0) {
         amount = 1.0; // hack for bad data
      } else {
         amount = Double.parseDouble(parts[2]);
      }
      description = parts[3]; 
      grams = Double.parseDouble(parts[4]); 
   }
   
   public void addToDB(HashMap foods) {
      Food food = (Food)foods.get(ndb_id);
      assert(food != null);
      food.getMeasures().add(new Measure(amount, description, grams));
   }

}
