/*
 * Created on 5-May-2006
 */
package ca.spaz.lists;

public interface ListListener {
    
   /**
    * An unspecified change has occurred to the list.
    * Listeners should recompute from scratch
    * Only sent if a more specific event is not available.
    */
   public void listChanged(); 
   
   /**
    * A new item has been added to the list.
    * @param o the added items
    */
   public void itemAdded(Object o);
      

   /**
    * A item has been removed from the list.
    * @param o the removed item
    */
   public void itemRemoved(Object o);

   /**
    * The item in the list has been modified in some way
    * @param o the modified item
    */
   public void itemChanged(Object o);


}