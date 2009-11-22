package ca.spaz.cron.exercise;

import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.List;


public class ExerciseSelection implements Transferable {

   public static final DataFlavor exerciseFlavor = new DataFlavor(Exercise[].class, "Exercise");
   public static final DataFlavor[] flavors = {exerciseFlavor};
   
   private Exercise[] exercises;
   
   public ExerciseSelection(Exercise[] list) {
      exercises = list;
   }
   
   public ExerciseSelection(ExerciseTable table) {
      List sel = table.getSelectedExercises();
      exercises = new Exercise[sel.size()];
      sel.toArray(exercises);
   }

   public DataFlavor[] getTransferDataFlavors() {        
      return flavors;
   }

   public boolean isDataFlavorSupported(DataFlavor flavor) { 
      return flavor.equals(exerciseFlavor);
   }
    
   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
      if (flavor.equals(exerciseFlavor))
         return (exercises);
      else
         throw new UnsupportedFlavorException(flavor);
   }

}
