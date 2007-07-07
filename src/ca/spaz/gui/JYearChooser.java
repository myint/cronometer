package ca.spaz.gui;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class JYearChooser extends JSpinner {
   
   private SpinnerNumberModel numberModel;
   
   public JYearChooser() {      
      numberModel = new SpinnerNumberModel(1950, 1800, 2100, 1);
      setEditor(new JSpinner.NumberEditor(this, "#"));     
      setModel(numberModel);
   }   
   
   public int getYear() { 
      return ((Number)getValue()).intValue();
   }

   public void setYear(int i) {
      setValue(new Integer(i));
   }

   public void setStartYear(int i) {
      numberModel.setMinimum(new Integer(i));
   }

   public void setEndYear(int i) {
      numberModel.setMaximum(new Integer(i));
   }

}
