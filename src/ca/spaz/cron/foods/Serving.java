/*
 * Created on Apr 2, 2005 by davidson
 */
package ca.spaz.cron.foods;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ca.spaz.cron.datasource.*;
import ca.spaz.cron.records.Record;
import ca.spaz.cron.user.UserManager;
import ca.spaz.sql.SQLRow;
import ca.spaz.util.XMLNode;

/**
 * Stores an amount and time of a food serving
 * 
 * @author davidson
 */
public class Serving implements Record {
     
    private FoodProxy food;

    private double grams;
    
    private Measure measure = Measure.GRAM;
    
    private long date = 0;

    private int meal;

    public Serving() {}
    
    public Serving(FoodProxy f) {
        this(f, 1.0);
    }

    public Serving(Serving s) {
       this.food = s.food;
       this.grams = s.grams;
       this.date = s.date;
       this.meal = s.meal;
       this.measure = s.measure;
    }
    
    public Serving(FoodProxy f, double grams) {
        this.food = f;
        this.grams = grams;
        this.date = System.currentTimeMillis();
        this.meal = -1;
    }

    public Serving(Element e) { 
       load(e);
    }
    
    public void load(Element e) {          
       FoodDataSource source = Datasources.getUserFoods();
       if (e.hasAttribute("source")) {
          source = Datasources.getSource(e.getAttribute("source"));
       }
       
       FoodProxy proxy = null;
       
       // load the food if stored as a child
       NodeList nl = e.getChildNodes();
       for (int i=0; i<nl.getLength(); i++) {         
          if (nl.item(i).getNodeName().equals("food") ||
              nl.item(i).getNodeName().equals("recipe")) {
             Food f = XMLFoodLoader.loadFood((Element)nl.item(i));             
             if (f != null) { 
                proxy = addToUserFoodsIfMissing(e.getAttribute("source"), f);
             }
          }
       }
       
       if (proxy == null) {
          proxy = source.getFoodProxy(e.getAttribute("food"));
       }
       
       setFood(proxy);
       if (proxy == null) {
          System.err.println("Failed to load food ["+source+":"+e.getAttribute("food")+"]");
          /*ErrorReporter.showError(
                "Failed to load food ["+source+":"+e.getAttribute("food")+"]", 
                CRONOMETER.getInstance());*/
          return;
       }
       
       if (e.hasAttribute("date")) {
          setDate(new Date(Long.parseLong(e.getAttribute("date"))));
       }
       setGrams(Double.parseDouble(e.getAttribute("grams")));
       if (e.hasAttribute("meal")) {
          setMeal(Integer.parseInt(e.getAttribute("meal")));
       }
       if (e.hasAttribute("measure")) {
          setMeasure(e.getAttribute("measure"));
       }
   }

   private FoodProxy addToUserFoodsIfMissing(String source, Food f) {     
      FoodDataSource allegedSource = Datasources.getSource(source);
      if (allegedSource != null) {
         FoodProxy f2 = allegedSource.getFoodProxy(f.getSourceUID());
         if (f2 != null) {
            if (f2.getFood().equals(f)) return f2;
         }
      }
      // TODO: scan all foods for identical matches first!
      Datasources.getUserFoods().addFood(f);  
      return f.getProxy();
   }

   public synchronized XMLNode toXML(boolean export) {
      XMLNode node = toXML();
      if (export && (food.getSource() == Datasources.getUserFoods())) {
         if (food.getFood() instanceof Recipe) {
            node.addChild(((Recipe)food.getFood()).toXML(export));
         } else {
            node.addChild(food.getFood().toXML());
         }
      }
      return node;
    }
   
   public synchronized XMLNode toXML() {
      XMLNode node = new XMLNode("serving");
      node.addAttribute("source", food.getSource().getName());
      node.addAttribute("food", food.getSourceID());
      if (date != 0) {
         node.addAttribute("date", date);
      }
      node.addAttribute("grams", grams); 
      if (measure != Measure.GRAM) {
         node.addAttribute("measure", measure.getDescription());
      }
      if (meal != -1) {
         node.addAttribute("meal", meal);
      }       
      return node;
    }
    
   
    public double getGrams() {
        return grams;
    }

    public double getAmount() {
       assert(getMeasure() != null);
       return getGrams()/getMeasure().getGrams();
    }

    public void setAmount(double val) {
       assert(getMeasure() != null);
       // TODO: verify this is correct with measures that have Amount != 1
       setGrams(getMeasure().getGrams() * val);
    }

    public FoodProxy getFoodProxy() {
        return food;
    }
    
    public Food getFood() {
       if (food == null) {
          return null;
       }
    //  assert food != null;
       return food.getFood();
   }

    public String toString() {
       return getAmount() + " " + getMeasure() + " of " + getFoodProxy().getDescription();
    }
    
    public Date getDate() {
        return new Date(date);
    }

    public void setDate(Date d) {
        this.date = d == null ? 0: d.getTime();
    }

    /**
     * Update the existing food information
     */
    public void update() {
       UserManager.getCurrentUser().getFoodHistory().update(this);
        //lds.changeServingAmount(this);
    }
    
    public void delete() {       
       UserManager.getCurrentUser().getFoodHistory().delete(this);
       //lds.removeServing(this);
    }

    public void setGrams(double amount) {
        this.grams = amount;
    }

    /**
     * @return the ID of the meal this serving was eaten at.
     */
    public int getMeal() {
        return meal;
    }


    /**
     * @return Returns the measure.
     */
    public Measure getMeasure() {
        return measure;
    }
    /**
     * @param measure The measure to set.
     */
    public void setMeasure(Measure measure) {
        this.measure = measure;
    }
    /**
     * @param meal The meal to set.
     */
    public void setMeal(int meal) {
        this.meal = meal;
    }

    public void setFood(FoodProxy food) {
       this.food = food;
       assert food != null;
       if (food != null) {
          food.addReference();
       }
    }

   /**
    * Find a matching measure by name in this food
    * 
    * @param measureName
    */
   public void setMeasure(String measureName) {
      if (food == null) return;
      List measures = getFoodProxy().getFood().getMeasures();
      for (int i=0; i<measures.size(); i++) {
         Measure m = (Measure)measures.get(i);
         if (m.getDescription().equals(measureName)) {
            setMeasure(m);
            return;
         }
      }
      // if nothing found, default to GRAMS
      setMeasure(Measure.GRAM);
   }

   public boolean isLoaded() {
      return food != null;
   }
   
   public Record copy() {
      return new Serving(this);
   }
   
   // generate the table mapping, could use xml def
   public synchronized SQLRow toSQLRow() {
      SQLRow row = new SQLRow("serving");
      row.addColumn("source", Types.VARCHAR);
      row.addColumn("food", Types.VARCHAR);
      row.addColumn("time", Types.TIMESTAMP);     
      row.addColumn("grams", Types.DOUBLE);
      row.addColumn("measure", Types.VARCHAR);    
      return row;
   }
   
   // populate (could optionally have reflective populate)
   public synchronized void populate(SQLRow row) {
      row.setValue("source", food.getSource().getName());
      row.setValue("food", food.getSourceID());
      if (date != 0) {
         row.setValue("time", new Timestamp(date));
      }
      row.setValue("grams", new Double(grams));
      if (measure != Measure.GRAM) {
         row.setValue("measure", measure.getDescription());
      }
   }

}
