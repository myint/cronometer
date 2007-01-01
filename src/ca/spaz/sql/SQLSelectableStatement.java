/*
 *******************************************************************************
 * Copyright (c) 2005 Chris Rose and AIMedia
 * All rights reserved. SQLSelectableStatement and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Chris Rose
 *******************************************************************************/
package ca.spaz.sql;

import java.util.ArrayList;

/**
 * An abstract class encapsulating capabilities that are consistent across all selecting SQL
 * statements.
 *  
 * @author Chris Rose
 */
public abstract class SQLSelectableStatement extends SQLStatement {
    
    private boolean and;

   protected SQLSelectableStatement(String table, boolean and, boolean querySupport, boolean executeSupport) {
        super(table, querySupport, executeSupport);
        this.and = and;
    }

    private final ArrayList where = new ArrayList();
    public static final String EQ = "=";
    public static final String GT = ">";
    public static final String LT = "<";

    /**
     * Added a WHERE constraint to the SELECT command.
     * 
     * @param name
     *            the field to constrain
     * @param val
     *            the value this field must equal as a constraint
     */
    public void addWhere(String name, Object val) {
        addWhere(name, EQ, val);
    }

    public void addWhere(String name, String operator, Object val) {
        where.add("upper(" + name + ") " + operator + " '"
                + escape(fixClass(val).toString()).toUpperCase() + "' ");
    }
    
    public void addWhere(String name, int val) {
        addWhere(name, EQ, val);
    }
    
    public void addWhere(String name, String op, int val) {
        addWhere(name, op, new Integer(val));
    }

    public void addWhere(String name, double val) {
        addWhere(name, EQ, val);
    }
    
    public void addWhere(String name, String op, double val) {
        addWhere(name, op, new Double(val));
    }

    public void addWhere(String name, char val) {
        addWhere(name, EQ, val);
    }
    
    public void addWhere(String name, String op, char val) {
        addWhere(name, op, new Character(val));
    }

    /**
     * Added a WHERE constraint to the SELECT command.
     * 
     * @param name
     *            the field to constrain
     * @param val
     *            the value this field must be like
     */
    public void addWhereLike(String name, String val) {
        where.add("upper(" + name + ") like '"
                + escape(val.toString()).toUpperCase() + "' ");
    }

    /**
     */
    protected String getWhere() {
        StringBuffer sb = new StringBuffer();
        if (where.size() > 0) {
            sb.append(" WHERE ");
            for (int i = 0; i < where.size(); i++) {
                Object w = where.get(i);
                sb.append(w.toString());
                if (i < where.size() - 1) {
                    sb.append(and ? " AND " : " OR ");
                }
            }
        }
        return sb.toString();
    }

}
