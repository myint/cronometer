/*
 * Created on 5-Dec-2005
 */
package ca.spaz.util;

import java.io.PrintStream;
import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.Attributes;

/**
 * Simple object class to represent XML and can print itself easily
 */
public class XMLNode {
   private String name;
   private String text;
   private HashMap attrs = new HashMap();
   private ArrayList children = new ArrayList();
   private boolean printNewLines = false;
   
   public XMLNode(String name) {
      this.name = name;
   }
   
   public XMLNode(Element e) {
      XMLNode node = new XMLNode(e.getNodeName());
      for (int i=0; i<e.getAttributes().getLength(); i++) {
         String name = e.getAttributes().item(i).getNodeName();
         String value = e.getAttributes().item(i).getNodeValue();
         node.addAttribute(name, value);
      }
      node.setText(getTextContent(e));
      // todo: include children
   }

   public void setText(String str) {
      this.text = str;
   }
   
   public void addAttribute(String key, String val) {
      attrs.put(key, val);
   }
   
   public void addAttribute(String key, Object val) {
      if (val != null) {
         attrs.put(key, val.toString());
      }
   }
   
   public void addAttribute(String key, int val) {
      attrs.put(key, Integer.toString(val));
   }

   public void addAttribute(String key, double val) {
      attrs.put(key, Double.toString(val));
   }
   
   public void addAttribute(String key, float val) {
      attrs.put(key, Float.toString(val));
   }
   
   public void addAttribute(String key, long val) {
      attrs.put(key, Long.toString(val));
   }
   
   public void addAttribute(String key, short val) {
      attrs.put(key, Short.toString(val));
   }
   
   public void addAttribute(String key, byte val) {
      attrs.put(key, Byte.toString(val));
   }
   
   public void addAttribute(String key, char val) {
      attrs.put(key, Character.toString(val));
   }
   
   public void addAttribute(String key, boolean val) {
      attrs.put(key, Boolean.toString(val));
   }
   
   public void addChild(XMLNode node) {
      children.add(node);
   }
   
   public void write(PrintStream out) {
      out.print(toString());
   }
   
   public String toString() {
      boolean singleton = (children.size() == 0 && text == null);
      StringBuffer sb = new StringBuffer();
      sb.append('<');
      sb.append(name);
      
      Iterator iter = attrs.keySet().iterator();
      while (iter.hasNext()) {
         String key = (String)iter.next();
         sb.append(' ');
         sb.append(key);
         sb.append('=');
         sb.append('"');
         sb.append(escapeXML((String)attrs.get(key)));
         sb.append('"');
      }
      if (singleton) {
         sb.append('/');
      }
      sb.append('>');      
      if (printNewLines && text == null) {
         sb.append('\n');
      }
      if (text != null) {          
         sb.append(escapeXML(text));          
      }
      iter = children.iterator();
      while (iter.hasNext()) {
         XMLNode child = (XMLNode)iter.next();
         if (printNewLines) {
            sb.append(' ');
         }
         sb.append(child.toString());
      }
      if (!singleton) {
         sb.append('<');
         sb.append('/');
         sb.append(name);
         sb.append('>');
         if (printNewLines) {
            sb.append('\n');
         }
      }      
      return sb.toString();
   }
   
   
   /**
    * Escape special XML characters from string 
    * before inserting it into an XML file
    * @param str the unescaped string
    * @return a safe string for XML
    */
   public static String escapeXML(String string) {
      if (string == null) { return null; }
      StringBuffer sb = new StringBuffer(string.length());
      int len = string.length();
      char c;

      for (int i = 0; i < len; i++) {
         // we let spaces be
         c = string.charAt(i);
         if (c == '"') sb.append("&quot;");
         else if (c == '&') sb.append("&amp;");
         else if (c == '<') sb.append("&lt;");
         else if (c == '>') sb.append("&gt;");
         else if (c == '“') sb.append("&quot;");
         else if (c == '”') sb.append("&quot;");
         else if (c == '‘') sb.append("'");
         else if (c == '’') sb.append("'"); 
         //else if (c == '\\') sb.append("&#92;");
         else {
            int ci = 0xffff & c;
            if (ci < 160) {
               // nothing special only 7 Bit
               sb.append(c);
            } else {
               // Not 7 Bit use the unicode system
               sb.append("&#");
               sb.append(new Integer(ci).toString());
               sb.append(';');
            }
         }
      }
      return sb.toString();
  } 
   
   public static String getString(Attributes e, String name) {
      return e.getValue(name);
   }

   public static String getString(Attributes e, String name, String defVal) {
      String val = e.getValue(name);
      if (val == null) return defVal;
      return val;
   }

