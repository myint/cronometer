package ca.spaz.cron.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ca.spaz.cron.exercise.ExerciseTable;


public class CutExercisesAction extends AbstractAction {
    private ExerciseTable exerciseTable;

    public CutExercisesAction(ExerciseTable exerciseTable) {
        super("Cut Exercises");
        this.exerciseTable = exerciseTable;
        putValue(SHORT_DESCRIPTION, "Cut the selected exercises from this list");
    }

    public void actionPerformed(ActionEvent e) {
        assert (exerciseTable != null);
        exerciseTable.cutSelectedExercises();
    }
}
