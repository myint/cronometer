/*
 * Created on 27-Jun-06
 */
package ca.spaz.util;

public class Logger {

   public static void log(String str) {
      System.out.println(str);
   } 
   
   public static void debug(String str) {
      System.out.println(str);
   } 
   

   public static void error(Exception e) {
      e.printStackTrace();
   }

   public static void debug(Exception e) {
      e.printStackTrace();
   }

   public static void error(String str) {
      System.err.println(str);
   }     

   public static void error(String str, Exception e) {
      System.err.println(str);
      e.printStackTrace();
   }

   public static boolean isDebugEnabled() {
      return true;
   } 
}
