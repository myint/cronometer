/*
 * Created on 16-Apr-2005
 */
package ca.spaz.sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ca.spaz.util.Logger;

/**
 * Conveniently produces simple SQL SELECT statements
 * 
 * Currently only handles basic column selection and where based on column
 * equality.
 * 
 * @author Aaron Davidson
 */
public class SQLSelect extends SQLSelectableStatement {
  
   protected List items = new ArrayList();

   protected ResultSet results = null;

   private SQLSelect subSelection = null;

   private String[] joinedTables = null;

   private List orders = new ArrayList();

    /**
     * Create a new SQLUpdate command for the given table
     * 
     * @param tableName
     *            the name of the table to update on
     */
    public SQLSelect(String tableName) {
        super(tableName, true, true, false);
    }

    public SQLSelect(SQLSelect subSelection) {
        super("", true, true, false);
        this.subSelection = subSelection;
    }

    public SQLSelect(String[] joinedTables) {
        super("", true, true, false);
        this.joinedTables = joinedTables;
    }
    
    public void addOrderBy(String orderClause) {
       orders.add(orderClause);
    }

    /**
     * Add an item to select
     * 
     * @param field
     *            a valid SQL selection item
     */
    public void addSelection(String field) {
        items.add(field);
    }
    
    public void addSelection(String field, String as) {
        items.add(field + " as " + as);
    }

    /**
     * Execute the query and return the results.
     */
    protected ResultSet doExecuteQuery(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String query = this.getQueryString();
        if (Logger.isDebugEnabled()) {
           Logger.debug("executeQuery() - Statement to be executed: " + query);
        }

        results = stmt.executeQuery(query);
        return results;
    }
    
    private String getOrder() {
       StringBuffer sb = new StringBuffer();
       if (orders.size() > 0) {
           sb.append(" ORDER BY ");
           for (int i = 0; i < orders.size(); i++) {
               Object w = orders.get(i);
               sb.append(w.toString());
               if (i < orders.size() - 1) {
                   sb.append(",");
               }
           }
       }
       return sb.toString();
    }

    /**
     * Generate the SQL string for a SELECT command.
     */
    protected String getQueryString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT ");
        if (items.size() == 0) {
            sb.append(" * ");
        } else {
            for (int i = 0; i < items.size(); i++) {
                String term = (String) items.get(i);
                if (term != null) {
                    sb.append(term);
                    if (i < items.size() - 1) {
                        sb.append(", ");
                    }
                }
            }
        }
        sb.append(" FROM ");
        if (null != joinedTables) {
            for (int i = 0; i < joinedTables.length; i++) {
                sb.append(joinedTables[i]);
                if (i < joinedTables.length - 1) {
                    sb.append(", ");
                }
            }
        } else if (null != subSelection) {
            sb.append("(");
            sb.append(subSelection.toString());
            sb.append(")");
        } else {
            sb.append(getTableName());
        }
        sb.append(getWhere());
        sb.append(getOrder());
        return sb.toString();
    }

}
