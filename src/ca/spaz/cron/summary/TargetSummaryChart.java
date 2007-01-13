/*
 * Created on 26-Jun-2005
 */
package ca.spaz.cron.summary;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;

import ca.spaz.cron.foods.*;
import ca.spaz.cron.targets.Target;
import ca.spaz.cron.user.*;

public class TargetSummaryChart extends JComponent implements UserChangeListener {
   public static final Color CALORIE_COLOR = Color.ORANGE;
   public static final Color PROTEIN_COLOR = new Color(80,220,80);
   public static final Color CARB_COLOR = new Color(80,80,220);
   public static final Color LIPID_COLOR = new Color(220,80,80);
   public static final Color ALCOHOL_COLOR = new Color(200,230,80);
   public static final Color VITAMIN_COLOR = new Color(120,180,20);
   public static final Color MINERAL_COLOR = new Color(80,180,180);
   
   private static final double DISPLAY_THRESH = 0.005;
   
   DecimalFormat valFormat = new DecimalFormat("00");
   
   NutritionSummaryPanel summary;
   List consumed;
   
   double energy = 0;
   double protein = 0;
   double carbs = 0;
   double lipid = 0;
   double fiber = 0;
   double vitamins = 0;
   double minerals = 0;
   double pcals = 0;
   double fcals = 0;
   double ccals = 0;
   double acals = 0;
   
   
   public TargetSummaryChart(NutritionSummaryPanel summary) {
      this.summary = summary;
      User.getUser().addUserChangeListener(this);
      setFont(new Font("Application", Font.BOLD, 12));
      setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
   }
   
   public void update(List consumed) {    
      this.consumed = consumed;
      update();
   }
   
   public void update() {    
     energy = getAmount(consumed, NutrientInfo.getByName("Energy"));
     protein = getAmount(consumed, NutrientInfo.getProtein());
     carbs = getAmount(consumed, NutrientInfo.getCarbs());
     fiber = getAmount(consumed, NutrientInfo.getByName("Fiber"));
     lipid = getAmount(consumed, NutrientInfo.getFat());
     vitamins = summary.getVitaminsPanel().getTargetCompletion(false);
     minerals = summary.getMineralsPanel().getTargetCompletion(false);
     computeCalorieBreakdown(consumed);
     repaint();
   }
   
   private void computeCalorieBreakdown(List servings) {
      NutrientInfo pni = NutrientInfo.getByName("Protein");
      NutrientInfo fni = NutrientInfo.getByName("Fat");
      NutrientInfo cni = NutrientInfo.getByName("Carbs");
      NutrientInfo ani = NutrientInfo.getByName("Alcohol");
      pcals = ccals = fcals = acals = 0;
      for (Iterator iter = servings.iterator(); iter.hasNext();) {
         Serving serving = (Serving) iter.next();
         Food f = serving.getFood();
         if (serving.isLoaded()) {
            double weight = serving.getGrams()/100.0;
            pcals += weight * f.getNutrientAmount(pni) * f.getProteinConversionFactor();
            fcals += weight * f.getNutrientAmount(fni) * f.getLipidConversionFactor();
            ccals += weight * f.getNutrientAmount(cni) * f.getCarbConversionFactor();
            acals += weight * f.getNutrientAmount(ani) * f.getAlcoholConversionFactor();
         }
     }      
   }

   private double getAmount(List servings, NutrientInfo ni) {
      double total = 0;
      for (Iterator iter = servings.iterator(); iter.hasNext();) {
         Serving serving = (Serving) iter.next();
         if (serving.isLoaded()) {
            double weight = serving.getGrams()/100.0;
            total += weight * serving.getFood().getNutrientAmount(ni);
         }
     }
     return total;
   }
   
