/*
 * Created on 5-Jan-2006
 */
package ca.spaz.cron.targets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.datadosen.component.RiverLayout;
import ca.spaz.cron.foods.NutrientInfo;
import ca.spaz.cron.user.UserManager;
import ca.spaz.gui.WrappedPanel;
import ca.spaz.util.ImageFactory;

public class NutrientInfoPanel extends WrappedPanel {
   private DecimalFormat df = new DecimalFormat("######0.0");
   
   private JSpinner min;
   private JSpinner max;
   private NutrientInfo ni; 
   private JPanel targetPanel;
   private JButton rdaBtn;
   private Target target;

   private SpinnerNumberModel maxModel, minModel;
   
   public NutrientInfoPanel(NutrientInfo ni) {
      this.ni = ni;
      target = UserManager.getCurrentUser().getTarget(ni);     
      setLayout(new BorderLayout(8,8));      
      add(getTargetPanel(), BorderLayout.CENTER);
      setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
   }
 
   
   public String getTitle() {
      return "Nutrient Info: " + ni.getName();
   }


   public String getSubtitle() {
      return ni.getName();
   }

   public String getInfoString() {
      StringBuffer sb = new StringBuffer();
      sb.append("<b>");
      sb.append(ni.getCategory());
      sb.append("<br><br><u>");
      sb.append(ni.getName());
      sb.append("</u></b><br><br>");
      
      if (ni.getReferenceDailyIntake() > 0) {
         sb.append("RDI: " + df.format(ni.getReferenceDailyIntake()) + " " + ni.getUnits());
         sb.append("<br>");
      }

      DRITargetModel model = new DRITargetModel();
      DRI dri = model.findMatch(UserManager.getCurrentUser(), ni.getDRIs());
      if (dri != null) {
         sb.append("RDA: " +  df.format(dri.getRDA()) + " " + ni.getUnits());
         sb.append("<br>");
      }
      if (dri != null) {
         if (dri.getTUL() > 0) {
            sb.append("TUL: " +  df.format(dri.getTUL()) + " " + ni.getUnits());
            sb.append("<br>");
         }
      }
      
      return sb.toString();
   }

   
   public ImageIcon getIcon() {
      return  new ImageIcon(ImageFactory.getInstance().loadImage("/img/apple-50x50.png"));
   }

   public boolean isCancellable() {
      return true;
   }

   public void doCancel() { }

   public boolean doAccept() {
      setTargets();
      return true;
   }
 
   private JPanel getTargetPanel() {
      if (targetPanel == null) {
         targetPanel = new JPanel(new RiverLayout());
 
         targetPanel.add("br", new JLabel("Minimum:"));
         targetPanel.add("tab hfill", getMinTarget());
         targetPanel.add("", new JLabel(ni.getUnits()));
                
         targetPanel.add("br", new JLabel("Maximum:"));
         targetPanel.add("tab hfill", getMaxTarget());
         targetPanel.add("", new JLabel(ni.getUnits()));
         
         targetPanel.add("p center", getResetButton());
         
         targetPanel.add("p", Box.createRigidArea(new Dimension(150,10)));
      }
      return targetPanel;
   }
   
   public JButton getResetButton() {
      if (rdaBtn == null) {
         rdaBtn = new JButton("Reset to RDA");
         rdaBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               DRITargetModel model = new DRITargetModel();
               getMinModel().setValue(new Double(model.getTargetMinimum(UserManager.getCurrentUser(), ni)));
               getMaxModel().setValue(new Double(model.getTargetMaximum(UserManager.getCurrentUser(), ni)));
            } 
         });
      }
      return rdaBtn;
   }
   
   
   public SpinnerNumberModel getMaxModel() {
      if (maxModel == null) {
         maxModel = new SpinnerNumberModel();
         maxModel.setMinimum(new Double(0));
         maxModel.setValue(new Double(target.getMax()));
      }
      return maxModel;
   }
   
   public SpinnerNumberModel getMinModel() {
      if (minModel == null) {
         minModel = new SpinnerNumberModel();
         minModel.setMinimum(new Double(0));      
         minModel.setValue(new Double(target.getMin()));
      }
      return minModel;
   }
   
   public JSpinner getMaxTarget() {
      if (max == null) {
         max = new JSpinner(getMaxModel());          
         max.setEditor(new JSpinner.NumberEditor(max, "#####0.0#"));
         max.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
               double curMax = getMaxModel().getNumber().doubleValue();
               double curMin = getMinModel().getNumber().doubleValue();
               if (curMin > curMax) {
                  getMinModel().setValue(maxModel.getNumber());
               }
            }
         });
      }
      return max;
   }   

   public JSpinner getMinTarget() {
      if (min == null) {      
         min = new JSpinner(getMinModel());        
         min.setEditor(new JSpinner.NumberEditor(min, "#####0.0#"));

         min.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
               double curMax = getMaxModel().getNumber().doubleValue();
               double curMin = getMinModel().getNumber().doubleValue();
               if (curMax < curMin) {
                  getMaxModel().setValue(minModel.getNumber());
               }     
            }            
         });
      }
      return min;
   }
 
   private void setTargets() {
      double val; 
      val = ((Number)getMinTarget().getValue()).doubleValue();
      target.setMin(val);
      val = ((Number)getMaxTarget().getValue()).doubleValue();
      target.setMax(val);
      UserManager.getCurrentUser().setTarget(ni, target);
   }
   
}

