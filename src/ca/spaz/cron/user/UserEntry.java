package ca.spaz.cron.user;

import java.util.Date;

import org.w3c.dom.Element;

import ca.spaz.util.XMLNode;

public interface UserEntry {

   public Date getDate();

   public XMLNode toXML();
   
   public void load(Element e);
   
}
