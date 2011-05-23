package ca.spaz.sql;

import java.sql.*;

/**
 * Base class for SQL command generators
 * 
 * @author davidson
 */
public abstract class SQLStatement {

    public final void execute(Connection con) throws SQLException {
        if (!isExecuteSupported()) {
            throw new UnsupportedOperationException("Execute not supported on "
                    + getClass().getName());
        }
        doExecute(con);
    }

    protected void doExecute(Connection con) throws SQLException { }

    public final boolean isExecuteSupported() {
        return executeSupport;
    }

    public final ResultSet executeQuery(Connection con) throws SQLException {
        if (!isQuerySupported()) {
            throw new UnsupportedOperationException("Query not supported on "
                    + getClass().getName());
        }
        return doExecuteQuery(con);
    }

    protected ResultSet doExecuteQuery(Connection con) throws SQLException {
        return null;
    }

    public final boolean isQuerySupported() {
        return querySupport;
    }

    protected String table;

    private boolean querySupport;

    private boolean executeSupport;

    protected SQLStatement(String table, boolean querySupport,
            boolean executeSupport) {
        this.table = table;
        this.querySupport = querySupport;
        this.executeSupport = executeSupport;
    }

    public String getTableName() {
        return table;
    }

    public static String escape(String s) {
        return s.replaceAll("\\'", "\\'\\'");
    }

    protected abstract String getQueryString();

    public String toString() {
        return getQueryString();
    }
    
    /**
     * Ensure that correct classes (with respect to toString()) ge
     * passed on
     * @param o the class to check.
     * @return a valid class for SQL
     */
    static Object fixClass(Object o) {
       if (o instanceof java.util.Date) {
          o = new java.sql.Date(((java.util.Date)o).getTime());
       }
       return o;
    }
    

}
