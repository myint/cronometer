/*
 * Created on 4-Jun-2005
 */
package ca.spaz.task;

public interface TaskListener {

   public void taskStarted(Task t);
   public void taskFinished(Task t);
   public void taskAborted(Task t);
   
}
