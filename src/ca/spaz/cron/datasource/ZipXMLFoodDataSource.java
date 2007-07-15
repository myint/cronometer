/*
 * Created on 12-Nov-2005
 */
package ca.spaz.cron.datasource;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.foods.Food;
import ca.spaz.gui.ErrorReporter;
import ca.spaz.util.Logger;

public abstract class ZipXMLFoodDataSource implements FoodDataSource {
     
   private ZipFile zip; // the zip file backing store
   private HashMap map; // maps sourceID to FoodProxy
  
   public abstract String getZipFileName();
   
   public void initialize() {
      try {
         zip = new ZipFile(getZipFileName());
         loadIndex();
         loadDeprecatedIndex();
         Logger.debug("Loaded " + map.size() +" foods.");
      } catch (IOException e) {
         Logger.error("Error Initliazing DataSource", e);
         ErrorReporter.showError("Error Initliazing DataSource", e, CRONOMETER.getInstance()); 
         zip = null;
      }
   }

   private void loadIndex() throws IOException {
      Logger.debug("Loading index...");
      map = new HashMap();
      ZipEntry entry = zip.getEntry("foods.index");
      BufferedReader in = new BufferedReader(
            new InputStreamReader(zip.getInputStream(entry)));
      String line = in.readLine();
      while (line != null) {
         String[] parts = line.split("\\|");
         FoodProxy food = new FoodProxy(parts[1],this,parts[0]);
         map.put(parts[0], food);
         line = in.readLine();
      }
      in.close();
   }
   

   private void loadDeprecatedIndex() throws IOException {
      Logger.debug("Loading Deprecated index...");
      ZipEntry entry = zip.getEntry("deprecated.index");
      if (entry != null) {
         BufferedReader in = new BufferedReader(
               new InputStreamReader(zip.getInputStream(entry)));
         String line = in.readLine();
         while (line != null) {
            String[] parts = line.split("\\|");
            DeprecatedFoodProxy food = new DeprecatedFoodProxy(parts[1],this,parts[0]);
            map.put(parts[0], food);
            line = in.readLine();
         }
         in.close();
      }
   }


   public Food loadFood(String id) {
      Food food = loadFood(zip.getEntry(id+".xml"));
      food.setDataSource(this);
      food.setSourceUID(id);
      return food;
   }
   
   public FoodProxy getFoodProxy(String id) {
      return (FoodProxy)map.get(id);
   }
   
   
   private Food loadFood(ZipEntry entry) {
      Food f = null;
      try {
         InputStream zIn = zip.getInputStream(entry);
         f = XMLFoodLoader.loadFood(zIn);
         zIn.close();
      } catch (Exception e) {
         Logger.error("Error loading: "+entry, e); 
         ErrorReporter.showError("Error loading: "+entry, e, CRONOMETER.getInstance()); 
         f = null;
      }
      return f;
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
   
   public void close() {
      zip = null;
   }

   public boolean isAvailable() {
      return zip != null;
   }

   
   ////////////////////////////////////////////////////////////////////////////////
   
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
   }   
    
}
