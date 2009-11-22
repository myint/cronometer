/*
 * Created on Feb 25, 2007 by davidson
 */
package ca.spaz.cron.exercise;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.ui.DailySummary;

public class ExercisePanel extends JPanel {

   private ExerciseTable exerciseTable;
   
   public ExercisePanel() {
      setLayout(new BorderLayout(4,4));
      setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
      add(getExerciseTable(),  BorderLayout.CENTER);
   }
   
   public ExerciseTable getExerciseTable() {
      if (null == exerciseTable) {
         exerciseTable = ExerciseTable.getExerciseTable();
         
         exerciseTable.addExerciseEditorListener(new ExerciseEditorListener() {
            public void exerciseChosen(Exercise e) {
               DailySummary ds = CRONOMETER.getDailySummary();
               if (ds.isOkToAddServings(ds.getDate(), false)) {
                  ds.addExercise(e);           
               }
            }
         });
      }
      return exerciseTable;
   }
} 
