/*
 * Adapted from: http://java.sun.com/docs/books/tutorial/uiswing/
 * components/example-swing/WholeNumberField.java Created on Oct 17, 2003
 */
package ca.spaz.gui;

import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.text.*;

/**
 * A text field for entering integer numeric data. The only characters it accepts are the 
 * digits 0-9. 
 * @author Gerald
 *
 */
public class IntegerField extends JTextField {
   private Toolkit toolkit;
   private int min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;

   public IntegerField(int value, int columns) {
      super(columns);
      setHorizontalAlignment(JTextField.RIGHT);
      toolkit = Toolkit.getDefaultToolkit();
      setValue(value);
   }
   
   public void setRange(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public int getValue() {
      int retVal = 0;
      try {
         retVal = Integer.parseInt(getText());
      } catch (NumberFormatException e) {
//       toolkit.beep();
      }
      if (retVal < min) retVal = min;
      if (retVal > max) retVal = max;
      return retVal;
   }

   public void setValue(int value) {
      if (value < min) value = min;
      if (value > max) value = max;
      if (value == 0) {
         setText("");
      }
      else {
         setText(Integer.toString(value));
      }
      selectAll();
   }
   
   public void setValue(String value) {
      setText(value);
   }

   protected Document createDefaultModel() {
      return new IntegerDocument();
   }
   
   protected String getCurrentText() {
      return getText();
   }

   protected class IntegerDocument extends PlainDocument {
      // Overidden to allow only digits and a hyphen (minus sign, only if min < 0)
      public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
         char[] source = str.toCharArray();
         char[] result = new char[source.length];
         int j = 0;
         for (int i = 0; i < result.length; i++) {
            char c = source[i];
            if (Character.isDigit(c) || (c == '-' && min < 0 && getCurrentText().indexOf('-') == -1)) {
               result[j++] = c;
            } else {
               toolkit.beep();
            }
         }
         super.insertString(offs, new String(result, 0, j), a);
      }
   }
}