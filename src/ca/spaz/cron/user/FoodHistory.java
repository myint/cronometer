/*
 * Created on 15-Nov-2005
 */
package ca.spaz.cron.user;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.datasource.FoodProxy;
import ca.spaz.cron.foods.Serving;
import ca.spaz.gui.ErrorReporter;
import ca.spaz.util.*;

/**
 * The Serving history for a user.
 * 
 * Currently uses a simple XML backing store
 * 
 * TODO: Manage data better (for instance, broken up by month or use proper DB)
 * 
 * @author Aaron Davidson
 */
public class FoodHistory {
 
   private static final String FOOD_HISTORY_FILE = "servings.xml";
   
   private ArrayList servings;
   private boolean dirty = false;
   
   public FoodHistory() {
      servings = new ArrayList();
      load();
   }
   
   /**
    * Add a new record of a Serving to the history
    */
   public synchronized void addServing(Serving c) {
      servings.add(c);
      c.getFoodProxy().addReference();
      dirty = true;    
   }

   public synchronized List getConsumedOn(Date curDate) {     
      ArrayList res = new ArrayList();
      for (int i=0; i<servings.size(); i++) {
         Serving s = (Serving)servings.get(i);
         if (ToolBox.isSameDay(s.getDate(),curDate)) {
            res.add(s);
         }
      }
      return res;
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
      for (int i=0; i<servings.size(); i++) {
         Serving s = (Serving)servings.get(i);
         if (s.getFoodProxy().equals(fp)) {
            res.add(s);
         }
      }
      return res;
   }
   
   public synchronized void deleteServings(List list) {
      servings.removeAll(list);
      dirty = true;
   }

   public synchronized void delete(Serving serving) {
      servings.remove(serving);
      dirty = true;
   }

   public void update(Serving serving) {
      dirty = true;
   }
   
   public File getFoodHistoryFile() {
      return new File(User.getUserDirectory(), FOOD_HISTORY_FILE);
   }
   
   /**
    * Flush to disk.
    */
   public synchronized void save() {
      try {
         PrintStream ps = new PrintStream(
               new BufferedOutputStream(
                     new FileOutputStream(getFoodHistoryFile())));
         writeXML(ps);
         ps.close();
      } catch (IOException e) {
         e.printStackTrace();
         ErrorReporter.showError(e, CRONOMETER.getInstance()); 
      }
      dirty = false;
   }
   
   
   public synchronized void writeXML(PrintStream out) {
      XMLNode node = new XMLNode("servings");
      for (int i=0; i<servings.size(); i++) {
         Serving s = (Serving)servings.get(i);
         node.addChild(s.toXML(false));
      }
      node.write(out);
   }
   
   public synchronized void load() {
      try {
         InputStream in = new BufferedInputStream(
               new FileInputStream(getFoodHistoryFile()));
         load(in);
         in.close();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
         ErrorReporter.showError(e, CRONOMETER.getInstance()); 
      }   
   }

   /**
    * Load Settings fresh from disk 
    */
   public synchronized void load(InputStream in) {
      try {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         dbf.setNamespaceAware(true);
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document d = db.parse(in);
         Element e = d.getDocumentElement();
         
         NodeList nl = e.getElementsByTagName("serving");
         for (int i=0; i<nl.getLength(); i++) {
            Element m = (Element)nl.item(i);
            Serving s = new Serving(m);
            if (s != null) {
               if (s.isLoaded()) {
                  servings.add(s);
               }
            }
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
         ErrorReporter.showError(e, CRONOMETER.getInstance()); 
      }
      dirty = false;
   }
}
