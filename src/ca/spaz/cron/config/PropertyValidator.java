/*
 *******************************************************************************
 * Copyright (c) 2005 Chris Rose and AIMedia
 * All rights reserved. PropertyValidator and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Chris Rose
 *******************************************************************************/
package ca.spaz.cron.config;

/**
 * An interface for validation of property values. 
 * @author Chris Rose
 */
public interface PropertyValidator {

   /**
    * Validate the specified value for the key.
    * @param key The property key to test.
    * @param value The property value to test.
    * @return <code>true</code> if the value is a valid one for the specified key,
    * <code>false</code> otherwise.
    */
   boolean isValid(String key, String value);
   
}
