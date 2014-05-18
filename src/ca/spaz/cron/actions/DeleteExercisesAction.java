package ca.spaz.cron.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ca.spaz.cron.exercise.ExerciseTable;


public class DeleteExercisesAction extends AbstractAction {
    private ExerciseTable exerciseTable;

    public DeleteExercisesAction(ExerciseTable exerciseTable) {
        super("Delete Servings");
        this.exerciseTable = exerciseTable;
        putValue(SHORT_DESCRIPTION, "Delete the selected servings from this list");

    }

    public void actionPerformed(ActionEvent e) {
        assert (exerciseTable != null);
        exerciseTable.deleteSelectedExercise();
    }
}
