/*
 * Adapted from: http://java.sun.com/docs/books/tutorial/uiswing/
 * components/example-swing/WholeNumberField.java Created on Oct 17, 2003
 */
package ca.spaz.gui;

import java.awt.Toolkit;
import java.text.DecimalFormat;

import javax.swing.JTextField;
import javax.swing.text.*;

public class DoubleField extends JTextField {
   private Toolkit toolkit;
   private double min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;
   private DecimalFormat df = new DecimalFormat("#########0.0###");

   public DoubleField(double value, int columns) {
      super(columns);
      toolkit = Toolkit.getDefaultToolkit();
      setValue(value);
   }
   
   public void setRange(double min, double max) {
      this.min = min;
      this.max = max;
   }

   public double getValue() {
      double retVal = 0;
      try {
         retVal = Double.parseDouble(getText());
      } catch (NumberFormatException e) {
         //toolkit.beep();
      }
      if (retVal < min) retVal = min;
      if (retVal > max) retVal = max;
      return retVal;
   }

   public void setValue(double value) {
      if (value < min) value = min;
      if (value > max) value = max;
      setText(df.format(value));
      selectAll();
      //setText(Double.toString(value));
   }

   protected Document createDefaultModel() {
      return new DoubleDocument();
   }
   
   protected String getCurrentText() {
      return getText();
   }

   protected class DoubleDocument extends PlainDocument {
      public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
         char[] source = str.toCharArray();
         char[] result = new char[source.length];
         int j = 0;
         for (int i = 0; i < result.length; i++) {
            char c = source[i];
            if (Character.isDigit(c) || (c=='.' && getCurrentText().indexOf('.')==-1)) {
               result[j++] = c;
            } else {
               toolkit.beep();
            }
         }
         super.insertString(offs, new String(result, 0, j), a);
      }
   }
}