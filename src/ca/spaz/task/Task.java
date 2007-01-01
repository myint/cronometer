/*
 * Created on 4-Jun-2005
 */
package ca.spaz.task;

public interface Task extends Runnable {

   /**
    * A number between 0 and 100 representing the 
    * % completion of the task.
    * 
    * @return the percentage complete
    */
   public int getTaskProgress();
      
   /**
    * The task has been cancelled. Attempt to stop.
    */
   public void abortTask();

   /**
    * Return true if the program can finish.
    * 
    * @return true if the task can be aborted.
    */
   public boolean canAbortTask();
   
   /**
    * A description of the current Task being performed
    * 
    * @return the description of the current Task being performed
    */
   public String getTaskDescription();
   
}
