package ca.spaz.cron.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ca.spaz.cron.foods.ServingTable;


public class CopyServingsAction extends AbstractAction {
    private ServingTable servingTable;

    public CopyServingsAction(ServingTable servingTable) {
        super("Copy Servings");
        this.servingTable = servingTable;
        putValue(SHORT_DESCRIPTION, "Copy the selected servings from this list");

    }

    public void actionPerformed(ActionEvent e) {
        assert (servingTable != null);
        servingTable.copySelectedServings();
    }
}
