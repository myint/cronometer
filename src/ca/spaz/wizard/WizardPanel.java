/*
 * Created on 5-Jun-2005
 */
package ca.spaz.wizard;

import javax.swing.JPanel;

public abstract class WizardPanel extends JPanel {
   private Wizard controller;
   
   public abstract String getWizardPanelTitle();
   
   public abstract void commitChanges();
   
   /**
    * @return true if the wizard panel is completed and user input is valid
    * The Wizard will not allow proceeding to next pane unless the current
    * pane returns isValid.
    */
   public abstract boolean isValid();
   
   
   protected void setWizard(Wizard w) {
      this.controller = w;
   }
   
   
}