   // @TODO: refactor this code as it's highly redundant
   public void paintComponent(Graphics g) {
      User user = User.getUser();
      
      int w = getWidth();
      int h = getHeight();
      int xo = 0;
      int yo = 0;
      Border border = getBorder();
      if (border != null) {
         Insets insets = border.getBorderInsets(this);
         w -= insets.left + insets.right;
         h -= insets.top + insets.bottom;
         xo = insets.left;
         yo = insets.top;
      }
      
      //int b = 4;
      int barHeight = (h / 6) - 5;
      int pieRadius = 80;
      double barFill = 0;
       
      g.setFont(g.getFont().deriveFont(Font.BOLD));
      FontMetrics fm = g.getFontMetrics();
      Graphics2D g2d = (Graphics2D)g;      
      //g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      Target energyTarget = user.getTarget(NutrientInfo.getByName("Energy"));
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(xo,yo,w-pieRadius,barHeight);
      g.setColor(CALORIE_COLOR);      
      if (energy > DISPLAY_THRESH) {
         barFill = energy/energyTarget.getMin();
         if (barFill > 1) {
            barFill = 1;
            g.setColor(CALORIE_COLOR.brighter());
            g.fill3DRect(xo,yo,(int)((w-pieRadius)*barFill), barHeight, false);
         } else {
            g.fill3DRect(xo,yo,(int)((w-pieRadius)*barFill), barHeight, true);
         }
      }
      g.setColor(Color.BLACK);
      g.drawString("Calories: " + (int)energy +" / " + (int)energyTarget.getMin() 
            + " (" + Math.round(100*energy/energyTarget.getMin()) + "%)", 
            xo+10, yo+barHeight/2+fm.getAscent()/2);

      
      Target proteinTarget = user.getTarget(NutrientInfo.getByName("Protein"));
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(xo,yo+(barHeight+5),w-pieRadius,barHeight);
      g.setColor(PROTEIN_COLOR);
      if (protein > DISPLAY_THRESH) {
         barFill = protein/proteinTarget.getMin();
         if (barFill > 1) {
            barFill = 1;
            g.setColor(PROTEIN_COLOR.brighter());
            g.fill3DRect(xo,yo+(barHeight+5),(int)((w-pieRadius)*barFill), barHeight,false);
         } else {
            g.fill3DRect(xo,yo+(barHeight+5),(int)((w-pieRadius)*barFill), barHeight,true);
         }
      }
      g.setColor(Color.BLACK);
      g.drawString("Protein: " + (int)protein +"g / " + (int)proteinTarget.getMin() 
            + "g (" + Math.round(100*protein/proteinTarget.getMin()) + "%)", 
            xo+10, yo+(barHeight+5)+barHeight/2+fm.getAscent()/2);

      
      Target carbTarget = user.getTarget(NutrientInfo.getByName("Carbs"));
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(xo,yo+(barHeight+5)*2,w-pieRadius,barHeight);
      g.setColor(CARB_COLOR);
      if (carbs > DISPLAY_THRESH) {
         barFill = carbs/carbTarget.getMin();
         if (barFill > 1) {
            barFill = 1;
            g.setColor(CARB_COLOR.brighter());
            g.fill3DRect(xo,yo+(barHeight+5)*2,(int)((w-pieRadius)*barFill), barHeight,false);
         } else {
            g.fill3DRect(xo,yo+(barHeight+5)*2,(int)((w-pieRadius)*barFill), barHeight,true);
         }
      }
      g.setColor(Color.BLACK);
      g.drawString("Carbohydrates: " + (int)carbs +"g / " + (int)carbTarget.getMin() 
            + "g (" + Math.round(100*carbs/carbTarget.getMin()) + "%)", 
            xo+10, yo+(barHeight+5)*2+barHeight/2+fm.getAscent()/2);

      Target lipidTarget = user.getTarget(NutrientInfo.getByName("Fat"));
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(xo,yo+(barHeight+5)*3,w-pieRadius,barHeight);
      g.setColor(LIPID_COLOR);
      if (lipid > DISPLAY_THRESH) {
         barFill = lipid/lipidTarget.getMin();
         if (barFill > 1) {
            barFill = 1;
            g.setColor(LIPID_COLOR.brighter());
            g.fill3DRect(xo,yo+(barHeight+5)*3,(int)((w-pieRadius)*barFill), barHeight, false);
         } else {
            g.fill3DRect(xo,yo+(barHeight+5)*3,(int)((w-pieRadius)*barFill), barHeight,true);
         }
      }
      g.setColor(Color.BLACK);
      g.drawString("Lipids: " + (int)lipid +"g / " + (int)lipidTarget.getMin() 
            + "g (" + Math.round(100*lipid/lipidTarget.getMin()) + "%)", 
            xo+10, yo+(barHeight+5)*3+barHeight/2+fm.getAscent()/2);
      
      
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(xo,yo+(barHeight+5)*4,w-pieRadius,barHeight);
      g.setColor(VITAMIN_COLOR);
      if (vitamins > DISPLAY_THRESH) {
         barFill = vitamins;
         if (barFill > 1) {
            barFill = 1;
            g.setColor(VITAMIN_COLOR.brighter());
            g.fill3DRect(xo,yo+(barHeight+5)*4,(int)((w-pieRadius)*barFill), barHeight, false);
         } else {
            g.fill3DRect(xo,yo+(barHeight+5)*4,(int)((w-pieRadius)*barFill), barHeight,true);
         }
      }
      g.setColor(Color.BLACK);
      g.drawString("Vitamins: " + Math.round(100*vitamins) + "%", 
            xo+10, yo+(barHeight+5)*4+barHeight/2+fm.getAscent()/2); 
      
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(xo,yo+(barHeight+5)*5,w-pieRadius,barHeight);
      g.setColor(MINERAL_COLOR);
      if (minerals > DISPLAY_THRESH) {
         barFill = minerals;
         if (barFill > 1) {
            barFill = 1;
            g.setColor(MINERAL_COLOR.brighter());
            g.fill3DRect(xo,yo+(barHeight+5)*5,(int)((w-pieRadius)*barFill), barHeight, false);
         } else {
            g.fill3DRect(xo,yo+(barHeight+5)*5,(int)((w-pieRadius)*barFill), barHeight,true);
         }
      }
      g.setColor(Color.BLACK);
      g.drawString("Minerals: " + Math.round(100*minerals) + "%", 
            xo+10, yo+(barHeight+5)*5+barHeight/2+fm.getAscent()/2);

      paintPFC(g, xo+(w-pieRadius)+12, yo, pieRadius-20);
   }
   
   
   private void paintPFC(Graphics g, int xo, int yo, int radius) {      
      double total = pcals + ccals + fcals + acals;
      if (total <= 0) return;
      
      if (energy - total > 0.1) {
         ccals += (energy - total);
         total = energy;
      }
      
      g.setColor(PROTEIN_COLOR);
      int amount = 0;
      g.fillArc(xo,yo,radius-4,radius-4, amount, (int)(360*(pcals/total)));
      amount += (int)(360*(pcals/total));
      
      g.setColor(CARB_COLOR);
      g.fillArc(xo,yo,radius-4,radius-4, amount, (int)(360*(ccals/total)));
      amount += (int)(360*(ccals/total));

      g.setColor(LIPID_COLOR);
      g.fillArc(xo,yo,radius-4,radius-4, amount, (int)(360*(fcals/total))); 
      amount += (int)(360*(fcals/total));

      g.setColor(ALCOHOL_COLOR);
      g.fillArc(xo,yo,radius-4,radius-4, amount, (int)(360*(acals/total))); 

      g.setColor(getBackground().darker());
      g.drawOval(xo, yo, radius-5, radius-5);
      
      g.setFont(new Font("Courier", Font.BOLD, 12));
      String str =
         valFormat.format(((int)Math.round(100*(pcals/total)))) + ":" +
         valFormat.format(((int)Math.round(100*(ccals/total)))) + ":" +
         valFormat.format(((int)Math.round(100*(fcals/total))));
      if (acals > 0) {
         str += ":"+valFormat.format(((int)Math.round(100*(acals/total))));
      }
      int sw = xo  + radius/2 - g.getFontMetrics().stringWidth(str)/2;
       
      str = valFormat.format(((int)Math.round(100*(pcals/total))))+":";
      g.setColor(getBackground().darker());
      g.drawString(str, sw-1+1, yo+radius+7+1);
      g.setColor(PROTEIN_COLOR.darker());       
      g.drawString(str, sw-1, yo+radius+7);
      
      str = "   "+valFormat.format(((int)Math.round(100*(ccals/total))))+":";
      g.setColor(getBackground().darker());       
      g.drawString(str, sw-1+1, yo+radius+7+1);
      g.setColor(CARB_COLOR); 
      g.drawString(str, sw-1, yo+radius+7);
      
      str = "      "+valFormat.format(((int)Math.round(100*(fcals/total))));
      if (acals > 0) str += ":";
      g.setColor(getBackground().darker());   
      g.drawString(str, sw-1+1, yo+radius+7+1);
      g.setColor(LIPID_COLOR);       
      g.drawString(str, sw-1, yo+radius+7);
      
      if (acals > 0) {
         str = "         "+valFormat.format(((int)Math.round(100*(acals/total))));
         g.setColor(ALCOHOL_COLOR.darker()); 
         g.drawString(str, sw-1, yo+radius+7);
      }   
   }

  /* public Dimension getPreferredSize() {
      return new Dimension(500, 180);
   }


   public Dimension getMinimumSize() {
      return new Dimension(500, 180);
   }*/
   
   public void userChanged(User user) {
      update();
   }
}
