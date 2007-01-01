/*
 * Created on 14-May-2005
 */
package ca.spaz.cron.summary;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;

import ca.spaz.cron.foods.NutrientInfo;

public class LipidSummaryPanel extends AbstractNutrientSummaryPanel {
   
   public LipidSummaryPanel() {
      setLayout(new BorderLayout());
      setBorder(BorderFactory.createEmptyBorder(4,4,4,4));      
      add(getNutrientTablePane(), BorderLayout.CENTER);
   }

   protected List getNutrientList() {
      return NutrientInfo.getLipids();
   }

   protected String getCategoryName() {
      return "Lipids";
  }
}
