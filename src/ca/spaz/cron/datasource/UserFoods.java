/*
 * Created on 12-Nov-2005
 */
package ca.spaz.cron.datasource;

import java.awt.Color;
import java.io.*;
import java.util.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.foods.Food;
import ca.spaz.gui.ErrorReporter;
import ca.spaz.util.Logger;

public class UserFoods implements FoodDataSource {
   private static final String FOODS_INDEX = "foods.index";

   private static final Color USER_COL = new Color(0x00, 0x70, 0x00);

   private Vector listeners = new Vector();

   private int maxUID = 0;
   private HashMap<String, FoodProxy> map; // maps sourceID to FoodProxy
   private File userDir;
   
   public UserFoods(File dir) {
      this.userDir = new File(dir, "foods");
      if (!userDir.exists()) {
         userDir.mkdir();
      }
   }

   public void initialize() {
      try { 
         loadIndex();
      } catch (FileNotFoundException e) {
         // normal on first run
      } catch (IOException e) {
         ErrorReporter.showError(e, CRONOMETER.getInstance()); 
         e.printStackTrace();
      }
   }
   
   private String getNewUID() {
      return Integer.toString(++maxUID);
   }

   private void loadIndex() throws IOException {
      Logger.debug("Loading index...");
      map = new HashMap<String,FoodProxy>();
      File file = new File(userDir, FOODS_INDEX);
      BufferedReader in = new BufferedReader(new FileReader(file));
      String line = in.readLine();
      while (line != null) {
         String[] parts = line.split("\\|");
         if (parts.length == 2) {
            FoodProxy food = new FoodProxy(parts[1],this,parts[0]);
            food.addReference();
            int UID = Integer.parseInt(parts[0]);
            if (UID > maxUID) {
               maxUID = UID;
            }
            map.put(parts[0], food);
         }
         line = in.readLine();
      }
      in.close();
      Logger.debug("Loaded " + map.size() +" foods.");
   }   
   
   private void writeIndex() {
      try {
         Logger.debug("Writing out index...");
         PrintStream ps = new PrintStream(
               new BufferedOutputStream(new FileOutputStream(
                     new File(userDir,FOODS_INDEX))));      
         Iterator iter = map.values().iterator();
         while (iter.hasNext()) {
            FoodProxy fp = (FoodProxy)iter.next();
            ps.println(fp.getSourceID()+"|"+fp.getDescription());
         }
         ps.close();
      } catch (Exception ex) {
         Logger.debug(ex);
         ErrorReporter.showError(ex, CRONOMETER.getInstance()); 
      }
   }   

   public FoodProxy getFoodProxy(String id) {
      return (FoodProxy)map.get(id);
   }
   
   public Food loadFood(String id) {
      Logger.debug("loadFood("+id+")");
      Food food = XMLFoodLoader.loadFood(new File(userDir, id+".xml"));
      food.setDataSource(this);
      food.setSourceUID(id);
      return food;
   }
   
   public List<FoodProxy> findFoods(String[] keys) {
      ArrayList<FoodProxy> results = new ArrayList<FoodProxy>();
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

   public List<FoodProxy> getAllFoods() {
      return new ArrayList<FoodProxy>(map.values());
   }

   public List getFoodGroups() {
      return new ArrayList();
   }

   public String getName() {
      return "My Foods";
   }

   public void close() {
      Logger.debug("Closing UserDataSource:" + userDir); 
   }

   public boolean isAvailable() {
      return true;
   }

   public boolean isMutable() {
      return true;
   }
   
   public void updateFood(Food f) {
      File file = new File(userDir, f.getSourceUID()+".xml");
      try {
         PrintStream ps = new PrintStream(
               new BufferedOutputStream(new FileOutputStream(file)));
         f.writeXML(ps, false);
         ps.close();
      } catch (IOException e) {
         Logger.error("Error writing food " + file, e); 
         ErrorReporter.showError("Error writing food " + file, e, CRONOMETER.getInstance()); 
      }
      FoodProxy fp = getFoodProxy(f.getSourceUID());
      assert (fp != null);
      if (f.getDescription() == null || !f.getDescription().equals(fp.getDescription())) {
         fp.setDescription(f.getDescription());
         writeIndex();
      }
      fireFoodModifiedEvent(f.getProxy());
   }
      
   public void addFood(Food f) {
      f.setDataSource(this);
      f.setSourceUID(getNewUID());
      FoodProxy proxy = new FoodProxy(f);
      proxy.addReference();
      map.put(f.getSourceUID(), proxy);
      updateFood(f);
      writeIndex();
      fireFoodAddedEvent(f.getProxy());
   }
   
   public void removeFood(Food f) {
      File file = new File(userDir, f.getSourceUID()+".xml");
      file.delete();
      map.remove(f.getSourceUID());
      writeIndex();
      fireFoodDeletedEvent(f.getProxy());
   }
   
   public String toString() {
      return getName();
   }

   /**
    * Searches dataset for an identical food. 
    * @param f a food to search for
    * @return the identical food if found
    */
   public Food findIdenticalFood(Food f) {
      Iterator iter = map.values().iterator();
      while (iter.hasNext()) {
         FoodProxy food = (FoodProxy)iter.next();
         if (food.getDescription().equals(f.getDescription())) {
            if (food.getFood().equals(f)) {
               return food.getFood();
            }
         }
      }
      return null;
   }

   public Color getDisplayColor() { 
      return USER_COL;
   }

   protected void fireFoodModifiedEvent(FoodProxy fp) {      
      Iterator iter = listeners.iterator();
      while (iter.hasNext()) {
         ((UserFoodsListener)iter.next()).userFoodModified(fp);
      }
   }
   
   protected void fireFoodAddedEvent(FoodProxy fp) {
      Iterator iter = listeners.iterator();
      while (iter.hasNext()) {
         ((UserFoodsListener)iter.next()).userFoodAdded(fp);
      }
   }
   
   protected void fireFoodDeletedEvent(FoodProxy fp) {      
      Iterator iter = listeners.iterator();
      while (iter.hasNext()) {
         ((UserFoodsListener)iter.next()).userFoodDeleted(fp);
      }
   }
   
   
   public void addUserFoodsListener(UserFoodsListener listener) {
      listeners.add(listener);
   }
   
   public void removeUserFoodsListener(UserFoodsListener listener) {
      listeners.remove(listener);
   }
   
   
   
}
