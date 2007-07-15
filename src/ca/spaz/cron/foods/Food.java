/*
 * Created on 12-Nov-2005
 */
package ca.spaz.cron.foods;

import java.io.PrintStream;
import java.util.*;

import ca.spaz.cron.datasource.FoodDataSource;
import ca.spaz.cron.datasource.FoodProxy;
import ca.spaz.util.XMLNode;

public class Food {
   private String description;
   private List measures;
   
   // store a value for every nutrient
   private NutrientTable nutrients;
   
   private FoodDataSource dataSource;
   private String sourceUID;
   private boolean dirty;
   private String comment;
   private double pCF=4, fCF=9, cCF=4; // calories per gram conversion factors
   
   public Food() {}
    
   /**
    * Copy Constructor
    * @param f create a copy of this food
    */
   public Food(Food f) {
      copy(f);
   }
   
   public void copy(Food f) {
      this.description = f.description;
      if (f.measures != null) {
         this.measures = new ArrayList(f.measures);
      }
      this.dataSource = f.dataSource;
      if (f.nutrients != null) {
         this.nutrients = new NutrientTable(f.nutrients);
      }
      this.sourceUID = f.sourceUID;
      this.dirty = f.dirty;
      this.comment = f.comment;
      this.pCF = f.pCF;
      this.fCF = f.fCF;
      this.cCF = f.cCF;
   }
   
   public void appendComment(String str) {
      if (comment == null) {
         comment = str;
      } else {
         comment += str;
      }
   }
   
   
   public double getCalories() { 
      return getNutrientAmount(NutrientInfo.getByName("Energy"));
   }

   /**
    * Get a textual description of the food.
    * 
    * @return this Food's description.
    */
   public String getDescription() {
      return description;
   }

   /**
    * Update this Food's description.  The <code>Food</code> 
    * implementation must be associated with a writeable datasource.
    * 
    * @param text The new description.
    */
   public void setDescription(String text) {
      description = text;
      setDirty(true);
   }

   /**
    * Retrieve a List of <code>Measure</code>s for this Food.
    * 
    * @return all <code>Measure</code>s associated with this Food.
    public */
   public List getMeasures() {
      if (measures == null) {
         measures = new ArrayList();
         measures.add(Measure.GRAM);
      }
      return measures;
   }

   /**
    * Replace the list of <code>Measure</code>s for this food.  The <code>Food</code> 
    * implementation must be associated with a writeable datasource.
    * 
    * @param measures A list of <code>Measure</code> objects that will completely
    * replace the ones currently in existence for this <code>Food</code>.
    */
   public void setMeasures(List measures) {
      this.measures = measures;
      setDirty(true);
   }

   /**
    * See if the food has a value set for the given nutrient
    * 
    * @param ni the <code>NutrientInfo</code> to look up.
    * @return true if a value exists in this food
    */
   public boolean hasDataFor(NutrientInfo ni) {
        if (ni == null) return false;
        return getNutrients().dataExists(ni.getIndex());         
    }

   /**
    * Get the amount of a nutrient provided by this Food.
    * 
    * @param ni the <code>NutrientInfo</code> to look up.
    * @return the amount of the requested nutrient per unit.
    */
   public double getNutrientAmount(NutrientInfo ni) {
        if (ni == null) return 0;
        return getNutrients().getAmount(ni.getIndex());         
    }

   /**
    * Set the amount of a nutrient provided by this Food.  The <code>Food</code> 
    * implementation must be associated with a writeable datasource.
    * 
    * @param ni the Nutrient to modify.
    * @param val the amount of the Nutrient per unit.
    */
   public void setNutrientAmount(NutrientInfo ni, double val) {
      getNutrients().setAmount(ni.getIndex(), val);        
      setDirty(true);
   }

   protected String getTagName() {
      return "food";
   }
   
   public XMLNode toXML(boolean export) {  
      return toXML();
   }
   
