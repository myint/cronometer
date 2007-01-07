/*
 * Created on 13-Nov-2005
 */
package ca.spaz.cron.datasource;

import java.util.*;

import ca.spaz.cron.user.*;
import ca.spaz.util.*;


public class Datasources {
   
   private static List sources;
   
   private static UserFoods userDataSource;
   private static USDAFoods usdaDataSource;
   private static CRDBFoods crdbDataSource;
   private static FoodHistory foodHist;
   private static BiometricsHistory bioHist;
   
   
   public static void initialize(ProgressListener pl) {
      sources = new ArrayList();
      if (pl != null) {
         pl.progressStart();
      }
      userDataSource = new UserFoods(User.getUserDirectory());
      userDataSource.initialize();
      sources.add(userDataSource);
      if (pl != null) {
         pl.progress(25);
      }
      
      crdbDataSource = new CRDBFoods();
      crdbDataSource.initialize();
      sources.add(crdbDataSource);
      if (pl != null) {
         pl.progress(30);
      }
      
      usdaDataSource = new USDAFoods();
      usdaDataSource.initialize();
      sources.add(usdaDataSource);
      if (pl != null) {
         pl.progress(90);
         pl.progressFinish();
      }
      
      // jump start lazy inits
      getFoodHistory();
      getBiometricsHistory();
      if (pl != null) {
         pl.progress(100);
         pl.progressFinish();
      }
   }   
   
   /**
    * Temporarily accessible here for now,
    * but will later become attached to User
    */
   public static BiometricsHistory getBiometricsHistory() {
      if (bioHist == null) {
         bioHist = new BiometricsHistory();         
      }
      return bioHist;
   }
   
   /**
    * Temporarily accessible here for now,
    * but will later become attached to User
    */
   public static FoodHistory getFoodHistory() {
      if (foodHist == null) {
         foodHist = new FoodHistory();         
      }
      return foodHist;
   }

   public static UserFoods getUserFoods() {
      return userDataSource;
   }

   public static CRDBFoods getCRDBFoods() {
      return crdbDataSource;
   }

   public static USDAFoods getUSDAFoods() {
      return usdaDataSource;
   }

   /**
    * Retrieve a list of all functional data sources in the application.  This list will
    * not contain any Datasources that have indicated that they are not available.
    * 
    * @return a <code>List</code> of <code>IFoodDatasource</code> instances consisting of
    * only those for which <code>isAvailable()</code> returns <code>true</code>.
    */
   public static List getDatasources() {
      return sources;
   }
   
   /**
    * Closes all data sources in the application.
    */
   public static void closeAll() {      
      for (Iterator iter = sources.iterator(); iter.hasNext();) {
         FoodDataSource element = (FoodDataSource) iter.next();
         element.close();
      }
      saveAll();
   }

   /**
    * Ensure all data is saved to backing stores.
    */
   public synchronized static void saveAll() {
      getFoodHistory().save();
      getBiometricsHistory().save();
   }
   
   /**
    * Look up a datasource by name
    * @param name the name of the datasource to find
    * @return the datasource or null if not found
    */
   public static FoodDataSource getSource(String name) {
      for (Iterator iter = sources.iterator(); iter.hasNext();) {
         FoodDataSource fds = (FoodDataSource) iter.next();
         if (fds.getName().equals(name)) {
            return fds;
         }         
      }
      return null;
   }

   
}
