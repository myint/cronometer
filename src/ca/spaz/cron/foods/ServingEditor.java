/*
 * Created on 21-Mar-2005
 */
package ca.spaz.cron.foods;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ca.spaz.cron.ui.SearchPanel;
import ca.spaz.gui.TranslucentLabel;
import ca.spaz.gui.TranslucentPanel;
 

/**
 * This is panel that displays some brief summary information about a Food item
 * and allows choosing of a measure to be used for either adding or updating a
 * Consumed food entry in the user data.
 * 
 * @author davidson
 */
public class ServingEditor extends JPanel {  
    

   private Serving cur;

   private JLabel titleLabel;

   private JLabel energyLabel, carbsLabel, proteinLabel, fatLabel, waterLabel,
         fiberLabel;

   private JButton addButton;
   private ActionListener addAction;
   private MeasureWidget measure;

   private JPanel addEatenPanel;

   private JPanel nutrientsPanel;
   
   private JPanel emptyPanel;
   private JPanel mainPanel;
   private CardLayout cards;
   private DecimalFormat labelFormatter;

   private Vector listeners = new Vector();
   
   public ServingEditor() {
      initialize();
      labelFormatter = new DecimalFormat("#####0.0");
   }

   private void initialize() {
      cards = new CardLayout();
      this.setLayout(cards);
      this.setOpaque(false);
      this.add(getMainPanel(), "MAIN");
      this.add(getEmptyPanel(), "EMPTY");
      cards.show(this, "EMPTY");
   }
   
   private JPanel getMainPanel() {
      if (mainPanel == null) {
         mainPanel = new JPanel(new BorderLayout(3, 3));
         mainPanel.setOpaque(false);
         mainPanel.add(getAddEatenPanel(), BorderLayout.SOUTH);
         mainPanel.add(getNutrientsPanel(), BorderLayout.CENTER);
         mainPanel.add(getTitleLabel(), BorderLayout.NORTH);
      } 
      return mainPanel;
   }

   private JPanel getEmptyPanel() {
      if (emptyPanel == null) {
         JLabel empty = new JLabel(
            "<html><h3 align=\"center\">" +
            "no food selected</h3></html>",
            JLabel.CENTER);
         emptyPanel = new TranslucentPanel(0.50);
         emptyPanel.setLayout(new BorderLayout(4, 4));   
         emptyPanel.add(empty);
      } 
      return emptyPanel;
   }

   
   /**
    * @return
    */
   private JPanel getAddEatenPanel() {
      if (null == addEatenPanel) {
         addEatenPanel = new JPanel();
         addEatenPanel.setOpaque(false);
         addEatenPanel.setLayout(new BoxLayout(addEatenPanel, BoxLayout.X_AXIS));
         addEatenPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
         addEatenPanel.add(Box.createHorizontalGlue());
         addEatenPanel.add(getMeasure());
         addEatenPanel.add(Box.createHorizontalStrut(5));
         addEatenPanel.add(getAddButton());
         addEatenPanel.add(Box.createHorizontalGlue());

      }
      return addEatenPanel;
   }


