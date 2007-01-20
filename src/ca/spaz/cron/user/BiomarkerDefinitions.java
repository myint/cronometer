package ca.spaz.cron.user;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.gui.ErrorReporter;
import ca.spaz.util.*;

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

   private static final String BIOMARKER_DEFINITIONS_FILE = "biomarkerDefinitions.xml";

   private ArrayList biomarkers;
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
      return new File(User.getUserDirectory(), BIOMARKER_DEFINITIONS_FILE);
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
      XMLNode node = new XMLNode("metrics");
      for (int i=0; i<biomarkers.size(); i++) {
         Metric m = (Metric)biomarkers.get(i);
         if (m.getValue() != null) {
            node.addChild(m.toXML());
         }
      }
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