   public XMLNode toXML() {  
      XMLNode node = new XMLNode(getTagName());
      node.addAttribute("name", getDescription());
      node.addAttribute("uid", getSourceUID());
      node.addAttribute("pcf", getProteinConversionFactor());
      node.addAttribute("fcf", getLipidConversionFactor());
      node.addAttribute("ccf", getCarbConversionFactor());
      if (comment != null) {
         XMLNode cNode = new XMLNode("comments");
         cNode.setText(comment);
         node.addChild(cNode);
      }
      List measures = getMeasures();
      Iterator iter = measures.iterator();
      while (iter.hasNext()) {
         Measure m = (Measure)iter.next();
         if (m != Measure.GRAM) {
            node.addChild(m.toXMLNode());
         }
      }
      
      List nutrients = NutrientInfo.getGlobalList();
      iter = nutrients.iterator();
      while (iter.hasNext()) {
         NutrientInfo ni = (NutrientInfo)iter.next();         
         if (hasDataFor(ni)) {
            double val = getNutrientAmount(ni);
            XMLNode niNode = new XMLNode("nutrient");
            niNode.addAttribute("name", ni.getName());
            niNode.addAttribute("amount", val);
            node.addChild(niNode);         
         }
      }
      return node;
   }
   
    public void writeXML(PrintStream out, boolean export) {         
       toXML(export).write(out);       
    }

    
   /**
    * Get the number of times this food was consumed.
    * 
    * @todo Determine if this Food can be associated with other instances of the same
    * between datasources.
    * @return the number of times this food was consumed.
    */

   public int getNumTimesConsumed() {
        int num = 0;
        /*if (dataSource instanceof ILocalFoodDatasource) {
            ILocalFoodDatasource lds = (ILocalFoodDatasource) dataSource;
            num = lds.getTimesConsumed(this);
        }*/
        return num;
    }
   
   /**
    * @return Returns the dataSource.
    */
   public FoodDataSource getSource() {
      return dataSource;
   }
   
   /**
    * Retrieve a key for this food uniquely identifying both its datasource
    * and its own unique ID in the DS.  The general contract of this method is that
    * if f1.getSourceUID().equals(f2.getSourceUID()) then f1 and f2 both come from
    * the same DS, and refer to the same item in that DS.
    * @return A UID for this food.
    */
   public String getSourceUID() {
      return sourceUID;
   }

   /**
    * Directly set the sourceUID of a food to that of another one.
    * @param uid The <code>Food</code> whose sourceUID will be used.
    */
   public void setSourceUID(String uid) {
      sourceUID = uid;
      setDirty(true);
   }
   
   /**
    * Set the comment on this food.  <code>null</code> values will result in the comment being set
    * to the empty string.
    * 
    * @param comment The comment.
    */
   public void setComment(String comment) {
      this.comment = comment;
      setDirty(true);
   }

   /**
    * Get the commment on this food.  This value is guaranteed not to be <code>null</code>.
    * 
    * @return the comment.
    */
   public String getComment() {
      return comment;
   }

   public void update() {
      if (getSource() != null) {
         if (getSource().isMutable()) {
            getSource().updateFood(this);
            setDirty(false);
         }
      }
   }
   
   public void delete() {
      if (getSource() != null) {         
         if (getSource().isMutable()) {
            getSource().removeFood(this);
         }      
      }
   }

   public void setDataSource(FoodDataSource dataSource) {
      this.dataSource = dataSource;
   }
   
   /**
    * See if the food data is identical 
    * 
    * @param f to compare against
    * @return true if the two foods contain the same food data (description, nutrients, and measures)
    */
   public boolean equals(Food f) {
      if (!f.getDescription().equals(getDescription())) return false;
      List nutrients = NutrientInfo.getGlobalList();
      for (int i=0; i<nutrients.size(); i++) {
         NutrientInfo ni = (NutrientInfo)nutrients.get(i);
         if (f.getNutrientAmount(ni) != getNutrientAmount(ni)) {
            return false;
         }
      }
      return true;
   }
      
   public FoodProxy getProxy() {
      return getSource().getFoodProxy(getSourceUID());
   }

   public boolean isDirty() {
      return dirty;
   }

   public void setDirty(boolean dirty) {
      this.dirty = dirty;
   }
   
   public double getAlcoholConversionFactor() {
      return 6.93;
   }
   
   public double getProteinConversionFactor() {
      return pCF;
   }
   
   public double getLipidConversionFactor() {
      return fCF;
   }
   
   public double getCarbConversionFactor() {
      return cCF;
   }

   public void setProteinConversionFactor(double v) {
      this.pCF = v;
   }
   
   public void setLipidConversionFactor(double v) {
      this.fCF = v;
   }

   public void setCarbConversionFactor(double v) {
      this.cCF = v;
   }


   public NutrientTable getNutrients() {
      if (nutrients == null) {
         nutrients = new NutrientTable();
      }
      return nutrients;
   }

}