package ca.spaz.cron.user;

import java.util.Date;
import java.lang.Comparable;

import org.w3c.dom.Element;

import ca.spaz.util.XMLNode;

public class Metric implements Comparable, UserEntry {
  
   private String name;
   private Number value;
   private Date date;
   
   public Metric() {
      setDate(new Date());
   }
   
   public Metric(String name, Date d) {
      setName(name);
      setDate(d);
   }   
   
   public Metric(Biomarker biomarker) {
      setName(biomarker.getName());
   }
   
   public Metric(Element e) {
      load(e);
   }
   
   public void load(Element e) {
      setName(e.getAttribute("name"));
      setValue(e.getAttribute("value"));
      setDate(new Date(Long.parseLong(e.getAttribute("date"))));     
   }
   
   /**
    * Compares two metrics by date for sorting.
    */
   public int compareTo(Object object) {
      return date.compareTo(((Metric)object).getDate());
   }
   
   public Date getDate() {
      return date;
   }
   
   public void setDate(Date date) {
      this.date = date;
   }
   
   public String getName() {
      return name;
   }
   
   public void setName(String name) {
      this.name = name;
   }
   
   public Number getValue() {
      return value;
   }
   
   public void setValue(String value) {
      this.value = new Double(value);
   }
   
   public void setValue(Number value) {
      this.value = value;
   }
   
   
   public XMLNode toXML() {
      XMLNode node = new XMLNode("metric");
      node.addAttribute("name",getName());
      node.addAttribute("date",getDate().getTime());
      node.addAttribute("value",getValue().doubleValue());
      return node;
   }

//   public boolean isWeight() {
//      return getName().equals(WEIGHT);
//   }
   
   public String toString() {
      return getName()+"-"+getDate()+"-"+getValue();
   }
}