   public static String getString(Element e, String name, String defVal) {
      if (!e.hasAttribute(name)) return defVal;
      return e.getAttribute(name);
   }
   
   public static String getString(Element e, String name) {
      return e.getAttribute(name);
   }
   
   public static String getString(Object e, String name) {
      if (e instanceof Attributes) {
         return getString((Attributes)e, name);
      }
      return getString((Element)e, name);
   }  
   

   public static String getString(Object e, String name, String defVal) {
      if (e instanceof Attributes) {
         return getString((Attributes)e, name, defVal);
      }
      return getString((Element)e, name, defVal);
   }  
   
   public static boolean getBoolean(Element e, String name) {
      return Boolean.valueOf(e.getAttribute(name)).booleanValue();
   }
   
   public static boolean getBoolean(Attributes e, String name) {
      return Boolean.valueOf(e.getValue(name)).booleanValue();
   }   
   
   public static boolean getBoolean(Object e, String name) {
      if (e instanceof Attributes) {
         return getBoolean((Attributes)e, name);
      }
      return getBoolean((Element)e, name);
   }  
   
   public static int getInt(Element e, String name) {
      return Integer.parseInt(e.getAttribute(name));
   }
   
   public static int getInt(Element e, String name, int defval) {
      if (e.hasAttribute(name)) {
         return getInt(e, name);
      } else {
         return defval;
      }         
   }
   
   public static int getInt(Attributes e, String name, int defval) {
      if (e.getValue(name) != null) {
         return getInt(e, name);
      } else {
         return defval;
      }         
   }
   
   public static double getDouble(Attributes e, String name, double defval) {
      if (e.getValue(name) != null) {
         return getDouble(e, name);
      } else {
         return defval;
      }         
   }
   
   
   public static double getDouble(Element e, String name, double defval) {
      if (e.hasAttribute(name)) {
         return getDouble(e, name);
      } else {
         return defval;
      }         
   }
   
   public static int getInt(Object e, String name, int defval) {
      if (e instanceof Attributes) {
         return getInt((Attributes)e, name, defval);
      }
      return getInt((Element)e, name, defval);
   }
   
   public static int getInt(Object e, String name) {
      if (e instanceof Attributes) {
         return getInt((Attributes)e, name);
      }
      return getInt((Element)e, name);
   }
   
   public static double getDouble(Object e, String name, double defval) {
      if (e instanceof Attributes) {
         return getDouble((Attributes)e, name, defval);
      }
      return getDouble((Element)e, name, defval);
   }
   
   public static long getLong(Object e, String name, long defval) {
      if (e instanceof Attributes) {
         return getLong((Attributes)e, name, defval);
      }
      return getLong((Element)e, name, defval);
   }
   
   public static double getDouble(Object e, String name) {
      if (e instanceof Attributes) {
         return getDouble((Attributes)e, name);
      }
      return getDouble((Element)e, name);
   }
   
   public static long getLong(Object e, String name) {
      if (e instanceof Attributes) {
         return getLong((Attributes)e, name);
      }
      return getLong((Element)e, name);
   }
   
   public static long getLong(Element e, String name) {
      return Long.parseLong(e.getAttribute(name));
   }
   
   public static long getLong(Element e, String name, long defval) {
      return e.hasAttribute(name) ? getLong(e, name) : defval;
   }
   
   public static double getDouble(Element e, String name) {
      return Double.parseDouble(e.getAttribute(name));
   }
   
   public static int getInt(Attributes e, String name) {
      return Integer.parseInt(e.getValue(name));
   }
   
   public static long getLong(Attributes e, String name) {
      return Long.parseLong(e.getValue(name));
   }
   
   public static double getDouble(Attributes e, String name) {
      return Double.parseDouble(e.getValue(name));
   }
   
   /**
    * Get the unique text nested in the given XML Node
    * @param e an XML DOM Node
    * @return all nested text below this node
    */
   public static String getTextContent(Node e) {
      StringBuffer sb = new StringBuffer();
      NodeList nl = e.getChildNodes();
      for (int n = 0; n<nl.getLength(); n++) {       
         if (nl.item(n).getNodeValue() != null) {
            sb.append(nl.item(n).getNodeValue());
         }
         sb.append(getTextContent(nl.item(n)));
      }
      return sb.toString();
   }
   
   /**
    * Set if this will format output with newlines or not
    */
   public void setPrintNewLines(boolean b) {
      printNewLines  = b;
      Iterator iter = children.iterator();
      while (iter.hasNext()) {
         ((XMLNode)iter.next()).setPrintNewLines(b);
      }
   }
   
}

