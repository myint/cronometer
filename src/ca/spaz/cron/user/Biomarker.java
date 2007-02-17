package ca.spaz.cron.user;

import java.util.*;

import org.w3c.dom.Element;

import ca.spaz.util.XMLNode;

/**
 * Biomarker is the definition of a biological measurement the user wishes to track to monitor their health.
 * There are several biomarkers pre-defined in the system and the user can define additional ones. 
 *  
 * @author Gerald
 */
public class Biomarker {
   String name = "";
   String units = ""; 
   double min;
   double max; 
   boolean enabled = true;
   boolean showMovingAverage = true;
   int movingAverageDays = 7;
   
   public Biomarker() {
   }
   
   public Biomarker(Element e) {
      setName(e.getAttribute("name"));
      setUnits(e.getAttribute("units"));
      setMin(Double.parseDouble(e.getAttribute("min")));
      setMax(Double.parseDouble(e.getAttribute("max")));     
      setEnabled(Boolean.getBoolean(e.getAttribute("enabled")));
      setShowMovingAverage(Boolean.getBoolean(e.getAttribute("showMovingAverage")));
      setMovingAverageDays(Integer.parseInt(e.getAttribute("movingAverageDays")));
   }   
  
   public XMLNode toXML() {
      XMLNode node = new XMLNode("biomarker");
      node.addAttribute("name", getName());
      node.addAttribute("units", getUnits());
      node.addAttribute("min", getMin());
      node.addAttribute("max", getMax());
      node.addAttribute("enabled", Boolean.toString(enabled));
      node.addAttribute("showMovingAverage", Boolean.toString(showMovingAverage));
      node.addAttribute("movingAverageDays", Integer.toString(movingAverageDays));
      return node;
   }
   
   public boolean isEnabled() {
      return enabled;
   }
   
   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
   
   public double getMax() {
      return max;
   }
   
   public void setMax(double max) {
      this.max = max;
   }
   
   public double getMin() {
      return min;
   }
   
   public void setMin(double min) {
      this.min = min;
   }
   
   public int getMovingAverageDays() {
      return movingAverageDays;
   }
   
   public void setMovingAverageDays(int movingAverageDays) {
      this.movingAverageDays = movingAverageDays;
   }
   
   public String getName() {
      return name;
   }
   
   public void setName(String name) {
      this.name = name;
   }
   
   public boolean isShowMovingAverage() {
      return showMovingAverage;
   }
   
   public void setShowMovingAverage(boolean showMovingAverage) {
      this.showMovingAverage = showMovingAverage;
   }
   
   public String getUnits() {
      return units;
   }
   
   public void setUnits(String units) {
      this.units = units;
   }
   
   public String toString() {
      return name;
   }
   
   public static List createPredefinedBiomarkers() {
      List biomarkers = new ArrayList();
      Biomarker biomarker = new Biomarker();
      biomarker.setName("Weight");
      biomarker.setUnits("lbs");
      biomarker.setMax(1000);
      biomarker.setMin(1);
      biomarkers.add(biomarker);
      
      biomarker = new Biomarker();
      biomarker.setName("Body Temperature");
      biomarker.setUnits("degrees");
      biomarker.setMax(120);
      biomarker.setMin(1);
      biomarkers.add(biomarker);  
      
      biomarker = new Biomarker();
      biomarker.setName("Systolic BP");
      biomarker.setUnits("mm Hg");
      biomarker.setMax(1000);
      biomarker.setMin(1);
      biomarkers.add(biomarker); 
      
      biomarker = new Biomarker();
      biomarker.setName("Diastolic BP");
      biomarker.setUnits("mm Hg");
      biomarker.setMax(1000);
      biomarker.setMin(1);
      biomarkers.add(biomarker); 
      
      biomarker = new Biomarker();
      biomarker.setName("Resting Heart Rate");
      biomarker.setUnits("bpm");
      biomarker.setMax(120);
      biomarker.setMin(1);
      biomarkers.add(biomarker); 
      
      biomarker = new Biomarker();
      biomarker.setName("Blood Glucose");
      biomarker.setUnits("mg/dl");
      biomarker.setMax(1000);
      biomarker.setMin(1);
      biomarkers.add(biomarker);       
      return biomarkers;
   }
}
