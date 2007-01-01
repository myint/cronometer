/*
 *******************************************************************************
 * Copyright (c) 2005 Chris Rose and AIMedia
 * All rights reserved. DatasourceValidator and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Chris Rose
 *******************************************************************************/
package ca.spaz.cron.config;

public final class DatasourceValidator implements PropertyValidator {
   
   private DatasourceValidator() {
      // NO-OP
   }
   
   private static final DatasourceValidator instance = new DatasourceValidator();

   public boolean isValid(String key, String value) {
      if (key.startsWith("datasource.") || key.startsWith("db.")) {
         return false;
      }
      return true;
   }
   
   public boolean equals(Object o) {
      return (null != o && o.getClass() == this.getClass());
   }
   
   public static final DatasourceValidator getInstance() {
      return instance;
   }

}
