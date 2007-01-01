/*
 * Created on 7-May-2005
 */
package ca.spaz.cron.datasource.USDAImport;

import java.sql.*;
import java.util.Map;

import ca.spaz.sql.SQLInsert;
import ca.spaz.util.Logger;

public class USDAFood {
      
   int ID;
   String ndb_id;
   String description;
   String foodgroup;
   double pCF=4, fCF=9, cCF=4;
   
   
   /**
    * Construct from USDB Flat file string
    * @param str a string from the USDB sr17 flat file
    * @param groups map of ids to food groups
    */
   public USDAFood(String str, Map groups) {
      //~01079~^~0100~^~Milk, reduced fat, fluid, 2% milkfat, with added vitamin A~^~MILK,RED FAT,FLUID,2% MILKFAT,W/ ADDED VIT A~^~~^~~^~Y~^~~^0^~~^^^^
      str = str.replaceAll("\\^\\^\\^\\^", "^~~^~~^~~^~~");
      str = str.replaceAll("\\^\\^", "^~~^");
      str = str.replaceAll("\\^$", "^~~");
      
      String[] parts = str.split("\\^");
      for (int i = 0; i < parts.length; i++) {
         parts[i] = parts[i].replaceAll("^~", "");
         parts[i] = parts[i].replaceAll("~$", "");
      }

      ndb_id = parts[0];
      foodgroup = (String) groups.get(parts[1]);
      description = parts[2];
      if (parts.length == 14) {
         try { pCF = Double.parseDouble(parts[11]); } catch (NumberFormatException e) {}
         try { fCF = Double.parseDouble(parts[12]); } catch (NumberFormatException e) {}
         try { cCF = Double.parseDouble(parts[13]); } catch (NumberFormatException e) {}
      } else {
         System.out.println("bad parts size?\n\t"+parts.length+"|"+str);
      }
   }   
   
   public void addToDB(Connection c) {
      try {
         SQLInsert s = new SQLInsert("Food");
         s.getColumns().add("ID", null);
         s.getColumns().add("ndb_id", ndb_id);
         s.getColumns().add("description", description);
         s.getColumns().add("foodgroup", foodgroup);
         s.getColumns().add("source", "USDA sr17");
         s.getColumns().add("sourceUID", "sql.usda-sr17." + ndb_id);
         s.execute(c);
         
         // get auto-incremented ID
         ResultSet rs = c.createStatement().executeQuery("CALL IDENTITY()");
         rs.next();
         ID = rs.getInt(1);        
      } catch (SQLException e) {
         Logger.error("parseFood(String)", e);
      }
   }
}