   private JLabel getTitleLabel() {
      if (null == titleLabel) {
         titleLabel = new TranslucentLabel(0.85, " ", JLabel.CENTER);
         titleLabel.setBackground(Color.BLACK);
         titleLabel.setForeground(Color.WHITE);
         titleLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
         titleLabel.setFont(new Font("Application", Font.BOLD, 12));
      }
      return titleLabel;
   }

   
   private JPanel getNutrientsPanel() {
      if (null == nutrientsPanel) {
         nutrientsPanel = new TranslucentPanel(0.20f);
         nutrientsPanel.setBackground(Color.BLACK);
         nutrientsPanel.setForeground(Color.WHITE);
         nutrientsPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
         nutrientsPanel.setLayout(new GridLayout(2, 3, 6, 3));

         nutrientsPanel.add(createFieldLabel("Energy:", true));
         nutrientsPanel.add(getEnergyLabel());

         nutrientsPanel.add(createFieldLabel("Water:", true));
         nutrientsPanel.add(getWaterLabel());

         nutrientsPanel.add(createFieldLabel("Fiber:", true));
         nutrientsPanel.add(getFiberLabel());

         nutrientsPanel.add(createFieldLabel("Protein:", true));
         nutrientsPanel.add(getProteinLabel());

         nutrientsPanel.add(createFieldLabel("Carbs:", true));
         nutrientsPanel.add(getCarbsLabel());

         nutrientsPanel.add(createFieldLabel("Fat:", true));
         nutrientsPanel.add(getFatLabel());
      }
      return nutrientsPanel;
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

   public MeasureWidget getMeasure() {
      if (null == measure) {
         measure = new MeasureWidget();
         measure.setOpaque(false); 
         measure.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
               update();
            }
         });
         measure.addActionListener(getAddAction());
      }
      return measure;
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
               if (cur.getFoodProxy().isDeprecated()) {
                  if (!deprecatedFoodWarning()) return;
               }
               fireServingChosenEvent();
            }
         };
      }
      return addAction;
   }

   private boolean deprecatedFoodWarning() {
      int choice = JOptionPane.showConfirmDialog(this, 
            "Warning!\nThis food item is considered obsolete.\n" +
            "Are you sure you want to add it?",
            "Food Obsolete", JOptionPane.YES_NO_OPTION);
      return (choice == JOptionPane.YES_OPTION);
   }
   
   /**
    * @return
    */
   private Component getFatLabel() {
      if (null == fatLabel) {
         fatLabel = new JLabel("");
      }
      return fatLabel;
   }

   /**
    * @return
    */
   private Component getProteinLabel() {
      if (null == proteinLabel) {
         proteinLabel = new JLabel("");
      }
      return proteinLabel;
   }

   /**
    * @return
    */
   private Component getCarbsLabel() {
      if (null == carbsLabel) {
         carbsLabel = new JLabel("");
      }
      return carbsLabel;
   }

   /**
    * @return
    */
   private Component getFiberLabel() {
      if (null == fiberLabel) {
         fiberLabel = new JLabel("");
      }
      return fiberLabel;
   }

   /**
    * @return
    */
   private Component getWaterLabel() {
      if (null == waterLabel) {
         waterLabel = new JLabel("");
      }
      return waterLabel;
   }

   /**
    * @return
    */
   private JLabel getEnergyLabel() {
      if (null == energyLabel) {
         energyLabel = new JLabel("");
      }
      return energyLabel;
   }

   public void setServing(Serving c) {
      cur = c;
      if (c == null) {
         cards.show(this, "EMPTY");
      } else {
         setFood(c.getFood());
         setWeight(c.getMeasure(), c.getAmount());
      }
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
   
   private void setFood(Food f) {
      getMeasure().setFood(f);
      getTitleLabel().setText(fixString(f.getDescription()));
      getAddButton().setText("Add");
      cards.show(this, "MAIN");      
   }

   public void setWeight(Measure w, double mult) {
      getMeasure().setMeasure(w, mult);
   }

   public void update() {
      Food curFood = cur.getFood();
      double mult = getMeasure().getMultiplier();
      
      double cals = mult * curFood.getNutrientAmount(NutrientInfo.getByName("Energy"));
      energyLabel.setText(labelFormatter.format(cals) + " kcal");

      double protein = mult * curFood.getNutrientAmount(NutrientInfo.getByName("Protein"));
      proteinLabel.setText(labelFormatter.format(protein) + " g");

      double carbs = mult * curFood.getNutrientAmount(NutrientInfo.getByName("Carbs"));
      carbsLabel.setText(labelFormatter.format(carbs) + " g");

      double fat = mult * curFood.getNutrientAmount(NutrientInfo.getByName("Fat"));
      fatLabel.setText(labelFormatter.format(fat) + " g");

      double water = mult * curFood.getNutrientAmount(NutrientInfo.getByName("Water"));
      waterLabel.setText(labelFormatter.format(water) + " g");

      double fiber = mult * curFood.getNutrientAmount(NutrientInfo.getByName("Fiber"));
      fiberLabel.setText(labelFormatter.format(fiber) + " g");
   }

   
   public synchronized void addServingEditorListener(ServingEditorListener sel) {
      listeners.add(sel);
   }

   public synchronized void removeServingEditorListener(ServingEditorListener sel) {
      listeners.remove(sel);
   }

   private synchronized void fireServingChosenEvent() {
      cur.setGrams(getMeasure().getGrams());
      cur.setMeasure(getMeasure().getSelectedMeasure());
      for (int i=0; i<listeners.size(); i++) {
         ServingEditorListener sel = (ServingEditorListener)listeners.get(i);
         sel.servingChosen(cur);
      }
   }
 
   public void linkToSearchResults(SearchPanel sp) {
      getMeasure().linkToSearchResults(sp);
  }
  

}
