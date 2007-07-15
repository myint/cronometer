/*
 * Created on 15-Nov-2005
 */
package ca.spaz.cron.foods;

import java.util.*;

import org.w3c.dom.Element;

import ca.spaz.cron.datasource.FoodProxy;
import ca.spaz.cron.records.History;
import ca.spaz.cron.records.Record;

/**
 * The Serving history for a user.
 * 
 * @author Aaron Davidson
 */
public class FoodHistory extends History {
  
   public String getBaseName() {
      return "servings";
   }

   public String getEntryTagName() {
      return "serving";
   }

   public Record loadUserEntry(Element item) {
      return new Serving(item);
   }
   
   /**
    * Add a new record of a Serving to the history
    */
   public synchronized void addServing(Serving c) {
      addEntry(c);
      c.getFoodProxy().addReference();
   }

   public synchronized List getConsumedOn(Date curDate) {     
      return getEntriesOn(curDate);
   }
   
   /**
    * Copies the servings from one day to another.
    * @param fromDate the day to copy from	
    * @param toDate the day to copy to
    * @return the copied servings
    */
   public synchronized List copyConsumedOn(Date fromDate, Date toDate) {
	   List prevConsumed = getConsumedOn(fromDate);
	   List consumed = new ArrayList();
	   Iterator iter = prevConsumed.iterator();
	   while (iter.hasNext()) {
		   Serving serving = new Serving((Serving) iter.next());
		   serving.setDate(toDate);
		   addServing(serving);
	   }
	   return consumed;
   }
   
   public synchronized List getServings(FoodProxy fp) {     
      ArrayList res = new ArrayList();
      for (int i=0; i<entries.size(); i++) {
         Serving s = (Serving)entries.get(i);
         if (s.getFoodProxy().equals(fp)) {
            res.add(s);
         }
      }
      return res;
   }
   
   public synchronized void deleteServings(List list) {
      super.deleteEntries(list);
   }

   public synchronized void delete(Serving serving) {
      super.deleteEntry(serving);
   }

   public void update(Serving serving) {
      super.updateEntry(serving);
   }
          
}
