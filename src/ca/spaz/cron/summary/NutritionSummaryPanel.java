/*
 * Created on Apr 3, 2005 by davidson
 */
package ca.spaz.cron.summary;

import java.util.Date;
import java.util.List;

import javax.swing.JTabbedPane;

import ca.spaz.gui.WrapperDialog;
import ca.spaz.util.ToolBox;

/**
 * A comprehensive summary of a set of consumed foods
 * 
 * @todo: multi day mode  
 * 
 * @author davidson
 * @author Chris Rose
 */
public class NutritionSummaryPanel extends JTabbedPane {

    private TargetSummaryChart targetPanel;
    
    private MacroNutrientSummaryPanel generalPanel;

    private MineralSummaryPanel mineralPanel;

    private VitaminSummaryPanel vitaminPanel;

    private AminoAcidSummaryPanel aminoAcidPanel;
    
    private LipidSummaryPanel lipidsPanel;

    public NutritionSummaryPanel() {
      if (ToolBox.isMacOSX()) {
         setFont(getFont().deriveFont(10.0f));
      }
      add("Summary", getTargetSummaryPanel());
      add("General", getGeneralPanel());
      add("Vitamins", getVitaminsPanel());
      add("Minerals", getMineralsPanel());
      add("Amino Acids", getAminoAcidsPanel());
      add("Lipids", getLipidsPanel());
   }

    public void setServings(List consumed, boolean allSelected) {
        getGeneralPanel().update(consumed);        
        getMineralsPanel().update(consumed);  
        getVitaminsPanel().update(consumed);  
        getAminoAcidsPanel().update(consumed);       
        getLipidsPanel().update(consumed);
        getTargetSummaryPanel().update(consumed, allSelected);        
    }
    
    public void setExercises(List exercises) {
       getTargetSummaryPanel().updateExercises(exercises);
    }
    
    protected TargetSummaryChart getTargetSummaryPanel() {
       if (null == targetPanel) {
          targetPanel = new TargetSummaryChart(this);          
       }
       return targetPanel;
   }
    
    protected MacroNutrientSummaryPanel getGeneralPanel() {
        if (null == generalPanel) {
           generalPanel = new MacroNutrientSummaryPanel();
            //generalPanel = new NumericSummaryPanel();
        }
        return generalPanel;
    }

    protected VitaminSummaryPanel getVitaminsPanel() {
        if (null == vitaminPanel) {
            vitaminPanel = new VitaminSummaryPanel();            
        }
        return vitaminPanel;
    }

    protected MineralSummaryPanel getMineralsPanel() {
        if (null == mineralPanel) {
            mineralPanel = new MineralSummaryPanel();
        }
        return mineralPanel;
    }

    protected AminoAcidSummaryPanel getAminoAcidsPanel() {
        if (null == aminoAcidPanel) {
            aminoAcidPanel = new AminoAcidSummaryPanel();
        }
        return aminoAcidPanel;
    }

    protected LipidSummaryPanel getLipidsPanel() {
       if (null == lipidsPanel) {
          lipidsPanel = new LipidSummaryPanel();
      }
      return lipidsPanel;
    }

   public void generateReport(Date date) { 
      ReportWindow wp = new ReportWindow(this, date);
      WrapperDialog.showDialog(this, wp, true);
   }

}
