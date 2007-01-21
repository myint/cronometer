package ca.spaz.cron.ui;

import ca.spaz.cron.user.Metric;

public interface MetricEditorListener {
   public void metricSelected(Metric metric);
   public void metricDoubleClicked(Metric metric);
   public void metricChosen(Metric metric);
}
