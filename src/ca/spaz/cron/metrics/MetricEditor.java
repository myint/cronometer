/*
 * Created on 21-Mar-2005
 */
package ca.spaz.cron.metrics;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.*;

import ca.spaz.gui.*;

/**
 * This is panel that displays some brief summary information about a metric item
 * and allows choosing of a measure to add to the daily summary
 * 
 * @author davidson
 */
public class MetricEditor extends JPanel {  
    
   private Metric cur;

   private JLabel titleLabel;

   private JButton addButton;
   private ActionListener addAction;
   private DoubleField multiplier;   

   private JPanel addMeasurementPanel;
 
   private JPanel emptyPanel;
   private JPanel mainPanel;
   private CardLayout cards;
   private DecimalFormat labelFormatter;

   private Vector listeners = new Vector();
   
   public MetricEditor() {
      initialize();
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
         mainPanel.add(getTitleLabel(), BorderLayout.NORTH);
         mainPanel.add(getAddMeasurementPanel(), BorderLayout.SOUTH);
      } 
      return mainPanel;
   }

   private JPanel getEmptyPanel() {
      if (emptyPanel == null) {
         JLabel empty = new JLabel(
            "<html><h3 align=\"center\">" +
            "no biomarker selected</h3></html>",
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
   private JPanel getAddMeasurementPanel() {
      if (null == addMeasurementPanel) {
         addMeasurementPanel = new JPanel();
         addMeasurementPanel.setOpaque(false);
         addMeasurementPanel.setLayout(new BoxLayout(addMeasurementPanel, BoxLayout.X_AXIS));
         addMeasurementPanel.setBorder(BorderFactory.createEmptyBorder(3, 20, 3, 20));
         addMeasurementPanel.add(getValueField());
         addMeasurementPanel.add(Box.createHorizontalStrut(5));
         addMeasurementPanel.add(new JLabel("units"));
         addMeasurementPanel.add(Box.createHorizontalStrut(5));
         addMeasurementPanel.add(new JLabel(" at "));
         addMeasurementPanel.add(new JTextField(" 3:15 pm "));
         addMeasurementPanel.add(Box.createHorizontalStrut(50));
         addMeasurementPanel.add(getAddButton());
         addMeasurementPanel.add(Box.createGlue());         
      }
      return addMeasurementPanel;
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

   public DoubleField getValueField() {
      if (multiplier == null) {
         multiplier = new DoubleField(0,8);
      }
      return multiplier;
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
               fireMetricChosenEvent();               
            }
         };
      }
      return addAction;
   }

   public void setMetric(Metric metric) {
      cur = metric;
      if (metric == null) {
         cards.show(this, "EMPTY");
      } else {
//          getMeasure().setFood(f);
          getTitleLabel().setText(fixString(metric.getName()));
          getAddButton().setText("Add");
          cards.show(this, "MAIN");      
//         setFood(metric.getFood());
//         setWeight(metric.getMeasure(), metric.getAmount());
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
 
   public synchronized void addMetricEditorListener(MetricSelectionListener sel) {
      listeners.add(sel);
   }

   public synchronized void removeMetricEditorListener(MetricSelectionListener sel) {
      listeners.remove(sel);
   }

   private synchronized void fireMetricChosenEvent() {
      cur.setValue(getValueField().getText());
      for (int i=0; i<listeners.size(); i++) {
         MetricSelectionListener sel = (MetricSelectionListener)listeners.get(i);
         sel.metricChosen(cur);
      }
   }
  

}
