/*
 *******************************************************************************
 * Copyright (c) 2005 Chris Rose and AIMedia
 * All rights reserved. ProgressListener and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Chris Rose
 *******************************************************************************/
package ca.spaz.util;

/**
 * This interface should be implemented by classes interested in being informed of the
 * progress of some other thread.
 *  
 * @author Chris Rose
 */
public interface ProgressListener {

   /**
    * This event is fired when the progress item being tracked starts.
    */
   void progressStart();
   
   /**
    * This event is fired when the progress item being tracked finishes.  At this point,
    * the item's progress should be considered to be 100%
    */
   void progressFinish();
   
   /**
    * This event is fired whenever the progress item being tracked updates its public
    * progress level.
    * 
    * @param percent A number from 0 to 100 indicating the progress of the tracked item.
    */
   void progress(int percent);
   
}
