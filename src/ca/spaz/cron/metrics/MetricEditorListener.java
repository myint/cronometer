package ca.spaz.cron.metrics;


public interface MetricEditorListener {
   public void metricSelected(Metric metric);
   public void metricDoubleClicked(Metric metric);
   public void metricChosen(Metric metric);
}
