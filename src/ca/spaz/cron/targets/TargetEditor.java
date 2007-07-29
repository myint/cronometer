/*
 * Created on 4-Jun-2005
 */
package ca.spaz.cron.targets;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.foods.NutrientInfo;
import ca.spaz.cron.user.*;
import ca.spaz.gui.WrappedPanel;
import ca.spaz.gui.WrapperDialog;
import ca.spaz.util.ImageFactory;
import ca.spaz.util.ToolBox;

/** 
 * 
 * A UI panel that will let the user view and edit their 
 * nutritional targets.
 * 
 * Ideally, there will be a mechanism to suggest good default
 * targets based on age, weight, gender, and other factors.
 * 
 * @author Aaron Davidson
 */
public class TargetEditor extends WrappedPanel {
   private User user;

   private TargetEditorTable macro, minerals, vitamins, aminoacids, lipids;
   private JTabbedPane tabPanel;  

   private JPanel mainPanel;  
   private JPanel defaultsPanel;  
   private JButton setDefaultsBtn;
 
   
   public TargetEditor(User user) {
      this.user = user;
      this.setLayout(new BorderLayout(8,8));
      this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));     
      this.add(getDefaultsPanel(), BorderLayout.NORTH);
      this.add(getTabPanel(), BorderLayout.CENTER);
      this.add(getRatioPanel(), BorderLayout.SOUTH);
   }
   
   private JTabbedPane getTabPanel() {
      if (tabPanel == null) {
         tabPanel = new JTabbedPane();
         if (ToolBox.isMacOSX()){
            tabPanel.setFont(getFont().deriveFont(10.0f));
         }
         tabPanel.addTab("General", getMacroNutrientsTable());
         tabPanel.addTab("Vitamins", getVitaminsTable());
         tabPanel.addTab("Minerals", getMineralsTable());
         tabPanel.addTab("Amino Acids", getAminoAcidsTable());
         tabPanel.addTab("Lipids", getLipidsTable());
      }
      return tabPanel;
   }

   private TargetEditorTable getMacroNutrientsTable() {
      if (macro == null) {
         macro = new TargetEditorTable(user, NutrientInfo.getMacroNutrients());
      }
      return macro;
   }

   private TargetEditorTable getMineralsTable() {
      if (minerals == null) {
         minerals = new TargetEditorTable(user, NutrientInfo.getMinerals());
      }
      return minerals;
   }
   
   private TargetEditorTable getVitaminsTable() {
      if (vitamins == null) {
         vitamins = new TargetEditorTable(user, NutrientInfo.getVitamins());
      }
      return vitamins;
   }
   
   private TargetEditorTable getAminoAcidsTable() {
      if (aminoacids == null) {
         aminoacids = new TargetEditorTable(user, NutrientInfo.getAminoAcids());
      }
      return aminoacids;
   }
   
   private TargetEditorTable getLipidsTable() {
      if (lipids == null) {
         lipids = new TargetEditorTable(user, NutrientInfo.getLipids());
      }
      return lipids;
   } 
   
   private JPanel getDefaultsPanel() {
      if (defaultsPanel == null) {
         defaultsPanel = new JPanel();
         defaultsPanel.add(getSetDefaultsButton(), BorderLayout.CENTER); 
      }
      return defaultsPanel;
   }
     
   private JButton getSetDefaultsButton() {
      if (setDefaultsBtn == null) {
         setDefaultsBtn = new JButton("Set to Dietary Reference Intakes");
         setDefaultsBtn.setToolTipText(
               "Resets all targets to Dietary Reference " +
               "Intakes based on your gender and age.");
         setDefaultsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               setDefaultTargets(new DRITargetModel());
            }
         });
      }
      return setDefaultsBtn;
   }
    
 
   public static void editTargets() {
      WrapperDialog.showDialog(CRONOMETER.getInstance(), new TargetEditor(UserManager.getCurrentUser()), true);      
   }
   
   
   private void setDefaultTargets(TargetModel model) {
      if (UserSettingsDialog.showDialog(UserManager.getUserManager(), setDefaultsBtn)) { 
         int rc = JOptionPane.showConfirmDialog(this,
               "Are you sure you want to replace the current targets with" +
               " '"+ model.toString()+"'?", 
               "Replace Targets?", JOptionPane.YES_NO_OPTION);
         if (rc != JOptionPane.YES_OPTION) return;
         setDefaultTargets(model, user);
         setRatios();
         getAminoAcidsTable().fireTargetsChanged();
         getMacroNutrientsTable().fireTargetsChanged();
         getMineralsTable().fireTargetsChanged();
         getVitaminsTable().fireTargetsChanged();
         getLipidsTable().fireTargetsChanged();
      }
   }
   
   public static void setDefaultTargets(TargetModel model, User user) {
      Iterator iter = NutrientInfo.getGlobalList().iterator();
      while (iter.hasNext()) {
         NutrientInfo ni = (NutrientInfo)iter.next();
         Target target = new Target();
         target.setMin(model.getTargetMinimum(user, ni));
         target.setMax(model.getTargetMaximum(user, ni));
         user.setTarget(ni, target);         
      }      
      user.setCustomTargets(false);
   }

   public boolean doAccept() { return true; }

   public void doCancel() { 
   }

   public String getInfoString() { 
      return  "<div align=\"justify\" width=\"180\"><br>"         
            + "Set your nutritional targets for tracking within the program.<br><br>"
            + "Clicking 'Set to Dietary Reference Intakes' will set all the targets "
            + "to values configured for your weight, height, gender, age, and activity level. "
            + "These values are based on the DRIs published by USDA.*<br><br>"
            + "You may also edit the targets directly in the table to the right "
            + "choosing customized values for your tracking. <br><br>"
            + "<small>* DRIs are not specified for all nutrients.<br></small>"
            + "</div>";
   }

   public boolean showSidebar() { 
      return true;
   }
   
   public ImageIcon getIcon() {
      return new ImageIcon(ImageFactory.getInstance().loadImage("/img/apple-100x100.png"));
   }
    
   public String getSubtitle() {
      return "Set Nutritional Targets";
   }

   public String getTitle() {
      return "Set Nutritional Targets";
   }

   public boolean isCancellable() { 
      return false;
   }
   
   private JPanel ratioPanel;
   
   private JPanel getRatioPanel() {
      if (ratioPanel == null) {
         ratioPanel = new JPanel();
         ratioPanel.setBorder(BorderFactory.createTitledBorder("Set Target Macro-Nutrient Ratios:"));
         ratioPanel.setLayout(new BoxLayout(ratioPanel, BoxLayout.X_AXIS));
         
         ratioPanel.add(new JLabel("Protein:"));
         ratioPanel.add(getProteinSpinner());
         ratioPanel.add(Box.createHorizontalStrut(7));
         ratioPanel.add(new JLabel("Carbs:"));
         ratioPanel.add(getCarbSpinner());
         ratioPanel.add(Box.createHorizontalStrut(7));
         ratioPanel.add(new JLabel("Fat:"));
         ratioPanel.add(getFatSpinner());
         ratioPanel.add(Box.createHorizontalStrut(7));
         ratioPanel.add(getSetRatioButton());
      }
      return ratioPanel;
   }
   
   private void setRatios() {
      int protein = ((Number)getProteinSpinner().getValue()).intValue();
      int carbs = ((Number)getCarbSpinner().getValue()).intValue();               
      int fat = ((Number)getFatSpinner().getValue()).intValue();
      int total = protein + carbs + fat;
      if (total != 100) {
         Toolkit.getDefaultToolkit().beep();
         protein = 100*protein / total;
         carbs = 100*carbs / total;
         fat = 100*fat / total;
         total = protein + carbs + fat;
         if (total < 100) {
            carbs += 100 - total;
         }
         getProteinSpinner().setValue(new Integer(protein));
         getCarbSpinner().setValue(new Integer(carbs));
         getFatSpinner().setValue(new Integer(fat));
      }
      
      Target t = user.getTarget(NutrientInfo.getCalories());
      double calories = t.getMin();
      double pGrams = Math.round((calories * (protein/100.0)) / 4);
      double cGrams = Math.round((calories * ((carbs)/100.0)) / 4);
      double fGrams = Math.round((calories * (fat/100.0)) / 9);
      cGrams += user.getTarget(NutrientInfo.getFiber()).getMin();
      
      user.setTarget(NutrientInfo.getProtein(), new Target(pGrams, Math.round(pGrams*1.25)));
      user.setTarget(NutrientInfo.getCarbs(), new Target(cGrams, Math.round(cGrams*1.25)));
      user.setTarget(NutrientInfo.getFat(), new Target(fGrams, Math.round(fGrams*1.25)));
      user.setProteinPercentage(protein);
      user.setCarbPercentage(carbs);
      user.setFatPercentage(fat);
      getMacroNutrientsTable().model.fireTableDataChanged(); 
   }
   
   private JButton setRatioButton;
   
   private JButton getSetRatioButton() {
      if (setRatioButton == null) { 
         setRatioButton = new JButton("Set");
         setRatioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
               setRatios();       
            }          
         });
      }
      return setRatioButton;
   }
   
   private JSpinner proteinSpinner;
   private JSpinner getProteinSpinner() {
      if (proteinSpinner == null) {
         proteinSpinner = new JSpinner(new SpinnerNumberModel(
               user.getProteinPercentage(), 0, 100, 1));
         proteinSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                
            }
         });          
      }
      return proteinSpinner;
   }   
   
   private JSpinner carbSpinner;
   private JSpinner getCarbSpinner() {
      if (carbSpinner == null) {
         carbSpinner = new JSpinner(new SpinnerNumberModel(
               user.getCarbPercentage(), 0, 100, 1));
         carbSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                
            }
         });          
      }
      return carbSpinner;
   }   
   
   
   private JSpinner fatSpinner;
   private JSpinner getFatSpinner() {
      if (fatSpinner == null) {
         fatSpinner = new JSpinner(new SpinnerNumberModel(
               user.getFatPercentage(), 0, 100, 1));
         fatSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                
            }
         });          
      }
      return fatSpinner;
   }   
   
}
