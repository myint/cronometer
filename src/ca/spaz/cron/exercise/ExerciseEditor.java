package ca.spaz.cron.exercise;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.*;

import ca.spaz.gui.DoubleField;
 
public class ExerciseEditor extends JPanel {  
    
   private Exercise cur = new Exercise();

   private JButton addButton;
   private ActionListener addAction;
   
   private JTextField nameField;
   private DoubleField minutesField;
   private DoubleField caloriesField;
   
   private JPanel exerciseTakenPanel;

   private JPanel mainPanel;
   private DecimalFormat labelFormatter;

   private Vector listeners = new Vector();
   
   public ExerciseEditor() {
      initialize();
      labelFormatter = new DecimalFormat("#####0.0");
   }

   private void initialize() {
      this.setOpaque(false);
      this.add(getMainPanel());
   }
   
   private JPanel getMainPanel() {
      if (mainPanel == null) {
         mainPanel = new JPanel(new BorderLayout(3, 3));
         mainPanel.setOpaque(false);
         mainPanel.add(getExerciseTakenPanel(), BorderLayout.CENTER);
      } 
      return mainPanel;
   }

   /**
    * @return
    */
   private JPanel getExerciseTakenPanel() {
      if (null == exerciseTakenPanel) {
         exerciseTakenPanel = new JPanel();
         exerciseTakenPanel.setOpaque(false);
         exerciseTakenPanel.setLayout(new BoxLayout(exerciseTakenPanel, BoxLayout.X_AXIS));
         exerciseTakenPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
         exerciseTakenPanel.add(Box.createHorizontalGlue());
         exerciseTakenPanel.add(createFieldLabel("Exercise:", false));
         exerciseTakenPanel.add(getNameInput());
         exerciseTakenPanel.add(Box.createHorizontalStrut(5));
         exerciseTakenPanel.add(createFieldLabel("Time (minutes):", false));
         exerciseTakenPanel.add(getMinutesInput());
         exerciseTakenPanel.add(Box.createHorizontalStrut(5));
         exerciseTakenPanel.add(createFieldLabel("Calories:", false));
         exerciseTakenPanel.add(getCaloriesInput());
         exerciseTakenPanel.add(Box.createHorizontalStrut(5));
         exerciseTakenPanel.add(getAddButton());
         exerciseTakenPanel.add(Box.createHorizontalGlue());
      }
      return exerciseTakenPanel;
   }


   /**
    * Return a label with the given text, right-aligned.
    * @param text the text to use on the label.
    * @param bold <code>true</code> if the label is to have boldface text,
    * <code>false</code> otherwise.
    * @return a <code>JLabel</code> object with the supplied text and font hints.
    */
   public static final JLabel createFieldLabel(String text, boolean bold) {
       JLabel label = new JLabel(text, JLabel.RIGHT);
       if (bold) {
           label.setFont(label.getFont().deriveFont(Font.BOLD));
       }
       return label;
   }

   public JButton getAddButton() {
      if (null == addButton) {
         addButton = new JButton("Add");
         addButton.setOpaque(false);
         addButton.addActionListener(getAddAction());
      }
      return addButton;
   }
  
   private ActionListener getAddAction() {
      if (addAction == null) {
         addAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               fireServingChosenEvent();
            }
         };
      }
      return addAction;
   }
   
   public JTextField getNameInput() {
      if(null == nameField) {
         nameField = new JTextField(20);
      }
      
      return nameField;
   }

   public DoubleField getMinutesInput() {
      if(null == minutesField) {
         minutesField = new DoubleField(0, 8);
      }
      
      return minutesField;
   }

   public DoubleField getCaloriesInput() {
      if(null == caloriesField) {
         caloriesField = new DoubleField(0, 8);
      }
      
      return caloriesField;
   }

   /**
    * Cap the string at a max length,
    * @return
    */
   private String fixString(String str) {
      //return "<html><div align=\"center\">" + str + "</div></html>";
      if (str.length() > 53) {
         return str.substring(0, 50)+"...";
      } else {
         return str;
      }
   }
   
   public void update() {
      
   }
   
   public synchronized void addExerciseEditorListener(ExerciseEditorListener sel) {
      listeners.add(sel);
   }

   public synchronized void removeExerciseEditorListener(ExerciseEditorListener sel) {
      listeners.remove(sel);
   }
   
   private synchronized void fireServingChosenEvent() {
      cur.setName(getNameInput().getText());
      cur.setMinutes(getMinutesInput().getValue());
      cur.setCalories(getCaloriesInput().getValue());
      for (int i=0; i<listeners.size(); i++) {
         ExerciseEditorListener sel = (ExerciseEditorListener)listeners.get(i);
         sel.exerciseChosen(cur);
      }
   }
}
