/*
 * Created on 15-Nov-2005
 */
package ca.spaz.cron.user;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.gui.ErrorReporter;
import ca.spaz.util.*;

/**
 * The Biometrics history for a user.
 * 
 * Currently uses a simple XML backing store
 * 
 * TODO: Manage data better (for instance, broken up by month or use proper DB)
 * 
 * @author Aaron Davidson
 */
public class BiometricsHistory { 
 
   private static final String METRICS_HISTORY_FILE = "metrics.xml";
   
   private ArrayList metrics;
   private boolean dirty = false;
   
   public BiometricsHistory() {
      metrics = new ArrayList();
      load();
   }
   
   /**
    * Add a new record of a Metric to the history
    */
   public synchronized void addMetric(Metric m) {
      metrics.add(m);
      dirty = true;
      Logger.debug("Add Metric: " + m);
   }

   public synchronized List getMetricsOn(Date curDate) {     
      ArrayList res = new ArrayList();
      for (int i=0; i<metrics.size(); i++) {
         Metric m = (Metric)metrics.get(i);
         if (ToolBox.isSameDay(m.getDate(),curDate)) {
            res.add(m);
         }
      }
      return res;
   }
   
   public List getMetrics() {
      return metrics;
   }
   
   public List getMetricsOfType(String type) {
      ArrayList res = new ArrayList();
      for (int i=0; i<metrics.size(); i++) {
         Metric m = (Metric)metrics.get(i);
         if (m.getName().equals(type)) {
            res.add(m);
         }
      }
      return res;
   }

   public void delete(Metric m) {
      metrics.remove(m);
      dirty = true;
      Logger.debug("Remove Metric: " + m);      
   }

   public void update(Metric m) {
      dirty = true;
   }
   
   public File getHistoryFile() {
      return new File(User.getUserDirectory(), METRICS_HISTORY_FILE);
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
      XMLNode node = new XMLNode("metrics");
      for (int i=0; i<metrics.size(); i++) {
         Metric m = (Metric)metrics.get(i);
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
            Metric m = new Metric((Element)nl.item(i));
            addMetric(m);
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
