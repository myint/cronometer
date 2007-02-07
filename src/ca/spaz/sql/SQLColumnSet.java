/*******************************************************************************
 * ******************************************************************************
 * Copyright (c) 2005 Chris Rose and AIMedia All rights reserved. SQLColumnSet
 * and the accompanying materials are made available under the terms of the
 * Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html Contributors: Chris
 * Rose
 ******************************************************************************/
package ca.spaz.sql;

import java.util.*;

/**
 * TODO describe
 * 
 * @author Chris Rose
 */
public class SQLColumnSet {

   private List terms;

   private List names;

   /**
    * 
    */
   public SQLColumnSet() {
      super();
      names = new ArrayList();
      terms = new ArrayList();
   }

   public void add(String name, String str) {
      names.add(name);
      terms.add(str);
   }

   public void add(String name, Object o) {
      names.add(name);
      terms.add(SQLStatement.fixClass(o));
   }

   public void add(String name, int i) {
      names.add(name);
      terms.add(new Integer(i));
   }

   public void add(String name, boolean b) {
      names.add(name);
      terms.add(new Boolean(b));
   }

   public void add(String name, double d) {
      names.add(name);
      terms.add(new Double(d));
   }
   
   public void add(String name, long d) {
      names.add(name);
      terms.add(new Long(d));
   }

   public void add(String name, float f) {
      names.add(name);
      terms.add(new Double(f));
   }

   public void add(String name, char c) {
      names.add(name);
      terms.add(new Character(c));
   }
   
   public String getNameString() {
      StringBuffer sb = new StringBuffer();
      sb.append(" ( ");
      for (int i = 0; i < names.size(); i++) {
          Object name = names.get(i);
          sb.append(name.toString());
          if (i < names.size() - 1) {
              sb.append(", ");
          }
      }
      sb.append(" ) ");
      return sb.toString();
   }
   
   public String getValueString() {
      StringBuffer sb = new StringBuffer();
      sb.append(" (");
      for (int i = 0; i < terms.size(); i++) {
          Object term = terms.get(i);
          if (term != null) {
              sb.append(" '");
              sb.append(SQLStatement.escape(term.toString()));
              sb.append("' ");
          } else {
              sb.append(" NULL");
          }
          if (i < terms.size() - 1) {
              sb.append(", ");
          }
      }
      sb.append(") ");
      return sb.toString();
   }
   
   public List getNames() {
      return Collections.unmodifiableList(names);
   }
   
   public List getValues() {
      return Collections.unmodifiableList(terms);
   }
   
}
