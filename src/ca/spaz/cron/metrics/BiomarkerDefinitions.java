package ca.spaz.cron.metrics;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.user.UserManager;
import ca.spaz.gui.ErrorReporter;
import ca.spaz.util.Logger;
import ca.spaz.util.XMLNode;

/**
 * The Biomarkers definitions for a user.
 * 
 * Currently uses a simple XML backing store
 * 
 * TODO: Manage data better (for instance, broken up by month or use proper DB)
 * 
 * @author Gerald
 */
public class BiomarkerDefinitions { 

   private static final String BIOMARKER_DEFINITIONS_FILE = "biomarkers.xml";

   private List biomarkers;
   private boolean dirty = false;

   public BiomarkerDefinitions() {
      biomarkers = new ArrayList();
      load();
   }

   /**
    * Add a new record of a biomarker
    */
   public synchronized void addBiomarker(Biomarker biomarker) {
      biomarkers.add(biomarker);
      dirty = true;
      Logger.debug("Add Biomarker: " + biomarker);
   }

   public List getBiomarkers() {
      return biomarkers;
   }
   
   public List getEnabledBiomarkers() {
      List enabledBiomarkers = new ArrayList();
      for (Iterator iter = biomarkers.iterator(); iter.hasNext();) {
         Biomarker biomarker = (Biomarker) iter.next();
         if (biomarker.isEnabled()) {
            enabledBiomarkers.add(biomarker);
         }
      }
      return enabledBiomarkers;
   } 
   
   public Biomarker getBiomarker(String name) {
      for (Iterator iter = biomarkers.iterator(); iter.hasNext();) {
         Biomarker biomarker = (Biomarker) iter.next();
         if (biomarker.getName().equals(name)) {
            return biomarker;
         }
      }
      return null;
   }     

   public void delete(Biomarker biomarker) {
      biomarkers.remove(biomarker);
      dirty = true;
      Logger.debug("Remove Biomarker: " + biomarker);      
   }

   public void update(Biomarker  biomarker) {
      Logger.debug("Update Biomarker: " + biomarker);      
      dirty = true;
   }

   public File getBiomarkersDefinitionsFile() {
      return new File(UserManager.getUserDirectory(UserManager.getCurrentUser()), BIOMARKER_DEFINITIONS_FILE);
   }

   /**
    * Flush to disk.
    */
   public synchronized void save() {
      if (dirty) {
         try {
            PrintStream ps = new PrintStream(new BufferedOutputStream(
                  new FileOutputStream(getBiomarkersDefinitionsFile())));
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
      XMLNode node = new XMLNode("biomarkers");
      for (int i=0; i<biomarkers.size(); i++) {
         Biomarker biomarker = (Biomarker)biomarkers.get(i);
         node.addChild(biomarker.toXML());
      }
      node.setPrintNewLines(true);
      node.write(out);
   }

   public synchronized void load() {
      Logger.debug("Loading from disk");
      try {
         InputStream in = new BufferedInputStream(
               new FileInputStream(getBiomarkersDefinitionsFile()));
         load(in);
         in.close();
      } catch (FileNotFoundException e) {
         // If the file does not exist, create it with the pre-defined biomarkers
         createFile();
      } catch (Exception e) {
         e.printStackTrace();
         ErrorReporter.showError(e, CRONOMETER.getInstance()); 
      }   
   }

   /**
    * Creates the pre-defined biomarkers and flushes to disk
    */
   private void createFile() {
      for (Iterator iter = Biomarker.createPredefinedBiomarkers().iterator(); iter.hasNext();) {
         Biomarker biomarker = (Biomarker) iter.next();
         addBiomarker(biomarker);
      }
      save();
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

         NodeList nl = e.getElementsByTagName("biomarker");
         for (int i=0; i<nl.getLength(); i++) {
            Biomarker biomarker = new Biomarker((Element)nl.item(i));
            addBiomarker(biomarker);
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
         ErrorReporter.showError(e, CRONOMETER.getInstance()); 
      }
      dirty = false;
   }
}

