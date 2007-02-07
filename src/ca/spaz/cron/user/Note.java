package ca.spaz.cron.user;

import java.util.Date;

import org.w3c.dom.Element;

import ca.spaz.util.XMLNode;

public class Note implements UserEntry {
   private long time = System.currentTimeMillis();
   private String note;
   
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
 
   
}
