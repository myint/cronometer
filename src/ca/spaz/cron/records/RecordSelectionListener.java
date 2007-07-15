/*
 * Created on Jun 30, 2007 by davidson
 */
package ca.spaz.cron.records;


public interface RecordSelectionListener {

   public void recordSelected(Record record);

   public void recordDoubleClicked(Record record);

   public void recordChosen(Record record);

}
