package ca.spaz.cron.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ca.spaz.cron.foods.ServingTable;


public class DeleteServingsAction extends AbstractAction {
    private ServingTable servingTable;

    public DeleteServingsAction(ServingTable servingTable) {
        super("Delete Servings");
        this.servingTable = servingTable;
        putValue(SHORT_DESCRIPTION, "Delete the selected servings from this list");

    }

    public void actionPerformed(ActionEvent e) {
        assert (servingTable != null);
        servingTable.deleteSelectedServings();
    }
}
