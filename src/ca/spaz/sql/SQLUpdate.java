package ca.spaz.sql;

import java.sql.*;
import java.util.List;

import ca.spaz.util.Logger;

/**
 * Simplifies constructing SQL Update Queries.
 */
public class SQLUpdate extends SQLSelectableStatement implements Columns {
  
   private SQLColumnSet cols;

   /**
    * Create a new SQLUpdate command for the given table
    * @param tableName the name of the table to update on
    */
   public SQLUpdate(String tableName) {
      super(tableName, true, false, true);
      cols = new SQLColumnSet();
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
     
      stmt.executeUpdate(query);
   }

   /**
    * Generate the SQL string for an UPDATE command.
    */
   protected String getQueryString() {
      StringBuffer sb = new StringBuffer();
      sb.append("UPDATE ");
      sb.append(getTableName());
      sb.append(" SET ");
      List names = cols.getNames();
      List terms = cols.getValues();
      for (int i=0; i<names.size(); i++) {         
         Object name = names.get(i);
         Object term = terms.get(i);
         if (term == null) {
            term = "NULL"; 
         }
         sb.append(name.toString());
         sb.append(" = '");
         sb.append(escape(term.toString()));
         sb.append("' ");
         if (i < names.size() - 1) {
            sb.append(", ");
         }
      }
      sb.append(getWhere());
      return sb.toString();
   }

   /**
    * Retrieve the <code>cols</code> from the <code>SQLInsert</code>
    * @return Returns the cols.
    */
   public SQLColumnSet getColumns() {
      return cols;
   }

}
