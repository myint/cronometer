/*
 * Created on 3-Dec-2005
 */
package ca.spaz.cron.datasource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.foods.*;
import ca.spaz.gui.ErrorReporter;
import ca.spaz.util.Logger;
import ca.spaz.util.XMLNode;

public class XMLFoodLoader {
 
   /**
    * Create from XML InputStream
    * @param in
    * @throws ParserConfigurationException 
    * @throws IOException 
    * @throws SAXException 
    */
   public static Food loadFood(InputStream in) throws ParserConfigurationException, SAXException, IOException {
       DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
       dbf.setNamespaceAware(true);
       DocumentBuilder db = dbf.newDocumentBuilder();
       Document d = db.parse(in);
       Element e = d.getDocumentElement();
       return loadFood(e);
   }
   
   /**
    * Import a food or recipe from XML format
    * @param e an XML element to import from
    * @return the imported food, or null if an error ocurred
    */
   public static Food loadFood(Element e) {
       Food f = null;
       if (e.getNodeName().equals("food")) {
          f = new Food();
       } else if (e.getNodeName().equals("recipe")) {
          f = new Recipe();
       }
       if (f == null) return null;
       
       f.setDescription(e.getAttribute("name"));
       f.setSourceUID(e.getAttribute("uid"));
       if (e.hasAttribute("pcf")) {
          f.setProteinConversionFactor(Double.parseDouble(e.getAttribute("pcf")));
       }
       if (e.hasAttribute("lcf")) {
          f.setLipidConversionFactor(Double.parseDouble(e.getAttribute("lcf")));
       }
       if (e.hasAttribute("ccf")) { 
          f.setCarbConversionFactor(Double.parseDouble(e.getAttribute("ccf")));
       }
       List measures = new ArrayList();
       measures.add(Measure.GRAM);
       NodeList nl = e.getElementsByTagName("measure");
       for (int i=0; i<nl.getLength(); i++) {
          Element m = (Element)nl.item(i);
          Measure measure = new Measure();
          measure.setDescription(m.getAttribute("name"));
          measure.setAmount(Double.parseDouble(m.getAttribute("amount")));
          measure.setGrams(Double.parseDouble(m.getAttribute("grams")));
          measures.add(measure);
       }
       f.setMeasures(measures);
       
       nl = e.getElementsByTagName("nutrient");
       for (int i=0; i<nl.getLength(); i++) {
          Element n = (Element)nl.item(i);
          NutrientInfo ni = NutrientInfo.getByName(n.getAttribute("name"));
          if (ni != null) {
             f.setNutrientAmount(ni, 
                   Double.parseDouble(n.getAttribute("amount")));          
          }
       }
       
       nl = e.getElementsByTagName("comments");
       for (int i=0; i<nl.getLength(); i++) {
          Element m = (Element)nl.item(i);
          String str = XMLNode.getTextContent(m);
          if (str != null) {
             f.appendComment(str.trim());
          }
       }
       
       if (f instanceof Recipe) {
          loadRecipe((Recipe)f, e);
       }
       
       return f;
   }
   
   
   /**
    * load special tags for this recipe
    */
   protected static void loadRecipe(Recipe r, Element e) { 
      List list = new ArrayList();
      NodeList nl = e.getElementsByTagName("serving");
      for (int i=0; i<nl.getLength(); i++) {
         Serving s = new Serving((Element)nl.item(i));
         if (s.isLoaded()) {
            list.add(s);
         }
      }      
      r.addServings(list);
   }
   
   
   public static Food loadFood(File file) {
      Food f = null;
      try {
         BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
         f = loadFood(in);
         in.close();
      } catch (Exception e) {
         Logger.error("Error loading: " + file, e); 
         ErrorReporter.showError("Error loading: " + file, e, CRONOMETER.getInstance()); 
         f = null;
      }
      return f;
   }  

}
