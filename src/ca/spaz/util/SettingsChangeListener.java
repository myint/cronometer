/*
 * Created on 31-Aug-2005
 */
package ca.spaz.util;

public interface SettingsChangeListener extends java.util.EventListener {
    /**
     * This method gets called when a setting is added, removed or when
     * its value is changed.
     * <p>
     * @param evt A SettingsChangeEvent object describing the event source 
     *      and the setting that has changed.
     */
    void settingChange(SettingsChangeEvent evt);
}
