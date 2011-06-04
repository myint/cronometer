/*
 * Created on 20-Nov-2005
 */
package ca.spaz.cron.foods;

import java.util.*;

import ca.spaz.util.XMLNode;

public class Recipe extends Food {
   private List servings;   

   public Recipe() {}
   
   public Recipe(Recipe r) {
      copy(r);
   }
   
   public void copy(Recipe r) {
      super.copy(r);
      if (r.servings != null) {
         getServings().clear();
         for (int i=0; i<r.servings.size(); i++) {
            getServings().add(new Serving((Serving)r.servings.get(i)));
         }
      }
   }
   
   protected String getTagName() {
      return "recipe";
   } 

   public XMLNode toXML() { 
      return toXML(false);
   }
   
   public XMLNode toXML(boolean export) { 
      XMLNode node = super.toXML();
      for (Iterator iter = getServings().iterator(); iter.hasNext();) {
         Serving serving = (Serving) iter.next();
         node.addChild(serving.toXML(export));
      }
      return node;
   }
   
   public List getServings() {
      if (servings == null) {
         servings = new ArrayList();
      }
      return servings; 
   }      

   public void addServings(Collection s) {
      //getServings().addAll(s);
      Iterator iter = s.iterator();
      while (iter.hasNext()) { 
         addServing(new Serving((Serving)iter.next()));
      }
      recomputeNutrients();
   }
   
   public void addServing(Serving s) {
      getServings().add(s);
      s.setDate(null);
      recomputeNutrients();
   }

   public void removeServing(Serving s) {
      getServings().remove(s);
      recomputeNutrients();
   }

   /**
    * Walk through all of the servings and tally up the 
    * nutrient values for the entire meal.
    */
   private void recomputeNutrients() {
      double total = getTotalGrams();
      
      Iterator iter = NutrientInfo.getGlobalList().iterator();
      while (iter.hasNext()) {
         NutrientInfo ni = (NutrientInfo)iter.next();
         setNutrientAmount(ni, getAmount(ni, total));
      }
      
      recomputeFactors();
      
      boolean found = false;
      List list = getMeasures();      
      for (int i=0; i<list.size(); i++) {
         Measure m = (Measure)list.get(i);
         if (m.getDescription().equals("full recipe")) {
            m.setGrams(total);
            found = true;
         }
      }
      if (!found) {
         getMeasures().add( new Measure(1.0, "full recipe", total) );
      }
      //update();
   }
   
   public double getTotalGrams() {
      double total = 0;
      for (Iterator iter = getServings().iterator(); iter.hasNext();) {
         Serving serving = (Serving) iter.next();
         total += serving.getGrams();
      }
      return total;
   }
   
   /**
    * Get the nutrient amount by walking through all of the servings
    * and calculating the total amount.
    */
   private double getAmount(NutrientInfo ni, double totalGrams) {
      double total = 0;
      for (Iterator iter = getServings().iterator(); iter.hasNext();) {
         Serving serving = (Serving) iter.next();
         if (serving.getFood() != null) {
            double weight = (serving.getGrams()/totalGrams);
            total += weight * serving.getFood().getNutrientAmount(ni);
         }
      }
      return total;
   }

   /**
    * Get the nutrient amount by walking through all of the servings and
    * calculating the total amount.
    */
   private void recomputeFactors() {
      NutrientInfo pni = NutrientInfo.getByName("Protein");
      NutrientInfo fni = NutrientInfo.getByName("Fat");
      NutrientInfo cni = NutrientInfo.getByName("Carbs");
      double pcals = 0, fcals = 0, ccals = 0;
      double pgrams = 0, fgrams = 0, cgrams = 0;
      for (Iterator iter = getServings().iterator(); iter.hasNext();) {
         Serving serving = (Serving) iter.next();
         Food f = serving.getFood();
         if (serving.isLoaded()) {
            double weight = serving.getGrams() / 100.0;

            pgrams += weight * f.getNutrientAmount(pni);
            pcals += weight * f.getNutrientAmount(pni)
                  * f.getProteinConversionFactor();

            fgrams += weight * f.getNutrientAmount(fni);
            fcals += weight * f.getNutrientAmount(fni)
                  * f.getLipidConversionFactor();

            cgrams += weight * f.getNutrientAmount(cni);
            ccals += weight * f.getNutrientAmount(cni)
                  * f.getCarbConversionFactor();
         }
      }
      setProteinConversionFactor(pgrams == 0 ? 0 : pcals / pgrams);
      setLipidConversionFactor(fgrams == 0 ? 0 : fcals / fgrams);
      setCarbConversionFactor(cgrams == 0 ? 0 : ccals / cgrams);

   }
   

   public void setServings(List list) {
      servings = list;      
      recomputeNutrients();
   }
   
   
}