/*
 * Created on Apr 6, 2005 by davidson
 */
package ca.spaz.sql;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple Java Object / Relational Database linker
 * Uses reflection to map between simple objects and corresponding table rows.
 * 
 * @author davidson
 */
public abstract class DBRow {
   
   /**
    * Attempts to load all fields in the object from a ResultSet row.
    * @param row a row in a database that corresponds directly to the object
    * @throws SQLException
    * @throws IllegalArgumentException
    * @throws IllegalAccessException
    */
   public static void load(ResultSet row, Object obj)  throws SQLException, IllegalArgumentException, IllegalAccessException {
      Field[] fields = obj.getClass().getFields();
      for (int i=0; i<fields.length; i++) {
         String name = fields[i].getName();
         Class type = fields[i].getType();
         
         if (type == String.class) {
            fields[i].set(obj, row.getObject(name));
         } else if (type == int.class) {
            fields[i].setInt(obj, row.getInt(name));         
         } else if (type == double.class) {
            fields[i].setDouble(obj, row.getDouble(name));         
         }
      }
   }


}
