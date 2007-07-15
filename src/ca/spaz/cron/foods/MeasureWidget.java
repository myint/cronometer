/*
 * Created on Apr 9, 2005 by davidson
 */
package ca.spaz.cron.foods;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ca.spaz.cron.ui.SearchPanel;
import ca.spaz.gui.DoubleField;

/**
 * A measure widget displays the UI for choosing an amount of a food item, based
 * on various Measures stored for the particular food being weighed.
 * 
 * @author davidson
 */
public class MeasureWidget extends JPanel implements ItemListener, ActionListener, KeyListener {
   // initial weight by default
   private Measure curMeasure = Measure.GRAM;
   
   // list of weights for selected food
   private JComboBox measures = new JComboBox();
   
   // multiplier (amount) of currently showing weight
   private DoubleField multiplier = new DoubleField(0,8);
   
   // action listeners
   private Vector listeners = new Vector();
   private Vector actionListeners = new Vector();
   
   // determines if multiplier value is linked to the weight
   private boolean linked = false;
   
   /**
    * Construct a new measure widget
    */
   public MeasureWidget() {
      multiplier.setRange(0,1000000);
      multiplier.setValue(1);
      
      measures.setOpaque(false);
      measures.addItemListener(this);
      multiplier.addActionListener(this);
      multiplier.addKeyListener(this);
      
      setOpaque(false);
      setLayout(new BorderLayout(4,4));
      add(multiplier, BorderLayout.WEST);
      add(measures, BorderLayout.CENTER);
   }

   /**
    * Set the measure to display weights for a particular food.
    */
   public void setFood(Food f) {
      measures.setModel(new DefaultComboBoxModel(f.getMeasures().toArray()));
      curMeasure = Measure.GRAM;
      fireChangeEvent();
   }

   /**
    * Set the weight to display
    * @param w the type of measure
    * @param mult the multiplier for that measure
    */
   public void setMeasure(Measure w, double mult) {
     // measures.setSelectedItem(w);
      for (int i=0; i<measures.getItemCount(); i++) {
         Measure m = (Measure)measures.getItemAt(i);
         if (m.equals(w)) {
            measures.setSelectedIndex(i);
            break;
         }
      }
      multiplier.setValue(mult);
      fireChangeEvent();
   }

   /**
    * Update the widget after a weight change has occurred.
    * Called when weight menu is changed or edited
    */
   public void updateMeasure() {
      Measure w = getSelectedMeasure();     
      if (linked) {
         double mult = w.getAmount();
         if (curMeasure != null) {
            double grams = curMeasure.getGrams() * multiplier.getValue();
            mult = grams/w.getGrams();
         }
         multiplier.setValue(mult);
      }
      setFocus();
      curMeasure = w;
      fireChangeEvent();
   }      

   /**
    * Get the multiplier for the current weight and mult settings.
    * @return a multiple of the standard nutrient value (x per 100g)
    */
   public double getMultiplier() {
      return getGrams() / 100.0;
   }

   /**
    * Get the number of grams in this measure
    * @return the number of grams in this state
    */
   public double getGrams() {
      Measure w = getSelectedMeasure();
      if (w != null) {
         return multiplier.getValue() * w.getGrams();// * (1.0/w.getAmount()); 
      }
      return 0;
   }
   
   public Measure getSelectedMeasure() {
      return (Measure)measures.getSelectedItem();
   }    
   
   public void addChangeListener(ChangeListener l) {
      listeners.add(l);
   }

   public void addRemoveListener(ChangeListener l) {
      listeners.remove(l);
   }
   
   
   public void addActionListener(ActionListener l) {
      actionListeners.add(l);
   }

   public void removeActionListener(ActionListener l) {
      actionListeners.remove(l);
   }
   
   public void fireChangeEvent() {
      ChangeEvent ce = new ChangeEvent(this);
      Iterator iter = listeners.iterator();
      while (iter.hasNext()) {
         ((ChangeListener)iter.next()).stateChanged(ce);
      }
   }
   
   public void fireActionEvent() {
      ActionEvent ae = new ActionEvent(this, 0, null);
      Iterator iter = actionListeners.iterator();
      while (iter.hasNext()) {
         ((ActionListener)iter.next()).actionPerformed(ae);
      }
   }


   /**
    * Called if the weight menu state is changed
    */
   public void itemStateChanged(ItemEvent e) {
      updateMeasure();
   }
   
   /**
    * Called when the text field is set
    */
   public void actionPerformed(ActionEvent e) {
      fireChangeEvent();
      fireActionEvent();
      //Toolkit.getDefaultToolkit().beep();
   }

   public void keyTyped(KeyEvent e) {
      //System.out.println("keyTyped: " + e);
   }

   public void keyPressed(KeyEvent e) { 
      if (searchPanel != null) {
         if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_KP_UP) {
            searchPanel.arrowUp();
         }
         if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_KP_DOWN) {
            searchPanel.arrowDown();
         }
      }
   }

   /**
    * Called when the text field is edited
    */
   public void keyReleased(KeyEvent e) {
      //System.out.println("keyReleased: " + e);
      fireChangeEvent();
   }



   private SearchPanel searchPanel = null;
   
   public void linkToSearchResults(SearchPanel sp) { 
      this.searchPanel = sp;      
   }
  
   
   /**
    * If linked, changing a weight will update the multiplier
    * to keep the grams constant. Otherwise, the multiplier is left unchanged.
    * 
    * @return true if the multiplier is linked to the weight
    */
   public boolean isLinked() {
      return linked;
   }


   /**
    * If linked, changing a weight will update the multiplier
    * to keep the grams constant. Otherwise, the multiplier 
    * is left unchanged.
    */
   public void setLinked(boolean linked) {
      this.linked = linked;
   }

   public void setFocus() {
      multiplier.requestFocus();
      multiplier.requestFocusInWindow();
      multiplier.selectAll();
   }
}
