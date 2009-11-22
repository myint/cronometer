/*
 * Created on 31-Dec-2005
 */
package ca.spaz.cron.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import ca.spaz.cron.exercise.ExerciseTable;
import ca.spaz.cron.foods.ServingTable;
import ca.spaz.util.ImageFactory;


public class CutExercisesAction extends AbstractAction {
   private ExerciseTable exerciseTable;
   
   public CutExercisesAction(ExerciseTable exerciseTable) {
      super("Cut Exercises");
      this.exerciseTable = exerciseTable;
      putValue(SMALL_ICON, new ImageIcon(ImageFactory.getInstance().loadImage("/img/cut.gif")));
      putValue(SHORT_DESCRIPTION, "Cut the selected exercises from this list");
   }
   
   public void actionPerformed(ActionEvent e) {
      assert (exerciseTable != null);
      exerciseTable.cutSelectedExercises();      
   }
}