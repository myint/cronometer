/*
 * Created on 15-Nov-2005
 */
package ca.spaz.cron.user;

import java.util.*;

import org.w3c.dom.Element;

/**
 * The Biometrics history for a user.
 *
 * @author Aaron Davidson
 */
public class BiometricsHistory extends History { 
     
   public BiometricsHistory() {
      super();
   }
    
   public String getBaseName() {
      return "metrics";
   }

   public String getEntryTagName() {
      return "metric";
   }

   public UserEntry loadUserEntry(Element item) {
      return new Metric(item);
   }
   
   /**
    * Add a new record of a Metric to the history
    */
   public synchronized void addMetric(Metric m) {
      addEntry(m);
   }

   public synchronized List getMetricsOn(Date curDate) {     
      return getEntriesOn(curDate);
   }
   
   public List getMetricsOfType(String type) {
      ArrayList res = new ArrayList();
      for (int i=0; i<entries.size(); i++) {
         Metric m = (Metric)entries.get(i);
         if (m.getName().equals(type)) {
            res.add(m);
         }
      }
      return res;
   }

   public void delete(Metric m) {
      deleteEntry(m);       
   }

   public void update(Metric m) {
      updateEntry(m);
   }
    
}
