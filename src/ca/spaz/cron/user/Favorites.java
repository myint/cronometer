package ca.spaz.cron.user;


public class Favorites {
   private static final int MAX_SLOTS = 1000;
   
   public class Favorite implements Comparable {
      Object val;
      int rank;
      
      public int compareTo(Object arg0) {
         return 0;
      }
   }

   private Favorite[] favorites = new Favorite[MAX_SLOTS];
   
   
}
