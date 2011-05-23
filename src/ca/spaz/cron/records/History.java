package ca.spaz.cron.records;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.user.UserManager;
import ca.spaz.gui.ErrorReporter;
import ca.spaz.util.*;

/**
 * A simple XML backing store taking the place of a lightweight database.
 * For storage and retrieval of timestamp based entries.
 * 
 * @todo: break into multiple files for better scaling
 * @todo: or wrap a www/database interface instead
 * 
 * @author adavidson
 */
public abstract class History {
 
   private boolean dirty = false;

   protected ArrayList entries = new ArrayList();
   
   public abstract String getBaseName();
   
   public abstract String getEntryTagName();
    
   public File getHistoryFile() {
      return new File(UserManager.getUserDirectory(UserManager.getCurrentUser()), getBaseName() + ".xml");
   }
   

   public File getOldHistoryFile() {
      return new File(UserManager.getUserDirectory(UserManager.getCurrentUser()), getBaseName() + ".bkp");
   }


   public File getTempHistoryFile() {
      return new File(UserManager.getUserDirectory(UserManager.getCurrentUser()), getBaseName() + ".tmp");
   }
   
   public History() {
      load();
   }
   
   /** 
    * Reload the history file.
    *
    */
   public void reload() {
      load();
   }
   /**
    * Add a new record to the history
    */
   public synchronized void addEntry(Record entry) {
      entries.add(entry);
      dirty = true;
      //Logger.debug("Add Entry: " + entry);
   }

   public synchronized List getEntriesOn(Date curDate) {     
      ArrayList res = new ArrayList();
      for (int i=0; i<entries.size(); i++) {
         Record entry = (Record)entries.get(i);
         if (ToolBox.isSameDay(entry.getDate(), curDate)) {
            res.add(entry);
         }
      }
      return res;
   }
   
   public List getEntries() {
      return entries;
   }    

   public void deleteEntry(Record entry) {
      entries.remove(entry);
      dirty = true;
      Logger.debug("Remove Entry: " + entry);      
   }

   public void deleteEntries(List list) {       
      entries.removeAll(list);
      dirty = true;
   }

   public void updateEntry(Record entry) {
      Logger.debug("Update Entry: " + entry);      
      dirty = true;
   }
   
   /**
    * Flush to disk.
    */
   public synchronized void save() {
      if (dirty) {
         try {
            File file = getHistoryFile();
            File tempFile = getTempHistoryFile(); 
            if (tempFile.exists()) {
               tempFile.delete();
            }
            PrintStream ps = new PrintStream(new BufferedOutputStream(
                  new FileOutputStream(tempFile)));
            writeXML(ps);
            ps.close();
            getOldHistoryFile().delete(); // delete old backup file
            file.renameTo(getOldHistoryFile()); // more current to backup
            file.delete(); // delete old current
            tempFile.renameTo(file); // move temp to become new current
         } catch (IOException e) {
            backupFile(getTempHistoryFile());
            e.printStackTrace();
            ErrorReporter.showError("An error occurred while trying to save.", e, CRONOMETER.getInstance());
         }
         dirty = false;
      }
   }
    
   public synchronized XMLNode toXML() {
      XMLNode node = new XMLNode(getBaseName());
      for (int i=0; i<entries.size(); i++) {
         Record entry = (Record)entries.get(i);
         if (entry.isLoaded()) {
            try {
               node.addChild(entry.toXML());
            } catch (Exception e) { e.printStackTrace(); }
         }
      }
      node.setPrintNewLines(true);    
      return node;
   }
   
   public synchronized void writeXML(PrintStream out) {     
      toXML().write(out);
   }
   
   public synchronized void load() {
      long start = System.currentTimeMillis();
      Logger.debug("Loading: " + getHistoryFile());
      if (!getHistoryFile().exists()) {
         Logger.debug("  --> file does not exist");
         return;
      }
      try {
         InputStream in = new BufferedInputStream(
               new FileInputStream(getHistoryFile()));
         load(in);
         in.close();
         long end = System.currentTimeMillis();
         Logger.debug("  --> Loaded in: " + (end-start) + " msec");
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (Exception e) {
         backupFile(getHistoryFile());
         e.printStackTrace();
         ErrorReporter.showError(e, CRONOMETER.getInstance()); 
      }
      dirty = false;
   }

   /**
    * Load Settings fresh from disk 
    * @throws ParserConfigurationException 
    * @throws IOException 
    * @throws SAXException 
    */
   public synchronized void load(InputStream in) throws ParserConfigurationException, SAXException, IOException {   
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document d = db.parse(in);
      Element e = d.getDocumentElement();
      
      NodeList nl = e.getElementsByTagName(getEntryTagName());
      for (int i=0; i<nl.getLength(); i++) {
         try {
            Record entry = loadUserEntry((Element)nl.item(i));
            if (entry != null) {
               if (entry.isLoaded()) {
                  addEntry(entry);
               }
            }
         } catch (Exception ex) {
            ErrorReporter.showError(ex, CRONOMETER.getInstance()); 
         }
      }     
   }

   private void backupFile(File f) {
      try {
         ToolBox.copyFile(f, new File(f.getParent(), System.currentTimeMillis() + "-"+f.getName()));      
      } catch (IOException e) { 
         e.printStackTrace();
      }      
   }

   public abstract Record loadUserEntry(Element item);

}
