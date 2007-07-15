/*
 * Created on Apr 9, 2005 by davidson
 */
package ca.spaz.cron.foods;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.datadosen.component.RiverLayout;
import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.datasource.Datasources;
import ca.spaz.util.ToolBox;

/**
 * An editor panel for a food item in the database. The editor must be able to
 * add / update / delete a food which includes all nutrient entries, and weights
 * 
 * @author davidson
 */
public class FoodEditor extends JPanel {

   private CRONOMETER cwapp;

   private JDialog dialog;

   protected Food food, original;

   private JTextField name;

   private MeasureWidget measure;
   private MeasureEditor measureEditor;

   private NutrientEditorTable macro, minerals, vitamins, aminoacids, lipids;

   private JButton saveButton;
   private JButton cancelButton;
   private JPanel buttonPanel;
   
   private JPanel centerPanel;
   private JPanel nutrientPanel;
   private JPanel generalPanel;
   private JTabbedPane tabPanel;
   private JPanel commentsPanel;
   private JTextArea commentEditor;

   public FoodEditor(CRONOMETER app, Food f) {
      this.cwapp = app;
      setFood(f);
      initialize();
      getMeasureSelector().setFocus(); 
   }

   public void setFood(Food f) {
      this.original = f;
      this.food = new Food(f); // edit on a copy
   }
   
   /**
    * Copy changes to original and save.
    */
   public void updateOriginal() {
      original.copy(food);
      original.update();
   }
   
   public void display() {
      getDialog().setVisible(true);
   }
   
   protected String getTitle() {
      return "Food Editor";
   }

   protected JDialog getDialog() {
      if (null == dialog) {
         dialog = new JDialog(cwapp);
         dialog.setTitle(getTitle());
         dialog.getContentPane().add(this);
         dialog.pack();
         dialog.setModal(true);
         dialog.setLocationRelativeTo(cwapp);
      }
      return dialog;
   }

