package ca.spaz.cron.ui;

import ca.spaz.cron.user.Biomarker;

public interface BiomarkerSelectionListener {
   public void biomarkerSelected(Biomarker biomarker);
   public void biomarkerDoubleClicked(Biomarker biomarker);
   public void biomarkerChosen(Biomarker biomarker);
}
