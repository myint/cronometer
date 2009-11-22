/*
 * Created on 26-Jun-2005
 */
package ca.spaz.cron.summary;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

import ca.spaz.cron.exercise.Exercise;
import ca.spaz.cron.foods.*;
import ca.spaz.cron.targets.Target;
import ca.spaz.cron.user.*;
import ca.spaz.util.ToolBox;

/**
 * @TODO: break up into real components
 * 
 * @author davidson
 */
public class TargetSummaryChart extends JComponent implements UserChangeListener {
   public static final Color CALORIE_COLOR = Color.ORANGE;
   public static final Color EXERCISE_COLOR = new Color(200,160,30);
   public static final Color PROTEIN_COLOR = new Color(80,220,80);
   public static final Color CARB_COLOR = new Color(80,80,220);
   public static final Color LIPID_COLOR = new Color(220,80,80);
   public static final Color ALCOHOL_COLOR = new Color(200,230,80);
   public static final Color VITAMIN_COLOR = new Color(120,180,20);
   public static final Color MINERAL_COLOR = new Color(80,180,180);
   
   private static final double DISPLAY_THRESH = 2;
   
   DecimalFormat valFormat = new DecimalFormat("00");
   
   NutritionSummaryPanel summary;
   List consumed;
   List exercises;
   
   boolean allSelected;
   
   double energy = 0;
   double energyBurned = 0;
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
   double total = 0;
   
   public TargetSummaryChart(NutritionSummaryPanel summary) {
      this.summary = summary;
      UserManager.getUserManager().addUserChangeListener(this);
      setFont(new Font("Application", Font.BOLD, 12));
      setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
   }
   
   public void update(List consumed, boolean allSelected) {    
      this.consumed = consumed;
      this.allSelected = allSelected;
      update();
   }
   
   public void updateExercises(List exercises) {
      this.exercises = exercises;
      update();
   }
   
