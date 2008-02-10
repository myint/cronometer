package ca.spaz.cron.records; 

import java.util.Date;

import org.w3c.dom.Element;

import ca.spaz.util.XMLNode;

public interface Record {

   public Date getDate();

   public XMLNode toXML();
   
   public void load(Element e);

   public Record copy();
   
   public boolean isLoaded();
   
}
