/*
 *******************************************************************************
 * Copyright (c) 2005 Chris Rose and AIMedia
 * All rights reserved. Target and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Chris Rose
 *******************************************************************************/
package ca.spaz.cron.targets;

public class Target {
   double min, max;

   public Target() {}
   
   public Target(double min, double max) {
      this.min = min;
      this.max = max;
   }
   
   public double getMax() {
      return max;
   }

   public void setMax(double max) {
      this.max = max;
   }

   public double getMin() {
      return min;
   }

   public void setMin(double min) {
      this.min = min;
   }

   public boolean isUndefined() {
      return min <= 0 && max <= 0;
   }
}
