/*
 * Created on 21-Jan-2006
 */
package ca.spaz.cron.targets;

import org.w3c.dom.Element;

import ca.spaz.cron.user.User;
import ca.spaz.util.XMLNode;

public class DRI {
   public static final int ALL = 0;
   public static final int MALE = 1;
   public static final int FEMALE = 2;
   
   private double min_age;
   private double max_age = 10000;
   private int gender = ALL;        // all, male, female
   private String status;           // normal, pregnant, lactating
   private double RDA = -1;         // recommended daily allowance
   private double TUL = -1;         // tollerable upper limit
   
   
   public DRI(Element e) {

      min_age = XMLNode.getDouble(e, "min_age");
      
      if (e.hasAttribute("max_age")) {
         max_age = XMLNode.getDouble(e, "max_age");
      }
      
      RDA = XMLNode.getDouble(e, "amount");
      
      if (e.hasAttribute("tul")) {
         TUL = XMLNode.getDouble(e, "tul");
      }
      
      if (e.hasAttribute("status")) {
         status = XMLNode.getString(e, "status");
      }
      
      if (e.hasAttribute("gender")) {
         String str = XMLNode.getString(e, "gender");
         if (str.equals("male")) gender = MALE;
         if (str.equals("female")) gender = FEMALE;
      }
   }


   public int getGender() {
      return gender;
   }


   public void setGender(int gender) {
      this.gender = gender;
   }


   public double getMaxAge() {
      return max_age;
   }


   public void setMaxAge(double max_age) {
      this.max_age = max_age;
   }


   public double getMinAge() {
      return min_age;
   }


   public void setMinAge(double min_age) {
      this.min_age = min_age;
   }
 

   public double getRDA() {
      return RDA;
   }


   public void setRDA(double rda) {
      RDA = rda;
   }


   public String getStatus() {
      return status;
   }


   public void setStatus(String status) {
      this.status = status;
   }


   public double getTUL() {
      return TUL;
   }

   public void setTUL(double tul) {
      TUL = tul;
   }


   public boolean matches(User user) {
      
      // gender check
      if (getGender() != ALL) {
         if (user.isMale() && getGender() == FEMALE) return false;
         if (user.isFemale() && getGender() == MALE) return false;
      }
      
      // age check
      int age = user.getAge();
      if (age < getMinAge()) return false;
      if (age > getMaxAge()) return false;

      // status check
      
      if (user.isPregnant()) {
         if (getStatus() == null) return false;
         if (!getStatus().equals("pregnant")) return false;
      }
      if (user.isLactating()) {
         if (getStatus() == null) return false;
         if (!getStatus().equals("lactating")) return false;
      }
      
      // good match
      return true;
   }
}
