/*
 * Created on 12-Nov-2005
 */
package ca.spaz.cron.datasource;

import java.lang.ref.SoftReference;

import ca.spaz.cron.foods.Food;

/**
 * A lightweight proxy for a Food. Just the food description and the ability 
 * to obtain the full food object if needed. Used primarily for search results. 
 * 
 * @author Aaron Davidson
 */
public class FoodProxy {
   private String description;
   private String sourceID;
   private FoodDataSource source;
   private SoftReference food;
   private int references = 0;
   
   public FoodProxy() {}   

   public FoodProxy(Food f) {
      description = f.getDescription();
      sourceID = f.getSourceUID();
      source = f.getSource();
   }
   
   public FoodProxy(String description, FoodDataSource source, String sourceid) {
      this.description = description;
      this.source = source;
      this.sourceID = sourceid;
   }

   public boolean equals(FoodProxy fp) {
      if (source != fp.getSource()) return false;
      if (!sourceID.equals(fp.getSourceID())) return false;
      if (!description.equals(fp.getDescription())) return false;
      return true;
   }
   
   public void addReference() {
      references++;
   }
   
   public int getReferences() {
      return references;
   }
   
   public Food getFood() {
      Food f = null;
      if (food != null) {
         f = (Food)food.get();
      }
      if (f == null) {
         f = source.loadFood(sourceID);
         food = new SoftReference(f);
      }
      return f;
   }

   public String getDescription() {
      if (description == null) {
         description = "";
      }
      return description;
   }
   
   public void setDescription(String description) {
      this.description = description;
   }
   
   public FoodDataSource getSource() {
      return source;
   }
   
   public void setSource(FoodDataSource source) {
      this.source = source;
   }
   
   public String getSourceID() {
      return sourceID;
   }
   
   public void setSourceID(String sourceID) {
      this.sourceID = sourceID;
   }

   public boolean isDeprecated() {
      return false;
   }
   
}
