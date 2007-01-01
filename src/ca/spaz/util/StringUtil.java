/*
 * Created on 28-Jan-2006
 */
package ca.spaz.util;

public class StringUtil {
   public static final int PAD_LEFT = 0;
   public static final int PAD_RIGHT = 1;
   public static final int PAD_CENTER = 2;
   
   public static String padr(String str, int num) {
      return pad(str, num, PAD_RIGHT, ' ');
   }

   public static String padl(String str, int num) {
      return pad(str, num, PAD_LEFT, ' ');
   }

   public static String padc(String str, int num) {
      return pad(str, num, PAD_CENTER, ' ');
   }
   
   public static String pad(String str, int num, int side, char pad) {
      int len = str.length();
      if (len >= num) return str;
      StringBuffer sb = new StringBuffer();
      switch (side) {
         case PAD_LEFT:
            while (len++ < num) { sb.append(pad); }
            sb.append(str);
            break;
         case PAD_RIGHT:
            sb.append(str);
            while (len++ < num) { sb.append(pad); }
            break;
         case PAD_CENTER:
            int n = (num - len)/2;
            while (n-- >= 0) { sb.append(pad); }
            sb.append(str);
            while (len++ < num) { sb.append(pad); }
            break;            
      }
      return sb.toString();
   }

   public static String charRun(char c, int i) {
      StringBuffer sb = new StringBuffer();
      while (i-- >= 0) { sb.append(c); }
      return sb.toString();
   }

}
