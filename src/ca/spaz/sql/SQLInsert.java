package ca.spaz.sql;

import java.sql.*;

import ca.spaz.util.Logger;

/**
 * Simplifies constructing SQL Insert Queries.
 * 
 * @author davidson
 */
public class SQLInsert extends SQLStatement implements Columns {
  
    private SQLColumnSet cols;

    public SQLInsert(String tableName) {
        super(tableName, true, true);
        cols = new SQLColumnSet();
    }

    protected void doExecute(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String query = this.getQueryString();
        if (Logger.isDebugEnabled()) {
           Logger.debug("executeQuery() - Statement to be executed: " + query);
        }

        stmt.execute(query);
    }
    
    protected ResultSet doExecuteQuery(Connection con) throws SQLException {
       Statement stmt = con.createStatement();
       String query = this.getQueryString();
       if (Logger.isDebugEnabled()) {
          Logger.debug("executeQuery() - Statement to be executed: " + query);
       }

       stmt.executeUpdate(query);
       if (con.getMetaData().supportsGetGeneratedKeys()) {
          return stmt.getGeneratedKeys();
       } else {
          return null;
       }
    }
    

    protected String getQueryString() {
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO ");
        sb.append(this.table);
        
        sb.append(cols.getNameString());
        
        sb.append("\n   VALUES ");
        sb.append(cols.getValueString());
        sb.append(";");
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
