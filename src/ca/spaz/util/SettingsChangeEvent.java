/*
 * Created on 31-Aug-2005
 */
package ca.spaz.util;

import java.util.EventObject;

public class SettingsChangeEvent extends EventObject {

   private String key;
   private String newValue;
   
   public SettingsChangeEvent(Object source, String key, String val) {
      super(source);
      this.key = key;
      this.newValue = val;
   }
   
   /**
    * Returns the key of the setting that was changed.
    *
    * @return  The key of the setting that was changed.
    */
   public String getKey() {
      return key;
   }
   
   /**
    * Returns the new value for the setting.
    *
    * @return  The new value for the setting, or <tt>null</tt> if the
    *          setting was removed.
    */
   public String getNewValue() {
      return newValue;
   }
}
