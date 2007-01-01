/*
 * Created on 26-Nov-2005
 */
package ca.spaz.cron.ui;

import ca.spaz.cron.foods.Serving;

public interface ServingSelectionListener {
   public void servingSelected(Serving s);
   public void servingDoubleClicked(Serving s);
}
