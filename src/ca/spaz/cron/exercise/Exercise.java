/*
 * Created on Apr 2, 2005 by davidson
 */
package ca.spaz.cron.exercise;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import org.w3c.dom.Element;

import ca.spaz.cron.records.Record;
import ca.spaz.cron.user.UserManager;
import ca.spaz.sql.SQLRow;
import ca.spaz.util.XMLNode;

/**
 * Stores an amount and time of an exercise
 * 
 * @author davidson
 */
public class Exercise implements Record {
     
   private String name;

    private double calories;
    
    private double minutes;
    
    private long date = 0;

    public Exercise() {}
    
    public Exercise(String n, double t, double c) {
        this.name = n;
        this.minutes = t;
        this.date = System.currentTimeMillis();
        this.calories = c;
    }
    
    public Exercise(Exercise e) {
       this.name = e.name;
       this.minutes = e.minutes;
       this.date = e.date;
       this.calories = e.calories;
    }

    public Exercise(Element e) { 
       load(e);
    }
    
    public void load(Element e) {          
       this.name = e.getAttribute("name");
       setMinutes(Double.parseDouble(e.getAttribute("minutes")));
       setCalories(Double.parseDouble(e.getAttribute("calories")));
       
       if (e.hasAttribute("date")) {
          setDate(new Date(Long.parseLong(e.getAttribute("date"))));
       }
   }

   public synchronized XMLNode toXML() {
      XMLNode node = new XMLNode("exercise");
      node.addAttribute("name", name);
      node.addAttribute("minutes", minutes);
      node.addAttribute("calories", calories);
      if (date != 0) {
         node.addAttribute("date", date);
      }
      return node;
    }
    
   /**
    * Update the existing food information
    */
   public void update() {
      UserManager.getCurrentUser().getExerciseHistory().update(this);
   }
   
   public void delete() {       
      UserManager.getCurrentUser().getExerciseHistory().delete(this);
   }
   
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
   
    public double getCalories() {
        return calories;
    }
    
    public void setCalories(double amount) {
       this.calories = amount;
    }
    
    public double getMinutes() {
       return minutes;
   }
   
   public void setMinutes(double amount) {
      this.minutes = amount;
   }

    public String toString() {
       return getMinutes() + " minutes of " + name;
    }
    
    public Date getDate() {
        return new Date(date);
    }

    public void setDate(Date d) {
        this.date = d == null ? 0: d.getTime();
    }

    public Record copy() {
      return new Exercise(this);
   }
   
   // generate the table mapping, could use xml def
   public synchronized SQLRow toSQLRow() {
      SQLRow row = new SQLRow("exercise");
      row.addColumn("name", Types.VARCHAR);
      row.addColumn("time", Types.TIMESTAMP);     
      row.addColumn("minutes", Types.DOUBLE);
      row.addColumn("calories", Types.DOUBLE);    
      return row;
   }
   
   // populate (could optionally have reflective populate)
   public synchronized void populate(SQLRow row) {
      row.setValue("name", name);
      row.setValue("minutes", new Double(minutes));
      if (date != 0) {
         row.setValue("time", new Timestamp(date));
      }
      row.setValue("calories", new Double(calories));
   }

   public boolean isLoaded() {
      return true;
   }
}
