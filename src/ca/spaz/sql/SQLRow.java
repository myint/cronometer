package ca.spaz.sql;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @TODO: rename, enhance.
 * 
 * A super-simple relational-object mapping system. 
 * 
 * @author adavidson
 */
public class SQLRow {
   private String name;
   private HashMap rows = new HashMap();
   
   public class SQLCol {
      int type;
      String name;
      Object value;
   }
   
   public SQLRow(String name) {
      this.name = name; 
   }

   public void addColumn(String name, int type) {
      SQLCol col = new SQLCol();
      col.type = type;
      col.name = name;
      rows.put(name, col);
   }

   public void setValue(String name, Object val) {
      SQLCol col = (SQLCol)rows.get(name);
      assert (col != null);
      if (col != null) {
         col.value = val;
      }      
   }

   private String getDatabaseTypeName(int colType) {
      switch (colType) {
      case Types.TINYINT: {
         return "TINYINT";
      }
      case Types.SMALLINT: {
         return "SMALLINT";
      }
      case Types.INTEGER: {
         return "INTEGER";
      }
      case Types.BIGINT: {
         return "BIGINT";
      }
      case Types.VARCHAR: {
         return "VARCHAR";
      }
      case Types.TIMESTAMP: {
         return "TIMESTAMP";
      }
      case Types.DOUBLE: {
         return "DOUBLE";
      }
      default: {
         return null;
      }
      }
   }

   public void createTable(Connection conn) throws SQLException {
      StringBuffer sql = new StringBuffer();

      sql.append("CREATE TABLE ");
      sql.append(name);
      sql.append(" ( ");

      int count = 0;
      Iterator iter = rows.values().iterator();
      while (iter.hasNext()) {
         SQLCol col = (SQLCol) iter.next();
         sql.append(col.name);
         sql.append(" ");
         sql.append(getDatabaseTypeName(col.type));
         if (++count < rows.size()) {
            sql.append(", ");
         }
      }
      sql.append(" ); ");

      conn.createStatement().execute(sql.toString());

   }
   
}
