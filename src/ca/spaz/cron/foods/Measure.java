/*
 * Created on 19-Mar-2005
 */
package ca.spaz.cron.foods;

import ca.spaz.util.XMLNode;


/**
 * A measure maps a common measure for a food item (ie: a Cup, Teaspoon,
 * Serving, etc...) The Measure contains the description of the measure and the
 * number of grams of the food there are in the given measure.
 * 
 * @author davidson
 */
public class Measure {
    // The default, standard measure, a Gram (g)
    public final static Measure GRAM = new Measure(1.0, "g", 1.0);

    private double grams; // number of grams in the food

    private double amount; // multiplier for the measure

    private String description; // description of the measure (must be unique for all measures for a food)
    
    public Measure() { }

    /**
     * Contruct a measure manually
     * 
     * @param amount
     *            the multiplier of the measure (ex: 0.5)
     * @param description
     *            the measure name (ex: Cups)
     * @param grams
     *            the number of grams in the multiple of this type (ex: grams in
     *            1/2 a Cup)
     */
    public Measure(double amount, String description, double grams) {
        this.amount = amount;
        this.description = description;
        this.grams = grams;
    }

    /**
     * Get the standard amount of this measure. Example: 1.0 Servings, 0.5 Cups,
     * 2.0 Tablespoons
     * 
     * @return the multiplier for this measure
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Set the standard amount of this measure
     * 
     * @param amount
     *            a multiplier
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Get the english name of the measure type
     * 
     * @return the english name of the measure type
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the english name of this measure type
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the number of grams in this measure
     * 
     * @return the number of grams in the measure
     */
    public double getGrams() {
        return grams;
    }

    /**
     * Set the number of grams in this measure
     */
    public void setGrams(double grams) {
        this.grams = grams;
    }

    public String toString() {
       if (amount != 1.0) {
          if (amount == 0.5) {
             return "1/2 " + description;
          }
          if (amount == (int)amount) {
             return (int)amount + " " + description;
          }          
          return amount + " " + description;
       }
       return description;
    }

    public boolean equals(Measure m) {
       if (amount != m.amount) return false;
       if (grams != m.grams) return false;
       return description.equals(m.getDescription());
    }
    
   public XMLNode toXMLNode() {
      XMLNode node = new XMLNode("measure");
      node.addAttribute("name", getDescription());
      node.addAttribute("amount", getAmount());
      node.addAttribute("grams", getGrams());
      return node;
   }

}
