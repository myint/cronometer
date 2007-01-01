/*
 * Created on 18-Apr-2005
 */
package ca.spaz.sql;

import java.sql.*;

import ca.spaz.util.Logger;

/**
 * Conveniently produces simple SQL DELETE statements
 *
 * @author Aaron Davidson
 */
public class SQLDelete extends SQLSelectableStatement {
   
   /**
    * Create a new SQLUpdate command for the given table
    * @param tableName the name of the table to update on
    */
   public SQLDelete(String tableName) {
      super(tableName, true, false, true);
   }
   
   /** 
    * Overrides execute() and calls executeUpdate()
    */
   protected void doExecute(Connection con) throws SQLException {
      Statement stmt = con.createStatement();      
      String query = this.getQueryString();
      if (Logger.isDebugEnabled()) {
         Logger.debug("executeQuery() - Statement to be executed: " + query);
      }
     
     stmt.execute(query);
   }

   /**
    * Generate the SQL string for an DELETE command.
    */
   protected String getQueryString() {
      StringBuffer sb = new StringBuffer();
      sb.append("DELETE FROM ");
      sb.append(getTableName());
      sb.append(getWhere());
      return sb.toString();
   }
}
