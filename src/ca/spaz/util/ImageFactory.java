/*
 *******************************************************************************
 * Copyright (c) 2005 Chris Rose and AIMedia
 * All rights reserved. ImageFactory and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Chris Rose
 *******************************************************************************/
package ca.spaz.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Map;

/**
 * A class for manufacturing images from the resource path. 
 * @author Chris Rose
 */
public class ImageFactory {

   private static ImageFactory instance = null;
   private int cacheSize;
   private Map imageCache;
   
   private ImageFactory(int cacheSz) {     
      this.cacheSize = cacheSz;
      this.imageCache = new CacheMap(cacheSize);
   }
   
   public static final ImageFactory getInstance() {
      if (null == instance) {
         instance = new ImageFactory(10);
      }
      return instance;
   }
   
   public Image loadImage(URL url) {
      Image ret = null;
      if (imageCache.containsKey(url)) {
         ret = (Image) imageCache.get(url);
      } else {
         ret = Toolkit.getDefaultToolkit().createImage(url);
         if (ret != null) {
            imageCache.put(url, ret);
         }
      }
      return ret;
   }
   
   public Image loadImage(String resourceID) {
      return loadImage(resourceID, this); 
   }
   
   public Image loadImage(String resourceID, Object source) {
      Class base = null;
      if (null == source) {
         base = this.getClass();
      } else {
         base = source.getClass();
      }
      URL url = base.getResource(resourceID);
      Image ret = null;
      if (url == null) {
         url = this.getClass().getResource(resourceID);
         if (url == null) {
            throw new IllegalArgumentException(resourceID + " is not a valid resource identifier");
         }
      }
      ret = loadImage(url);
      return ret;
   }
   
}
