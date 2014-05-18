package ca.spaz.cron.foods;


public interface ServingSelectionListener {
    public void servingSelected(Serving s);
    public void servingDoubleClicked(Serving s);
    public void servingChosen(Serving s);
}
