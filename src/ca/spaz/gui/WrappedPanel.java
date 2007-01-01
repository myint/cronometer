/*
 * Created on 7-Jan-2006
 */
package ca.spaz.gui;

import javax.swing.*;

public abstract class WrappedPanel extends JPanel {
   public abstract String getTitle();
   public abstract String getSubtitle();
   public abstract String getInfoString();
   public abstract ImageIcon getIcon();
      
   public abstract boolean isCancellable();
   public abstract void doCancel();
   public abstract void doAccept();     
   
   public boolean showSidebar() { 
      return true;
   }
}
