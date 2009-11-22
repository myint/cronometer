/*
 * Created on 26-Nov-2005
 */
package ca.spaz.cron.exercise;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import ca.spaz.util.ToolBox;

public class ExerciseDialog extends JDialog implements ExerciseEditorListener {

   private ExerciseEditor exerciseEditor;
   private JPanel mainPanel; 
   private boolean abort = true;
   private Exercise exercise = null;
   
   public ExerciseDialog(Frame parent) {
     super(parent);
     init(parent);
   }
   
   public ExerciseDialog(Dialog parent) {
      super(parent);
      init(parent);
    }
   
   private void init(Window parent) {
      this.setTitle("Add exercise");
      this.getContentPane().add(getMainPanel());
      this.pack(); 
      ToolBox.centerOver(this, parent);
      this.setModal(true);
      
      // add escape listener to dismiss window
      getRootPane().registerKeyboardAction( new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            dispose();
         }
      }, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), 
      JComponent.WHEN_IN_FOCUSED_WINDOW );  
       
      //Datasources.getUserFoods().addUserFoodsListener(this);
   }

   public void display(boolean addable) {
      getExerciseEditor().getAddButton().setVisible(addable);
      this.setVisible(true);
   }
   
   public Exercise getSelectedExercise() {
      if (abort) return null;
      return exercise;
   }
   
   public JPanel getMainPanel() {
      if (null == mainPanel) {
         mainPanel = new JPanel(new BorderLayout(4,4));
         mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4)); 
         mainPanel.add(getExerciseEditor(), BorderLayout.CENTER);
      }
      return mainPanel;
   }
   
   public ExerciseEditor getExerciseEditor() {
      if (exerciseEditor == null) {
         exerciseEditor = new ExerciseEditor();
         exerciseEditor.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
         exerciseEditor.addExerciseEditorListener(this);
      }
      return exerciseEditor;
   }

   public void exerciseChosen(Exercise s) {
      exercise = s;
      abort = false;
      dispose();
   }
   
   public void dispose() {
      super.dispose();
   }
}
