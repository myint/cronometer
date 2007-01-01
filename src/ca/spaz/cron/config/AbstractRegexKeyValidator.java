/*
 *******************************************************************************
 * Copyright (c) 2005 Chris Rose and AIMedia
 * All rights reserved. AbstractRegexKeyValidator and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Chris Rose
 *******************************************************************************/
package ca.spaz.cron.config;

import java.util.regex.*;

public abstract class AbstractRegexKeyValidator implements PropertyValidator {

   protected Matcher getMatcher(String string) {
      if (null == string) {
         throw new IllegalArgumentException("Null strings are not allowed");
      }
      return Pattern.compile(getExpression()).matcher(string);
   }
   
   /* (non-Javadoc)
    * @see ca.spaz.cron.config.PropertyValidator#isValid(java.lang.String, java.lang.String)
    */
   public boolean isValid(String key, String value) {
      boolean valid = false;
      if (getMatcher(key).find()) {
         valid = doIsValid(key, value);
      }
      return valid;
   }
   
   /**
    * This method will be called only in the case where the pattern matches.  This
    * can be assumed to be a precondition of the method call.
    * 
    * @param key The matching key.
    * @param value The value being checked.
    * @return <code>true</code> if <code>value</code> is a valid value for
    * <code>key</code>, <code>false</code> otherwise.
    */
   protected abstract boolean doIsValid(String key, String value);
   
   /**
    * Get the regular expression to match the key on.
    * @return a string representation of a regular expression.
    */
   protected abstract String getExpression();
   
}
