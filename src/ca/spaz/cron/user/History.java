package ca.spaz.cron.user;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.gui.ErrorReporter;
import ca.spaz.util.*;

/**
 * A simple XML backing store taking the place of a lightweight database.
 * For storage and retreival of timestamp based entries.
 * 
 * @author adavidson
 */
public abstract class History {
 
   private boolean dirty = false;

   private ArrayList entries = new ArrayList();
   
   public abstract String getBaseName();
    
   public File getHistoryFile() {
      return new File(User.getUserDirectory(), getBaseName() + ".xml");
   }

   /**
    * Add a new record to the history
    */
   public synchronized void addEntry(UserEntry entry) {
      entries.add(entry);
      dirty = true;
      Logger.debug("Add Entry: " + entry);
   }

   public synchronized List getEntriesOn(Date curDate) {     
      ArrayList res = new ArrayList();
      for (int i=0; i<entries.size(); i++) {
         UserEntry entry = (UserEntry)entries.get(i);
         if (ToolBox.isSameDay(entry.getDate(), curDate)) {
            res.add(entry);
         }
      }
      return res;
   }
   
   public List getEntries() {
      return entries;
   }    

   public void delete(UserEntry entry) {
      entries.remove(entry);
      dirty = true;
      Logger.debug("Remove Entry: " + entry);      
   }

   public void update(UserEntry entry) {
      Logger.debug("Update UserEntry: " + entry);      
      dirty = true;
   }
   
   /**
    * Flush to disk.
    */
   public synchronized void save() {
      if (dirty) {
         try {
            PrintStream ps = new PrintStream(new BufferedOutputStream(
                  new FileOutputStream(getHistoryFile())));
            writeXML(ps);
            ps.close();
         } catch (IOException e) {
            e.printStackTrace();
            ErrorReporter.showError(e, CRONOMETER.getInstance());
         }
         dirty = false;
      }
   }
   
   
   public synchronized void writeXML(PrintStream out) {
      XMLNode node = new XMLNode("entries");
      for (int i=0; i<entries.size(); i++) {
         UserEntry entry = (UserEntry)entries.get(i);
         node.addChild(entry.toXML());
      }
      node.setPrintNewLines(true);      
      node.write(out);
   }
   
   public synchronized void load() {
      Logger.debug("Loading from disk");
      try {
         InputStream in = new BufferedInputStream(
               new FileInputStream(getHistoryFile()));
         load(in);
         in.close();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
         ErrorReporter.showError(e, CRONOMETER.getInstance()); 
      }   
   }

   /**
    * Load Settings fresh from disk 
    */
   public synchronized void load(InputStream in) {
      try {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         dbf.setNamespaceAware(true);
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document d = db.parse(in);
         Element e = d.getDocumentElement();
         
         NodeList nl = e.getElementsByTagName("metric");
         for (int i=0; i<nl.getLength(); i++) {
            UserEntry entry = loadUserEntry((Element)nl.item(i));
            addEntry(entry);
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
         ErrorReporter.showError(e, CRONOMETER.getInstance()); 
      }
      dirty = false;
   }

   public abstract UserEntry loadUserEntry(Element item);

}
