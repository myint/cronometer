/*
 * Created on 5-May-2006
 */
package ca.spaz.lists;

import java.util.*;
 

public class SmartList extends ArrayList {

   private List listeners; // lazy initilized
   private boolean suppressEvents;

   public SmartList() {}
   
   public SmartList(List copy) {
      addAll(copy);
   }
   
   public synchronized void addAll(List copy) {
      super.addAll(copy);
      fireListChangedEvent();
   }
   
   public synchronized boolean add(Object o) {
      boolean r = super.add(o);
      fireItemAddedEvent(o);
      return r;
   }
   
   public synchronized boolean remove(Object o) {
      boolean r = super.remove(o);
      fireItemRemovedEvent(o);
      return r;
   }
   
   public synchronized Object remove(int i) {
      Object o = super.remove(i);
      fireItemRemovedEvent(o);
      return o;
   }
   
   public synchronized void clear() {
      super.clear();
      fireListChangedEvent();
   }
    
   private synchronized List getListeners() {
      if (listeners == null) {
         listeners = new Vector();
      }
      return listeners;
   }
   
   public synchronized void setSuppressEvents(boolean val) {
      suppressEvents = val;
   }
   
   public synchronized void addListListener(ListListener sll) {      
      getListeners().add(sll);
   }
 
   public synchronized void removeListListener(ListListener sll) {
      getListeners().remove(sll);
   }
   
   public synchronized void fireListChangedEvent() {
      if (suppressEvents || listeners == null) return;
      Iterator iter = getListeners().iterator();
      while (iter.hasNext()) {
         ((ListListener)iter.next()).listChanged();
      }
   }
   
   public synchronized void fireItemRemovedEvent(Object o) {
      if (suppressEvents || listeners == null) return;
      Iterator iter = getListeners().iterator();
      while (iter.hasNext()) {
         ((ListListener)iter.next()).itemRemoved(o);
      }
   }
   
   
   public synchronized void fireItemAddedEvent(Object o) {
      if (suppressEvents || listeners == null) return;
      Iterator iter = getListeners().iterator();
      while (iter.hasNext()) {
         ((ListListener)iter.next()).itemAdded(o);
      }
   }
   
   public synchronized void fireItemChangedEvent(Object o) {
      if (suppressEvents || listeners == null) return;
      Iterator iter = getListeners().iterator();
      while (iter.hasNext()) {
         ((ListListener)iter.next()).itemChanged(o);
      }
   }
 
   
}
