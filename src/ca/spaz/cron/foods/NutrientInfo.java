/*
 * Created on Apr 6, 2005 by davidson
 */
package ca.spaz.cron.foods;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.targets.DRI;
import ca.spaz.cron.user.User;
import ca.spaz.gui.ErrorReporter;

public class NutrientInfo {

   public static final String LIPIDS = "Lipids";
   public static final String MACRO_NUTRIENTS = "General";
   public static final String MINERALS = "Minerals";
   public static final String AMINO_ACIDS = "Amino Acids";
   public static final String VITAMINS = "Vitamins";

   public static final String[] CATEGORIES = {
      MACRO_NUTRIENTS, VITAMINS, MINERALS, AMINO_ACIDS, LIPIDS
   };
   
   private static byte index_count = 0;
    
   private byte index = index_count++;
   
   private String name;
   private String units;
   private String category;
   private double RDI = 0;//For %DV calculations
   private List DRIs;
   private NutrientInfo parent;
   private String usda;
   private boolean sparse = false; // data is considered sparse (under-represented)
   private boolean track = true; // default tracking value

   private static List globalList = new ArrayList();
   private static HashMap nutrients = new HashMap();   
   private static HashMap categories = new HashMap();
    

   static {
      try {
         load(NutrientInfo.class.getResourceAsStream("/nutrients.xml"));
      } catch (Exception e) {            
         ErrorReporter.showError("Error Loading nutrients.xml", e, CRONOMETER.getInstance());
      }
   }   

   private static void load(InputStream in) throws ParserConfigurationException, SAXException, IOException {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document d = db.parse(in);
      Element root = d.getDocumentElement();
      NodeList nl = root.getElementsByTagName("nutrient");
      for (int i=0; i<nl.getLength(); i++) {
         NutrientInfo ni = new NutrientInfo((Element)nl.item(i));
         if (ni != null) {
            NutrientInfo.nutrients.put(ni.getName(), ni);
            NutrientInfo.globalList.add(ni);
            NutrientInfo.getCategory(ni.getCategory()).add(ni);
         }
      }
   }
   
   public static List getCategory(String name) {
      List list = (List)categories.get(name);
      if (list == null) {
         list = new ArrayList();
         categories.put(name, list);
      }
      return list;
   }
   
   /**
    * Check if the data available for this nutrient is generally incomplete.
    */
   public boolean isSparseData() {
      return sparse;
   }

   public NutrientInfo(Element e) {
      this.category = e.getAttribute("category");
      this.name = e.getAttribute("name");
      this.units = e.getAttribute("unit");
      if (e.hasAttribute("dv")) {
         this.RDI = Double.parseDouble(e.getAttribute("dv"));
      }
      if (e.hasAttribute("parent")) {
         this.parent = NutrientInfo.getByName(e.getAttribute("parent"));
      }
      if (e.hasAttribute("sparse")) {
         this.sparse = e.getAttribute("sparse").equalsIgnoreCase("true");
      }
      if (e.hasAttribute("track")) {
         this.track = e.getAttribute("track").equalsIgnoreCase("true");
      }
      
      // map all USDA nutrient IDs to NutrientInfo objects
      if (e.hasAttribute("usda")) {
         this.usda = e.getAttribute("usda");
         String[] parts = this.usda.split(",");
         for (int i=0; i<parts.length; i++) {
            getUSDANutrientMap().put(parts[i], this);
         }
      }
      
      // load any RDA values
      NodeList nl = e.getElementsByTagName("rda");
      for (int j=0; j<nl.getLength(); j++) {
         DRI dri = new DRI((Element)nl.item(j));
         getDRIs().add(dri);
      }      
   }


   public boolean isVitamin() {
       return getCategory().equals(VITAMINS);
    }
    
    public boolean isMineral() {
       return getCategory().equals(MINERALS);
    }
    
    public boolean isMacroNutrient() {
       return getCategory().equals(MACRO_NUTRIENTS);
    }
    
    public boolean isLipid() {
       return getCategory().equals(LIPIDS);
    }

    public boolean isAminoAcid() {
       return getCategory().equals(AMINO_ACIDS);
    }

    public boolean isOmega3() {       
       return getName().equals("Omega-3");
    }

    public boolean isOmega6() {       
       return getName().equals("Omega-6");
    }
      
    public String getName() {
        return name;
    }

    public String getUnits() {
        return units;
    }
 

    public String getCategory() {
        return category;
    }

    
    public static List getMacroNutrients() {
       return (List)categories.get(MACRO_NUTRIENTS);        
    }

    public static List getMinerals() {
       return (List)categories.get(MINERALS);
    }

    public static List getAminoAcids() {
       return (List)categories.get(AMINO_ACIDS);       
    }
    
    public static List getVitamins() {
       return (List)categories.get(VITAMINS);      
    }
    
    public static List getLipids() {
       return (List)categories.get(LIPIDS);
   }
    
    public static NutrientInfo getByName(String name) {
       return (NutrientInfo)nutrients.get(name);
    } 
    
    public static List getGlobalList() {
       return globalList;
    }

   public double getTarget() {
      return getReferenceDailyIntake();
   }
   
   public double getReferenceDailyIntake() {
      return RDI;
   }
   
   public String toString() {
      return name;
   }

   public NutrientInfo getParent() {
      return parent;
   }

   public void setParent(NutrientInfo parent) {
      this.parent = parent;
   }   
   
   public List getDRIs() {
      if (DRIs == null) {
         DRIs = new ArrayList();
      }
      return DRIs;
   }
   
   public int getIndex() {
      return index;
   }
   
   
   public double getTargetMinimum(User user) {      
      DRI dri = findMatch(user, getDRIs());
      if (dri != null) {
         return dri.getRDA();
      }
      return getReferenceDailyIntake();
   }

   public double getTargetMaximum(User user) {
      DRI dri = findMatch(user, getDRIs());
      if (dri != null) {
         double TUL = dri.getTUL();
         return TUL > 0 ? TUL : dri.getRDA() * 5;
      }      
      return getReferenceDailyIntake() * 3;
   }

   public DRI findMatch(User user, List DRIs) {
      Iterator iter = DRIs.iterator();
      while (iter.hasNext()) {
         DRI dri = (DRI)iter.next();
         if (dri.matches(user)) {
            return dri;
         }
      }
      return null;
   }

   public static NutrientInfo getByUSDA(String usdaID) {
      return (NutrientInfo)getUSDANutrientMap().get(usdaID);
   }
    
   public String getUSDA() {
      return usda;
   }

   private static HashMap nutrientMapUSDA;
   public static HashMap getUSDANutrientMap() {
      if (nutrientMapUSDA == null) {
         nutrientMapUSDA = new HashMap();
      }
      return nutrientMapUSDA;
   }

   public static NutrientInfo getCalories() {
      return getByUSDA("208");
   }

   public static NutrientInfo getProtein() {
      return NutrientInfo.getByName("Protein");
   }

   public static NutrientInfo getCarbs() {
      return NutrientInfo.getByName("Carbs");
   }

   public static NutrientInfo getFat() {
      return NutrientInfo.getByName("Fat");
   }
   
   public static NutrientInfo getFiber() {
      return NutrientInfo.getByName("Fiber");
   }

   public boolean getDefaultTracking() {
      return usda != null && track;
   }

}
 
