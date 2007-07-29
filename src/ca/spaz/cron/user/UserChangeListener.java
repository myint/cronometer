/*
 *******************************************************************************
 * Copyright (c) 2005 Chris Rose and AIMedia
 * All rights reserved. UserChangeListener and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Chris Rose
 *******************************************************************************/
package ca.spaz.cron.user;

/**
 * An interface to be implemented by objects interested in user model changes. 
 * @author Chris Rose
 */
public interface UserChangeListener {
   
   /**
    * This event is fired whenever the user model changes in some way.
    * @param userMan the <code>User</code> that has changed.
    */
   void userChanged(UserManager userMan);

}
