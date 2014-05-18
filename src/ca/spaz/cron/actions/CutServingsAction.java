package ca.spaz.cron.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ca.spaz.cron.foods.ServingTable;


public class CutServingsAction extends AbstractAction {
    private ServingTable servingTable;

    public CutServingsAction(ServingTable servingTable) {
        super("Cut Servings");
        this.servingTable = servingTable;
        putValue(SHORT_DESCRIPTION, "Cut the selected servings from this list");
    }

    public void actionPerformed(ActionEvent e) {
        assert (servingTable != null);
        servingTable.cutSelectedServings();
    }
}
