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


public class CopyExercisesAction extends AbstractAction {
   private ExerciseTable exerciseTable;
   
   public CopyExercisesAction(ExerciseTable exerciseTable) {
      super("Copy Exercises");
      this.exerciseTable = exerciseTable;
      putValue(SMALL_ICON, new ImageIcon(ImageFactory.getInstance().loadImage("/img/copy.gif")));
      putValue(SHORT_DESCRIPTION, "Copy the selected exercises from this list");
       
   }
   
   public void actionPerformed(ActionEvent e) {
      assert (exerciseTable != null);
      exerciseTable.copySelectedExercises();      
   }
}