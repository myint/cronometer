/*
 * Created on 12-Nov-2005
 */
package ca.spaz.cron.datasource;

import java.awt.Color;
import java.util.List;

import ca.spaz.cron.foods.Food;

/**
 * This interface defines the interaction the application will have with a static,
 * readonly data source.  The methods on it are all the ways that the app might
 * ask for information on elements of the food system.
 * 
 * Note regaring <code>Food</code> instances and this interface -- this interface and
 * all subclasses expect and should enforce (unless noted) that the <code>Food</code>
 * object passes as a parameter to a given instance have that same instance as its
 * <code>dataSource</code> property. 
 * 
 * @author Chris Rose
 * @author Aaron Davidson
 */
public interface FoodDataSource {
   
   /**
    * Initialize the datasource to a working state.  At the time that this method completes
    * normally (without throwing an exception), <code>isAvailable</code> must return 
    * <code>true</code> for this data source.  If this source is already available, this
    * method will do nothing.
    */
   void initialize();
   
   /**
    * Retrieve a <code>List</code> of all foods in this particular Datasource.
    * 
    * @todo finalize the needed parameters here.
    * @param keys the keys to search on.  This searches with an AND relation.
    * @return a List of <code>Food</code> objects matching the criteria.
    */
   List findFoods(String[] keys);

   /**
    * Retrieve a list of all foods in this datasource.
    * @return a List of <code>Food</code> objects consisting of every food in the datasource.
    * @throws <code>UnsupportedOperationException</code> if the datasource does not
    * support listing. (if <code>isListable()</code> is false)
    */
   List getAllFoods();

   /**
    * Retrieve the name of this datasource for use in UI components.
    * @return this Datasource's name.
    */
   String getName();

   /**
    * Close this connection.  May or may not be required, but this method should
    * ensure that there is nothing remaining of the connection.
    */
   void close();

   /**
    * Determines if the datasource is operable and available.  This includes connections
    * to physical datasources, proper setup of tables, or any other conditions that may
    * prevent this from being a functional datasource.
    * @return <code>true</code> if this datasource is functioning, <code>false</code>
    * otherwise
    */
   boolean isAvailable();

   /**
    * Load the food by unique food sourceID.
    * @param sourceID a unique food identifier for this datasource
    * @return the Food matching the given sourceID, or null if not found
    */
   public Food loadFood(String sourceID);
   
   /**
    * Get the food proxy unique food sourceID.
    * @param sourceID a unique food identifier for this datasource
    * @return the Food matching the given sourceID, or null if not found
    */
   public FoodProxy getFoodProxy(String sourceID);
   

   public boolean isMutable();
   public void updateFood(Food f);
   public void addFood(Food f);
   public void removeFood(Food f);

   /**
    * Get a unique display color for list views.
    * @return a unique display color for highlighting in lists with multiple datasources.
    */
   public Color getDisplayColor();

}
