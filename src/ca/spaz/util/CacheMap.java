/*
 *******************************************************************************
 * Copyright (c) 2005 Chris Rose and AIMedia
 * All rights reserved. CacheMap and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Chris Rose
 *******************************************************************************/
package ca.spaz.util;

import java.util.*;

/**
 * A caching map implementation that provides a maximum number of stored objects.
 * @author Chris Rose
 */
public class CacheMap implements Map {
   
   private Map backingMap;
   private LinkedList orderStack;
   private int maxCacheSize;

   public CacheMap(int size) {
      this.backingMap = new HashMap();
      this.orderStack = new LinkedList();
      this.maxCacheSize = size;
   }

   /* (non-Javadoc)
    * @see java.util.Map#size()
    */
   public int size() {
      return backingMap.size();
   }

   /* (non-Javadoc)
    * @see java.util.Map#isEmpty()
    */
   public boolean isEmpty() {
      return backingMap.isEmpty();
   }

   /* (non-Javadoc)
    * @see java.util.Map#containsKey(java.lang.Object)
    */
   public boolean containsKey(Object key) {
      return backingMap.containsKey(key);
   }

   /* (non-Javadoc)
    * @see java.util.Map#containsValue(java.lang.Object)
    */
   public boolean containsValue(Object value) {
      return backingMap.containsValue(value);
   }

   /* (non-Javadoc)
    * @see java.util.Map#get(java.lang.Object)
    */
   public Object get(Object key) {
      assert(orderStack.size() == backingMap.size());
      //TODO caching behaviour, if necessary
      return backingMap.get(key);
   }

   /* (non-Javadoc)
    * @see java.util.Map#put(java.lang.Object,java.lang.Object)
    */
   public Object put(Object arg0, Object arg1) {
      assert(orderStack.size() == backingMap.size());
      if (!orderStack.contains(arg0)) {
         if (orderStack.size() >= maxCacheSize) {
            prepQueue();
         }
         orderStack.addLast(arg0);
         backingMap.put(arg0, arg1);
      } else {
         assert(backingMap.containsKey(arg0));
      }
      assert(orderStack.size() == backingMap.size());
      assert(backingMap.size() <= maxCacheSize);
      return null;
   }

   private void prepQueue() {
      while (orderStack.size() >= maxCacheSize) {
         Object key = orderStack.removeFirst();
         Object res = backingMap.remove(key);
         assert (res != null);
      }
   }

   /* (non-Javadoc)
    * @see java.util.Map#remove(java.lang.Object)
    */
   public Object remove(Object key) {
      assert(orderStack.size() == backingMap.size());
      orderStack.remove(key);
      Object ret = backingMap.remove(key);
      assert(orderStack.size() == backingMap.size());
      return ret;
   }
   
   /* (non-Javadoc)
    * @see java.util.Map#putAll(java.util.Map)
    */
   public void putAll(Map arg0) {
      assert(orderStack.size() == backingMap.size());
      for (Iterator iter = arg0.entrySet().iterator(); iter.hasNext();) {
         Map.Entry entry = (Map.Entry) iter.next();
         put(entry.getKey(), entry.getValue());
      }
      assert(orderStack.size() == backingMap.size());
   }

   /* (non-Javadoc)
    * @see java.util.Map#clear()
    */
   public void clear() {
      orderStack.clear();
      backingMap.clear();
   }

   /* (non-Javadoc)
    * @see java.util.Map#keySet()
    */
   public Set keySet() {
      return backingMap.keySet();
   }

   /* (non-Javadoc)
    * @see java.util.Map#values()
    */
   public Collection values() {
      return backingMap.values();
   }

   /* (non-Javadoc)
    * @see java.util.Map#entrySet()
    */
   public Set entrySet() {
      return backingMap.entrySet();
   }

   public boolean equals(Object obj) {
      return backingMap.equals(obj);
   }

   public int hashCode() {
      return backingMap.hashCode();
   }

   public String toString() {
      return backingMap.toString();
   }

}
