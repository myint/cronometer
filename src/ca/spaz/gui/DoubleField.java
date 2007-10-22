/*
 * Adapted from: http://java.sun.com/docs/books/tutorial/uiswing/
 * components/example-swing/WholeNumberField.java Created on Oct 17, 2003
 */
package ca.spaz.gui;

import java.awt.Toolkit;
import java.text.DecimalFormat;

import javax.swing.JTextField;
import javax.swing.text.*;

import ca.spaz.util.ToolBox;

/**
 * A text field for entering floating point data. The only characters it accepts
 * are the digits 0-9 and one (optional) decimal point.
 * 
 * @author Aaron
 * 
 */
public class DoubleField extends JTextField {
   private Toolkit toolkit;

   private double min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;

   private DecimalFormat df = new DecimalFormat("#########0.0###");

   public DoubleField(double value, int columns) {
      super(columns);
      setHorizontalAlignment(JTextField.RIGHT);
      toolkit = Toolkit.getDefaultToolkit();
      setValue(value);
   }

   public void setRange(double min, double max) {
      this.min = min;
      this.max = max;
   }

   public double getValue() {
      double retVal = 0.0;
      String[] q;
      double[] r = {0.0, 0.0};    
      q = getText().split("/", 2);    
      try {
         for (int i = 0; i < q.length; i++) {
            r[i] = Double.parseDouble(q[i]);
         }
      } catch (NumberFormatException e) {
         // toolkit.beep();        
      }   
      retVal = ToolBox.safeDivide(r[0], r[1], r[0]);  
      if (retVal < min) retVal = min;
      if (retVal > max) retVal = max;
      return retVal;
   }

   public void setValue(double value) {
      if (value < min)
         value = min;
      if (value > max)
         value = max;
      if (value == 0.0) {
         setText("");
      } else {
         setText(df.format(value));
      }
      selectAll();
   }

   public void setValue(String value) {
      setText(value);
   }

   protected Document createDefaultModel() {
      return new DoubleDocument();
   }

   protected String getCurrentText() {
      return getText();
   }

   protected class DoubleDocument extends PlainDocument {
      // Overridden to allow only digits and one decimal point e.g. 1.00
      // Note that not all data that would allowed by Double.parseDouble() is
      // supported, e.g.
      // exponential notation, negative signs, hex notation etc.
      public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
         char[] source = str.toCharArray();
         char[] result = new char[source.length];
         int j = 0;
         for (int i = 0; i < result.length; i++) {
            char c = source[i];
            if (Character.isDigit(c)
                  || (c == '.' && getCurrentText().indexOf('.') == -1)
                  || (c == '/' && getCurrentText().indexOf('/') == -1)) {
               result[j++] = c;
            } else {
               toolkit.beep();
            }
         }
         super.insertString(offs, new String(result, 0, j), a);
      }
   }
}