   protected void initialize() {
      this.setLayout(new BorderLayout(4, 4));
      this.setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9));
      this.add(getGeneralPanel(), BorderLayout.NORTH);
      this.add(getCenterPanel(), BorderLayout.WEST);
      this.add(getNutrientPanel(), BorderLayout.CENTER);
   }

   
   
   protected JPanel getCenterPanel() {
      if (centerPanel == null) {
         centerPanel = new JPanel();
         centerPanel.setLayout(new BorderLayout(4, 4));
         //centerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));         
         centerPanel.add(getMeasureEditor(), BorderLayout.CENTER);         
         centerPanel.add(getCommentsPanel(), BorderLayout.SOUTH);
      }
      return centerPanel;
   }
   
   
   protected Border makeTitleBorder(String str) {
      return new CompoundBorder(
            BorderFactory.createTitledBorder(
                  BorderFactory.createEtchedBorder(),
                  //BorderFactory.createBevelBorder(BevelBorder.RAISED),
                  str),
            BorderFactory.createEmptyBorder(8,8,8,8));      
   }
   
   protected JPanel getNutrientPanel() {
      if (nutrientPanel == null) {
         nutrientPanel = new JPanel();
         nutrientPanel.setLayout(new BorderLayout(7, 7));
         nutrientPanel.setBorder(makeTitleBorder("Nutrition Info"));
         nutrientPanel.add(getMeasureSelector(), BorderLayout.NORTH);
         nutrientPanel.add(getTabPanel(), BorderLayout.CENTER);         
      }
      return nutrientPanel;
   }
   
   public void doCancel() {
      getDialog().dispose();
   }
 
   
   /**
    * Commit any changes made to the food
    */
   public void doSave() {
      
      String name = getNameField().getText().trim();
      if (name.length() == 0) {
         JOptionPane.showMessageDialog(this, 
               "You must enter a name for this food.", 
               "Error", JOptionPane.ERROR_MESSAGE);
         return;
      }
      
      int rc = JOptionPane.YES_OPTION;             
      if (food.getSource() != null && food.getSource() != Datasources.getUserFoods()) {
         rc = JOptionPane.showConfirmDialog(this, 
               "The "+food.getSource().getName()+" Database cannot be modified.\n" +
               "Would you like to save a copy of this modified\n" +
               "food in your custom foods?");
         if (rc == JOptionPane.CANCEL_OPTION) {
            return;
         }
      }
      
      food.setDescription(name);     
      food.setMeasures(getMeasureEditor().getMeasures());
      food.setComment(getCommentEditor().getText());
      if (food.getSource() != Datasources.getUserFoods()) {
         if (rc == JOptionPane.YES_OPTION) {
            Datasources.getUserFoods().addFood(food);
            JOptionPane.showMessageDialog(this, 
                  "'"+food.getDescription()+"' has been added to your foods.", 
                  "Food Added", JOptionPane.INFORMATION_MESSAGE);
         }  
      } else {
         updateOriginal(); // commit changes         
      }
      getDialog().dispose();
      CRONOMETER.getInstance().refreshDisplays();
   }

   private JPanel getButtonPanel() {
      if (null == buttonPanel) {
         buttonPanel = new JPanel();
         buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
         buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
         buttonPanel.add(Box.createHorizontalGlue());
         buttonPanel.add(getCancelButton());
         buttonPanel.add(Box.createHorizontalStrut(10));
         buttonPanel.add(getSaveButton());
         buttonPanel.add(Box.createHorizontalStrut(10));
      }
      return buttonPanel;
   }

   /**
    * @return
    */
   private JButton getCancelButton() {
      if (null == cancelButton) {
         cancelButton = new JButton("Cancel");
         cancelButton.setVerifyInputWhenFocusTarget(false);
         cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doCancel();
            }          
         });
      }
      return cancelButton;
   }

   private JButton getSaveButton() {
      if (null == saveButton) {
         saveButton = new JButton("Save");
         saveButton.setToolTipText("Save the changes made to this food");
         saveButton.setRequestFocusEnabled(true);
         saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doSave();
            }
         });        
      }
      return saveButton;
   }

 
   protected JPanel getGeneralPanel() {
      if (null == generalPanel) {
         final String TAB_HFILL = RiverLayout.TAB_STOP + " "
               + RiverLayout.HFILL;
         generalPanel = new JPanel(new RiverLayout());
         generalPanel.add(RiverLayout.LINE_BREAK, new JLabel("Name:"));
         generalPanel.add(TAB_HFILL, getNameField());
         generalPanel.add(TAB_HFILL, getSourceLabel());         
         generalPanel.add(TAB_HFILL, getButtonPanel()); 
        // generalPanel.add(TAB_HFILL, getMeasureSelector());
      }
      return generalPanel;
   }

   private JLabel getSourceLabel() {
      if (food.getSource() != null) {
         return new JLabel(food.getSource().getName()+":"+food.getSourceUID());
      } else {
         return new JLabel("unsaved");
      }
   }
   
   protected JTextField getNameField() {
      if (null == name) {
         name = new JTextField(40);
         name.setText(food.getDescription());
      }
      return name;
   }

   protected NutrientEditorTable getMacroNutrientsTable() {
      if (macro == null) {
         macro = new NutrientEditorTable(NutrientInfo.getMacroNutrients());
         macro.setFood(food);
         macro.setMultiplier(getMeasureSelector().getMultiplier());
      }
      return macro;
   }

   protected NutrientEditorTable getMineralsTable() {
      if (minerals == null) {
         minerals = new NutrientEditorTable(NutrientInfo.getMinerals());
         minerals.setFood(food);
         minerals.setMultiplier(getMeasureSelector().getMultiplier());
      }
      return minerals;
   }
   
   protected NutrientEditorTable getVitaminsTable() {
      if (vitamins == null) {
         vitamins = new NutrientEditorTable(NutrientInfo.getVitamins());
         vitamins.setFood(food);
         vitamins.setMultiplier(getMeasureSelector().getMultiplier());
      }
      return vitamins;
   }
   
   protected NutrientEditorTable getAminoAcidsTable() {
      if (aminoacids == null) {
         aminoacids = new NutrientEditorTable(NutrientInfo.getAminoAcids());
         aminoacids.setFood(food);
         aminoacids.setMultiplier(getMeasureSelector().getMultiplier());
      }
      return aminoacids;
   }
   
   protected NutrientEditorTable getLipidsTable() {
      if (lipids == null) {
         lipids = new NutrientEditorTable(NutrientInfo.getLipids());
         lipids.setFood(food);
         lipids.setMultiplier(getMeasureSelector().getMultiplier());
      }
      return lipids;
   }

   /**
    * @return the food comment panel.
    */
   private JPanel getCommentsPanel() {
      if (commentsPanel != null)
         return commentsPanel;
      commentsPanel = new JPanel(new BorderLayout());
      commentsPanel.setBorder(makeTitleBorder("Comments"));
      JScrollPane jsp = new JScrollPane(getCommentEditor());
      jsp.setPreferredSize(new Dimension(150,80));
      commentsPanel.add(jsp, BorderLayout.CENTER);
      return commentsPanel;
   }
   
   protected JTextArea getCommentEditor() {
      if (commentEditor != null)
         return commentEditor;
      
      commentEditor = new JTextArea(food == null ? "" : food.getComment());
      commentEditor.setWrapStyleWord(true);
      commentEditor.setLineWrap(true);
      return commentEditor;
   }
   
   private JTabbedPane getTabPanel() {
      if (tabPanel == null) {
         tabPanel = new JTabbedPane();
         if (ToolBox.isMacOSX()) {
            tabPanel.setFont(tabPanel.getFont().deriveFont(10.0f));
         }
         //tabPanel.addTab("Measures", getMeasureEditor());
         //tabPanel.addTab("Comments", getCommentsPanel());
         tabPanel.addTab("General", getMacroNutrientsTable());
         tabPanel.addTab("Minerals", getMineralsTable());
         tabPanel.addTab("Vitamins", getVitaminsTable());
         tabPanel.addTab("Amino Acids", getAminoAcidsTable());
         tabPanel.addTab("Lipids", getLipidsTable());
      }
      return tabPanel;
   }

   protected MeasureEditor getMeasureEditor() {
      if (measureEditor == null) {
         measureEditor = new MeasureEditor(food);
         measureEditor.setBorder(makeTitleBorder("Measures"));
         measureEditor.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
               food.setMeasures(getMeasureEditor().getMeasures());
               getMeasureSelector().setFood(food);
            }
         });
      }
      return measureEditor;
   }

   
   MeasureWidget getMeasureSelector() {
      if (null == measure) {
         measure = new MeasureWidget();
         measure.setFood(food);
         measure.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
               getMacroNutrientsTable().setMultiplier(measure.getMultiplier());
               getMineralsTable().setMultiplier(measure.getMultiplier());
               getVitaminsTable().setMultiplier(measure.getMultiplier());
               getAminoAcidsTable().setMultiplier(measure.getMultiplier());
               getLipidsTable().setMultiplier(measure.getMultiplier());
            }
         });
      }
      return measure;
   }
 

   public void setMeasure(Measure m, double amount) {
      getMeasureSelector().setMeasure(m, amount);
   }

   public static void editFood(Food f) {
      if (f instanceof Recipe) {
         RecipeEditor editor = new RecipeEditor(CRONOMETER.getInstance(), (Recipe)f);
         editor.display();
      } else {
         FoodEditor editor = new FoodEditor(CRONOMETER.getInstance(), f);
         editor.display();
      }
   }

   public static void editFood(Serving s) {
      Food f = s.getFood();
      assert (f != null);
      if (f instanceof Recipe) {
         RecipeEditor editor = new RecipeEditor(CRONOMETER.getInstance(), (Recipe)f);
         editor.display();
         editor.setMeasure(s.getMeasure(), s.getAmount());
      } else {
         FoodEditor editor = new FoodEditor(CRONOMETER.getInstance(), f);
         editor.setMeasure(s.getMeasure(), s.getAmount());
         editor.display();
      }
   }

  
}
