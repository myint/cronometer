package ca.spaz.cron.foods;

import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.List;


public class ServingSelection implements Transferable {

   public static final DataFlavor servingFlavor = new DataFlavor(Serving[].class, "Serving");
   public static final DataFlavor[] flavors = {servingFlavor};
   
   private Serving[] servings;
   
   public ServingSelection(Serving[] list) {
      servings = list;
   }
   
   public ServingSelection(ServingTable table) {
      List sel = table.getSelectedServings();
      servings = new Serving[sel.size()];
      sel.toArray(servings);
   }

   public DataFlavor[] getTransferDataFlavors() {        
      return flavors;
   }

   public boolean isDataFlavorSupported(DataFlavor flavor) { 
      return flavor.equals(servingFlavor);
   }
    
   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
      if (flavor.equals(servingFlavor))
         return (servings);
      else
         throw new UnsupportedFlavorException(flavor);
   }

}
