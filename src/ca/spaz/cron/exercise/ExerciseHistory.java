package ca.spaz.cron.exercise;

import java.util.*;

import org.w3c.dom.Element;

import ca.spaz.cron.records.History;
import ca.spaz.cron.records.Record;

/**
 * The Exercise history for a user.
 */
public class ExerciseHistory extends History {
  
   public String getBaseName() {
      return "exercises";
   }

   public String getEntryTagName() {
      return "exercise";
   }

   public Record loadUserEntry(Element item) {
      return new Exercise(item);
   }
   
   /**
    * Add a new record of a Serving to the history
    */
   public synchronized void addExercise(Exercise c) {
      addEntry(c);
   }

   public synchronized List getConsumedOn(Date curDate) {     
      return getEntriesOn(curDate);
   }
   
   /**
    * Copies the servings from one day to another.
    * @param fromDate the day to copy from	
    * @param toDate the day to copy to
    * @return the copied servings
    */
   public synchronized List copyExercisesOn(Date fromDate, Date toDate) {
	   List prevConsumed = getConsumedOn(fromDate);
	   List consumed = new ArrayList();
	   Iterator iter = prevConsumed.iterator();
	   while (iter.hasNext()) {
	      Exercise exercise = new Exercise((Exercise) iter.next());
	      exercise.setDate(toDate);
		   addExercise(exercise);
	   }
	   return consumed;
   }
   
   public synchronized void deleteExercises(List list) {
      super.deleteEntries(list);
   }

   public synchronized void delete(Exercise exercise) {
      super.deleteEntry(exercise);
   }

   public void update(Exercise exercise) {
      super.updateEntry(exercise);
   }
          
}
