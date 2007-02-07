 
package ca.spaz.cron.user;

import java.util.ArrayList;

import org.w3c.dom.Element;

/**
 * The notes (daily diary) history for a user. 
 * 
 * @author Aaron Davidson
 */
public class NotesHistory extends History { 
 
   private static final String NOTES_HISTORY_FILE = "notes.xml";
   
   private ArrayList notes;
   
   public NotesHistory() {
      notes = new ArrayList();
      load();
   }
   
   public String getBaseName() { 
      return "notes";
   }

   public UserEntry loadUserEntry(Element e) {
      Note note = new Note();
      note.load(e);
      return note;
   } 
   
}
