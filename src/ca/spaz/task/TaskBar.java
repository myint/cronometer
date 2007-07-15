/*
 * Created on 4-Jun-2005
 */
package ca.spaz.task;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

/**
 * A TaskBar is a Swing component that can safely run a Task in a background
 * thread, display progress of the task, interact with the task control,
 * and fire events to report task progress. 
 * 
 * @see Task
 * @see TaskListener
 * 
 * @author Aaron Davidson
 */
public class TaskBar extends JPanel {
   
   private Task curTask;
   private JProgressBar progressBar;
   private JButton abortBtn;
   private Timer timer;
   private boolean aborting;
   private ArrayList listeners;
   
   public TaskBar() {      
      setLayout(new BorderLayout(4,4));
      add(getProgressBar(), BorderLayout.CENTER);
      add(getAbortButton(), BorderLayout.EAST);
   }
     
   public void setOpaque(boolean v) {
      super.setOpaque(v);
      getProgressBar().setOpaque(v);
      getAbortButton().setOpaque(v);
   }
   
   private JProgressBar getProgressBar() {
      if (progressBar == null) {
         progressBar = new JProgressBar(JProgressBar.HORIZONTAL);         
         progressBar.setValue(0);
         progressBar.setMinimum(0);
         progressBar.setMaximum(100);
      }
      return progressBar;
   }
   
   public JButton getAbortButton() {
      if (abortBtn == null) {
         abortBtn = new JButton("Stop");
         abortBtn.setEnabled(false);
         abortBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               abortCurrentTask();
            }
         });
      }
      return abortBtn;
   }
   
   public void abortCurrentTask() {
      if (curTask != null) {
         aborting = true;
         curTask.abortTask();
         progressBar.setString("Aborting...");                  
      }
   }    
   private Timer getTimer() {
      if (timer == null) {
         timer = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               updateProgressBar();
            }
         });
         timer.setCoalesce(true);
         timer.setRepeats(true);
      }
      return timer;
   }
   
   /**
    * Start the given task. 
    * 
    * @param t a Task to execute
    */
   public void executeTask(Task t) {
      abortCurrentTask();
      this.curTask = t;
      fireTaskStarted();
      getProgressBar().setValue(0);
      getProgressBar().setStringPainted(true);
      getAbortButton().setVisible(curTask.canAbortTask());
      getAbortButton().setEnabled(curTask.canAbortTask());
      Thread taskThread = new Thread(new Runnable() {
         public void run() {
            aborting = false;
            if (curTask != null) {
               curTask.run();
            }
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  getTimer().stop();
                  getProgressBar().setStringPainted(false);
                  if (aborting) {
                     fireTaskAborted();
                  } else {
                     fireTaskFinished();
                  }
                  // Ensure that the progress bar gets set to the right value.
                  if (getProgressBar().getValue() != 100) {
                     getProgressBar().setValue(0);
                  }
                  getAbortButton().setEnabled(false);
                  curTask = null;
                  getProgressBar().setStringPainted(false);
               }
            });
         }
      }, "TaskBar");
      taskThread.start();
      getTimer().start();
   }
   
   private void updateProgressBar() {
      if (curTask != null && !aborting) {
         getProgressBar().setValue(curTask.getTaskProgress());
         getProgressBar().setString(curTask.getTaskDescription());
      }
   }

   /**
    * Adds a task listener to this object to receive events on
    * a task's progress.
    * 
    * @param tl a task listener 
    */
   public synchronized void addTaskListener(TaskListener tl) {
      if (null == listeners) {
         listeners = new ArrayList();
      }
      listeners.add(tl);
   }

   /**
    * Remove a task listener from the TaskBar
    * 
    * @param tl a task listener 
    */
   public synchronized void removeTaskListener(TaskListener tl) {
      if (listeners != null) {
         listeners.remove(tl);
      }
   }
   
   private synchronized void fireTaskStarted() {
      if (listeners != null) {
        Iterator iterator = listeners.iterator();
        while (iterator.hasNext()) {
           TaskListener tl = (TaskListener)iterator.next();
           tl.taskStarted(curTask);
        }
      }
   }

   private synchronized void fireTaskAborted() {
      if (listeners != null) {
        Iterator iterator = listeners.iterator();
        while (iterator.hasNext()) {
           TaskListener tl = (TaskListener)iterator.next();
           tl.taskAborted(curTask);
        }
      }
   }

   private synchronized void fireTaskFinished() {
      // Ensure that the progress bar gets set to the right value.
      if (getProgressBar().getValue() != 100) {
         getProgressBar().setValue(100);
      }
      if (listeners != null) {
        Iterator iterator = listeners.iterator();
        while (iterator.hasNext()) {
           TaskListener tl = (TaskListener)iterator.next();
           tl.taskFinished(curTask);
        }
      }
   }


}