   public void update() {    
     energy = getAmount(consumed, NutrientInfo.getByName("Energy"));
     
     if(allSelected)
     {
        energyBurned = getEnergyBurned(exercises);
     }
     else
     {
        energyBurned = 0;
     }
     
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
            double weight = serving.getGrams() / 100.0;
            pcals += weight * f.getNutrientAmount(pni)
                  * f.getProteinConversionFactor();
            fcals += weight * f.getNutrientAmount(fni)
                  * f.getLipidConversionFactor();
            ccals += weight * f.getNutrientAmount(cni)
                  * f.getCarbConversionFactor();
            acals += weight * f.getNutrientAmount(ani)
                  * f.getAlcoholConversionFactor();
         }
      }
      total = pcals + ccals + fcals + acals;
      if (total > 0 && energy - total > 0.1) {
         ccals += (energy - total);
         total = energy;
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
   
   private double getEnergyBurned(List exercises) {
      double total = 0;
      
      if(exercises != null) {
         for (Iterator iter = exercises.iterator(); iter.hasNext();) {
            Exercise exercise = (Exercise) iter.next();
            total += exercise.getCalories();
         }
      }
      return total;
   }

   private void paintBar(Graphics2D g, int x, int y, int w, int h, int w2, Color col) {
      GradientPaint gradient = new GradientPaint(0, 0, Color.GRAY, w, 0, Color.LIGHT_GRAY, false);
      
      g.setPaint(gradient); 
      g.fillRoundRect(x, y, w, h, h/2, h/2); 
      if (w2 > w) { 
         w2 = w;
      }
      if (w2 > DISPLAY_THRESH) {
         gradient = new GradientPaint(0, 0, col.brighter(), w, 0, col.darker(), false);            
         g.setPaint(gradient); 
         g.fillRoundRect(x, y, w2, h, h/2, h/2);
      }
      g.setColor(Color.GRAY);
      g.drawRoundRect(x, y, w, h, h/2, h/2); 
   }
   
   private void paintBar(Graphics2D g, int x, int y, int w, int h, int w2, int secondaryW, Color col, Color secondCol) {
      GradientPaint gradient = new GradientPaint(0, 0, Color.GRAY, w, 0, Color.LIGHT_GRAY, false);
      
      g.setPaint(gradient); 
      g.fillRoundRect(x, y, w, h, h/2, h/2); 
      if (w2 > w) {
         secondaryW = Math.max(secondaryW - (w2 - w), 0);
         w2 = w;
      }
      
      if (secondaryW > w2)
      {
         secondaryW = w2;
      }
      
      if (w2 > DISPLAY_THRESH) {
         gradient = new GradientPaint(0, 0, col.brighter(), w, 0, col.darker(), false);            
         g.setPaint(gradient); 
         g.fillRoundRect(x, y, w2, h, h/2, h/2);
      }
      
      if (secondaryW > DISPLAY_THRESH)
      {
         g.setColor(secondCol); 
         g.fillRoundRect(x + w2 - secondaryW, y, secondaryW, h, h/2, h/2);
      }
      
      g.setColor(Color.GRAY);
      g.drawRoundRect(x, y, w, h, h/2, h/2); 
   }
   

   private void paintCaloriesBar(Graphics2D g, int x, int y, int w, int h, int w2, Color col) {
      GradientPaint gradient = new GradientPaint(0, 0, Color.GRAY, w, 0, Color.LIGHT_GRAY, false);            
      g.setPaint(gradient); 
      g.fillRect(x, y, w, h); 
      if (w2 > w) {
         w2 = w;
      }
      if (w2 > DISPLAY_THRESH) {
         int s = x;
         int e = (int)(w2*(pcals/total));
         gradient = new GradientPaint(s, 0, PROTEIN_COLOR, s+e, 0, CARB_COLOR, false);            
         g.setPaint(gradient); 
         g.setColor(PROTEIN_COLOR);
         g.fillRect(s, y, e, h);

         s += e;
         e = (int)(w2*(ccals/total));        
         gradient = new GradientPaint(s, 0, CARB_COLOR, s+e, 0, LIPID_COLOR, false);            
         g.setPaint(gradient); 
         g.setColor(CARB_COLOR);
         g.fillRect(s, y, e, h);

         s += e;
         e = (int)(w2*(fcals/total));        
         gradient = new GradientPaint(s, 0, LIPID_COLOR, s+e, 0, ALCOHOL_COLOR, false);            
         g.setPaint(gradient); 
         g.setColor(LIPID_COLOR);
         g.fillRect(s, y, e, h);

         s += e;
         e = (int)(w2*(acals/total));        
         gradient = new GradientPaint(s, 0, ALCOHOL_COLOR, s+e, 0, ALCOHOL_COLOR, false);            
         g.setPaint(gradient); 
         g.setColor(ALCOHOL_COLOR);
         g.fillRect(s, y, e, h);
         
      }
      g.setColor(Color.GRAY);
      g.drawRect(x, y, w, h); 
   }
   
   // @TODO: refactor this code more, as it's highly redundant
   public void paintComponent(Graphics g) {
      User user = UserManager.getCurrentUser();
      
      int w = getWidth();
      int h = getHeight();
      int xo = 0;
      int yo = 0;
      Border border = getBorder();
      if (border != null) {
         Insets insets = border.getBorderInsets(this);
         w -= insets.left + insets.right + 90;
         h -= insets.top + insets.bottom;
         xo = insets.left;
         yo = insets.top;
      }
      
      //int b = 4;
      int barHeight = (h / 6) - 5;
      double barFill = 0;
       
      g.setFont(g.getFont().deriveFont(Font.BOLD));
      FontMetrics fm = g.getFontMetrics();
      Graphics2D g2d = (Graphics2D)g;      
      //g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      Target energyTarget = user.getTarget(NutrientInfo.getByName("Energy"));  
      barFill = ToolBox.safeDivide(energy, energyTarget.getMin());
      double secondaryBarFill = ToolBox.safeDivide(energyBurned, energyTarget.getMin());
      paintBar(g2d, xo, yo+(barHeight+5)*0, w, barHeight, (int)(w*barFill), (int)(w*secondaryBarFill), CALORIE_COLOR, EXERCISE_COLOR);
      g.setColor(Color.BLACK);
      String calorieString = "Calories: " + (int)energy;
      
      if(energyBurned > 0)
      {
         calorieString += " (" + (int) (energy - energyBurned) + ")";
      }
      
      calorieString += " / " + (int)energyTarget.getMin() 
         + " (" + Math.round(100*barFill) + "%)";
      g.drawString(calorieString, xo+10, yo+barHeight/2+fm.getAscent()/2);
      
      
      Target proteinTarget = user.getTarget(NutrientInfo.getByName("Protein"));
      barFill = ToolBox.safeDivide(protein,proteinTarget.getMin());
      paintBar(g2d, xo, yo+(barHeight+5)*1, w, barHeight, (int)(w*barFill), PROTEIN_COLOR);
      g.setColor(Color.BLACK);
      g.drawString("Protein: " + (int)protein +"g / " + (int)proteinTarget.getMin() 
            + "g (" + Math.round(100*barFill) + "%)", 
            xo+10, yo+(barHeight+5)+barHeight/2+fm.getAscent()/2);

      
      Target carbTarget = user.getTarget(NutrientInfo.getByName("Carbs"));
      barFill = ToolBox.safeDivide(carbs,carbTarget.getMin());
      paintBar(g2d, xo, yo+(barHeight+5)*2, w, barHeight, (int)(w*barFill), CARB_COLOR);
      g.setColor(Color.BLACK);
      g.drawString("Carbohydrates: " + (int)carbs +"g / " + (int)carbTarget.getMin() 
            + "g (" + Math.round(100*barFill) + "%)", 
            xo+10, yo+(barHeight+5)*2+barHeight/2+fm.getAscent()/2);

      Target lipidTarget = user.getTarget(NutrientInfo.getByName("Fat"));
      barFill = ToolBox.safeDivide(lipid,lipidTarget.getMin());
      paintBar(g2d, xo, yo+(barHeight+5)*3, w, barHeight, (int)(w*barFill), LIPID_COLOR);
      g.setColor(Color.BLACK);
      g.drawString("Lipids: " + (int)lipid +"g / " + (int)lipidTarget.getMin() 
            + "g (" + Math.round(100*barFill) + "%)", 
            xo+10, yo+(barHeight+5)*3+barHeight/2+fm.getAscent()/2);      
      
      barFill = vitamins;
      paintBar(g2d, xo, yo+(barHeight+5)*4, w, barHeight, (int)(w*barFill), VITAMIN_COLOR);
      g.setColor(Color.BLACK);
      g.drawString("Vitamins: " + Math.round(100*barFill) + "%", 
            xo+10, yo+(barHeight+5)*4+barHeight/2+fm.getAscent()/2); 
      
      barFill = minerals;
      paintBar(g2d, xo, yo+(barHeight+5)*5, w, barHeight, (int)(w*barFill), MINERAL_COLOR);
      g.setColor(Color.BLACK);
      g.drawString("Minerals: " + Math.round(100*barFill) + "%", 
            xo+10, yo+(barHeight+5)*5+barHeight/2+fm.getAscent()/2);

      paintPFC(g, xo+w+12, yo, 80);
   }
   
   
   private void paintPFC(Graphics g, int xo, int yo, int radius) {      
      if (total <= 0) return;
      
      g.setColor(PROTEIN_COLOR);
      int amount = 0;
      g.fillArc(xo,yo,radius-4,radius-4, amount, (int)Math.round(360*pcals/total));
      amount += (int)(360*(pcals/total));
      
      g.setColor(CARB_COLOR);
      g.fillArc(xo,yo,radius-4,radius-4, amount, (int)Math.round(360*ccals/total));
      amount += (int)(360*(ccals/total));

      g.setColor(LIPID_COLOR);
      g.fillArc(xo,yo,radius-4,radius-4, amount, (int)Math.round((360*fcals/total))); 
      amount += (int)(360*(fcals/total));

      g.setColor(ALCOHOL_COLOR);
      g.fillArc(xo,yo,radius-4,radius-4, amount, (int)Math.round((360*acals/total))); 

      g.setColor(Color.GRAY);
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

   public void userChanged(UserManager userMan) {
      update();
   }
}
