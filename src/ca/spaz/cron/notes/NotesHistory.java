 
package ca.spaz.cron.notes;

import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;

import ca.spaz.cron.records.History;
import ca.spaz.cron.records.Record;

/**
 * The notes (daily diary) history for a user. 
 * 
 * @author Aaron Davidson
 */
public class NotesHistory extends History { 
 
   private static final String NOTES_HISTORY_FILE = "notes.xml";
    
   
   public NotesHistory() { 
      super();
   }
   
   public String getBaseName() { 
      return "notes";
   }
   

   public String getEntryTagName() { 
      return "note";
   }

   public Record loadUserEntry(Element e) {
      Note note = new Note();
      note.load(e);
      return note;
   }

   public synchronized String getNote(Date date) { 
      String str = null;
      List list = getEntriesOn(date);
      if (list != null) {
         if (list.size() > 0) {
            Note note = (Note)list.get(0);
            str = note.getNote();
         }
      }
      return str;
   }

   public synchronized void setNote(String note, Date d) {
      if (note != null && note.length() == 0) {
         note = null;
      }
      List list = getEntriesOn(d);
      if (list.size() > 0){
         Note n = (Note)list.get(0);
         if (note == null) {
            deleteEntry(n);
         } else if (!n.getNote().equals(note)) {
            n.setNote(note);
            updateEntry(n);
         }
      } else if (note != null) {
         Note n = new Note();
         n.setDate(d);
         n.setNote(note);
         addEntry(n);
      }
   } 
   
}
