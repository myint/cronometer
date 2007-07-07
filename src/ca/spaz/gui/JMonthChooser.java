package ca.spaz.gui;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class JMonthChooser extends JComboBox {

   public static final String[] MONTHS = {
      "January",
      "February",
      "March",
      "April",
      "May",
      "June",
      "July",
      "August",
      "September",
      "October",
      "November",
      "December"
   };
   
   public JMonthChooser() {
      setModel(new DefaultComboBoxModel(MONTHS));
   }

   public void setMonth(int i) {
      setSelectedIndex(i);
   }
   
   public int getMonth() {
      return getSelectedIndex();
   }
}
