/*
 * Created on 31-Dec-2005
 */
package ca.spaz.cron.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import ca.spaz.cron.exercise.ExerciseTable;
import ca.spaz.util.ImageFactory;


public class DeleteExercisesAction extends AbstractAction {
   private ExerciseTable exerciseTable;
   
   public DeleteExercisesAction(ExerciseTable exerciseTable) {
      super("Delete Servings");
      this.exerciseTable = exerciseTable;
      putValue(SMALL_ICON, new ImageIcon(ImageFactory.getInstance().loadImage("/img/trash.gif")));
      putValue(SHORT_DESCRIPTION, "Delete the selected servings from this list");
       
   }      
   
   public void actionPerformed(ActionEvent e) {
      assert (exerciseTable != null);
      exerciseTable.deleteSelectedExercise();      
   }
}