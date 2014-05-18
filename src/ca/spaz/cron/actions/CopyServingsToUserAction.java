package ca.spaz.cron.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ca.spaz.cron.Cronometer;
import ca.spaz.cron.foods.ServingTable;
import ca.spaz.cron.user.UserDatePickerDialog;
import ca.spaz.cron.user.UserManager;
import ca.spaz.gui.WrapperDialog;
import ca.spaz.util.Logger;

public class CopyServingsToUserAction extends AbstractAction {

    public CopyServingsToUserAction(ServingTable servingTable) {
        super("Copy Servings To...");
        if (UserManager.getUserManager().numberOfUsers() == 1) {
            this.setEnabled(false);
        }
        putValue(SHORT_DESCRIPTION, "Copy the selected servings to another user");
    }

    public void actionPerformed(ActionEvent e) {
        copyToUserDialog();
    }

    public static void copyToUserDialog() {
        Logger.debug("actionPerformed");
        assert (ServingTable.getServingTable() != null);
        UserDatePickerDialog userDP = new UserDatePickerDialog();
        WrapperDialog.showDialog(Cronometer.getInstance(), userDP, true);

        if ( !userDP.cancelPressed()) {
            Logger.debug("Date selected is: " + userDP.getDate().toString());
            ServingTable.getServingTable().copySelectedServingsToUser(userDP.getUser(), userDP.getDate());
        }
    }
}
