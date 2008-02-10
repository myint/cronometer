package ca.spaz.cron.notes;

import java.util.Date;

import org.w3c.dom.Element;

import ca.spaz.cron.records.Record;
import ca.spaz.util.XMLNode;

public class Note implements Record {
   private long time = System.currentTimeMillis();
   private String note;
   
   public Record copy() {
      Note n = new Note();
      n.setTime(time);
      setNote(note);
      return n;
   }
   
   public String getNote() {
      return note;
   }
   
   public void setDate(Date d) {
      time = d.getTime();
   }
   
   public void setTime(long t) {
      time = t;
   }

   public Date getDate() { 
      return new Date(time);
   }

   public void load(Element e) { 
      this.time = XMLNode.getLong(e, "time");
      this.note = XMLNode.getTextContent(e);
   }

   public XMLNode toXML() { 
      XMLNode node = new XMLNode("note");
      node.addAttribute("time", time);
      if (note != null) {
         node.setText(note);
      }
      return node;
   }

   public void setNote(String n) {
      this.note = n;
   }
 
   public boolean isLoaded() { 
      return true;
   }
}
