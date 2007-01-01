package ca.spaz.cron.user;

import java.util.Date;

import org.w3c.dom.Element;

import ca.spaz.util.XMLNode;

public class Metric {
   public static final String WEIGHT = "Weight";
   public static final String BODY_TEMPERATURE = "Body Temperature";
   public static final String SYSTOLIC_BP = "Systolic BP";
   public static final String DIASTOLIC_BP = "Diastolic BP";
   public static final String RESTING_HEART_RATE = "Resting Heart Rate";
   public static final String BLOOD_GLUCOSE = "Blood Glucose";
   
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
   
   public Metric(Element e) {
      setName(e.getAttribute("name"));
      setValue(e.getAttribute("value"));
      setDate(new Date(Long.parseLong(e.getAttribute("date"))));     
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

   public boolean isWeight() {
      return getName().equals(WEIGHT);
   }
   
   public String toString() {
      return getName()+"-"+getDate()+"-"+getValue();
   }
}
