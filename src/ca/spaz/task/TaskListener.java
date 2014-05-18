package ca.spaz.task;

public interface TaskListener {

    public void taskStarted(Task t);
    public void taskFinished(Task t);
    public void taskAborted(Task t);
}
