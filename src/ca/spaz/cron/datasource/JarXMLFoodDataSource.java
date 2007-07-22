/*
 * Created on 12-Nov-2005
 */
package ca.spaz.cron.datasource;

import java.io.*;
import java.util.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.foods.Food;
import ca.spaz.gui.ErrorReporter;
import ca.spaz.util.Logger;

public abstract class JarXMLFoodDataSource implements FoodDataSource {
     
   private HashMap map; // maps sourceID to FoodProxy
  
   public abstract String getBaseName();
   
   public void initialize() {
      try {
         loadIndex();
         loadDeprecatedIndex();
         Logger.debug("Loaded " + map.size() +" foods.");
      } catch (IOException e) {
         Logger.error("Error Initliazing DataSource", e);
         ErrorReporter.showError("Error Initliazing DataSource", e, CRONOMETER.getInstance()); 
      }
   }

   private InputStream getStream(String name) {
      return getClass().getResourceAsStream("/"+getBaseName()+"/"+name);   
   }
   
   private void loadIndex() throws IOException {
      Logger.debug("Loading index..."); 
      InputStream in = getStream("foods.index");
      if (in != null) {
         BufferedReader bIn = new BufferedReader(new InputStreamReader(in));
         map = new HashMap();
         String line = bIn.readLine();
         while (line != null) {
            String[] parts = line.split("\\|"); 
            map.put(parts[0], new FoodProxy(parts[1],this,parts[0]));
            line = bIn.readLine();
         }
         bIn.close();
      }
   }
   

   private void loadDeprecatedIndex() throws IOException {
      Logger.debug("Loading Deprecated index...");
      InputStream in = getStream("deprecated.index");
      if (in != null) {
         BufferedReader bIn = new BufferedReader(new InputStreamReader(in));
         String line = bIn.readLine();
         while (line != null) {
            String[] parts = line.split("\\|"); 
            map.put(parts[0], new DeprecatedFoodProxy(parts[1],this,parts[0]));
            line = bIn.readLine();
         }
         bIn.close();
      }
   }

   public Food loadFood(String id) {
      Food food = null;
      InputStream in = getStream(id+".xml");
      if (in != null) {
         try { 
            food = XMLFoodLoader.loadFood(in); 
            in.close();
            food.setDataSource(this);
            food.setSourceUID(id);
         } catch (Exception e) {
            Logger.error("Error loading: "+id, e); 
            ErrorReporter.showError("Error loading: "+id, e, CRONOMETER.getInstance()); 
            food = null;
         } 
      }
      return food;
   }
   
   public FoodProxy getFoodProxy(String id) {
      return (FoodProxy)map.get(id);
   }
    
   
   public List findFoods(String[] keys) {
      ArrayList results = new ArrayList();
      Iterator iter = map.values().iterator();
      while (iter.hasNext()) {
         FoodProxy food = (FoodProxy)iter.next();
         String desc = food.getDescription().toUpperCase();
         boolean match = true;
         for (int i=0; match && i<keys.length; i++) {
            if (desc.indexOf(keys[i].toUpperCase()) == -1) {
                match = false;
            }
         }
         if (match) {
            results.add(food);
         }
      }
      return results;
   }


   public List getAllFoods() {
      return new ArrayList(map.values());
   }

   public List getFoodGroups() {
      return  new ArrayList();
   }
   
   public void close() { }

   public boolean isAvailable() {
      return map != null;
   }

   
   ////////////////////////////////////////////////////////////////////////////////
   /*
   private void generateIndex() {
      try {
         PrintStream ps = new PrintStream(
               new BufferedOutputStream(new FileOutputStream("foods.index")));      
         Enumeration e = zip.entries();
         while (e.hasMoreElements()) {
            Food f = loadFood((ZipEntry)e.nextElement());
            if (f != null) {
               Logger.error(f.getSourceUID()+"|"+f.getDescription());               
               ps.println(f.getSourceUID()+"|"+f.getDescription());
            }
         }
         ps.close();
      } catch (Exception ex) {
         Logger.debug(ex);
      }
   }   */
    
}
