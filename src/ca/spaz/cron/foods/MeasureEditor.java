/*
 * Created on Apr 13, 2005 by davidson
 */
package ca.spaz.cron.foods;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.gui.PrettyTable;
import ca.spaz.gui.PrettyTableModel;
import ca.spaz.util.ImageFactory;

/**
 * A class for editing the set of weights for a food item
 * 
 * @author davidson
 */
public class MeasureEditor extends JPanel {

    private WeightTableModel model;

    private JTable weightTable;

    private Food food;

    private JButton delBtn;

    private JButton addBtn;

    private JScrollPane measureScrollPane;

    private JToolBar toolBar;
    
    private boolean dirty = false;
    
    private Vector listeners;

    public MeasureEditor(Food f) {
       this.food = f;
       resetMeasures();
       initialize();
   }

    protected void resetMeasures() {
       getWeightTableModel().getWeights().clear();
       getWeightTableModel().getWeights().addAll(food.getMeasures());
       getWeightTableModel().getWeights().remove(Measure.GRAM);       
       getWeightTableModel().fireTableDataChanged();
    }
    
    public void addChangeListener(ChangeListener cl) {
       getListeners().add(cl);
    }
    
    public void removeChangeListener(ChangeListener cl) {
       getListeners().remove(cl);
    }
    
    private Vector getListeners() {
       if (listeners == null) {
          listeners = new Vector();
       }
       return listeners;
    }
    

    public void fireChangeEvent() {
       ChangeEvent ce = new ChangeEvent(this);
       Iterator iter = listeners.iterator();
       while (iter.hasNext()) {
          ((ChangeListener)iter.next()).stateChanged(ce);
       }
    }

    /**
     * @param parent
     */
    private void initialize() {
        this.setLayout(new BorderLayout(4, 4));
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.add(getToolBar(), BorderLayout.WEST);
        this.add(getWeightScrollTable(), BorderLayout.CENTER); 
    }

    
    public List getMeasures() {
       List measures = new ArrayList();
       measures.add(Measure.GRAM);
       measures.addAll(getWeightTableModel().getWeights());
       return measures;
    }
    
    /**
     * @return
     */
    private JScrollPane getWeightScrollTable() {
        if (null == measureScrollPane) {
            measureScrollPane = new JScrollPane();
            measureScrollPane.setViewportView(getWeightTable());
            measureScrollPane.getViewport().setBackground(Color.WHITE);
            measureScrollPane.setPreferredSize(new Dimension(275, 100));
        }
        return measureScrollPane;
    }

    /**
     * @todo add reset measures button
     * @return the toolbar.
     */
    private JToolBar getToolBar() {
        if (null == toolBar) {
            toolBar = new JToolBar();
            toolBar.setRollover(true);
            toolBar.setOrientation(JToolBar.VERTICAL);
            toolBar.setFloatable(false);
            toolBar.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
            toolBar.add(getAddButton());
            toolBar.add(getDeleteButton());
            toolBar.add(Box.createGlue());
        }
        return toolBar;
    }

    private JButton getAddButton() {
        if (null == addBtn) {
            ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/add.gif"));
            addBtn = new JButton(icon);
            addBtn.setToolTipText("Add a new measurement.");
            addBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addMeasure();
                }
            });
            CRONOMETER.fixButton(addBtn);
        }
        return addBtn;
    }

    private JButton getDeleteButton() {
        if (null == delBtn) {
            ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/trash.gif"));
            delBtn = new JButton(icon);
            delBtn.setToolTipText("Delete the selected measurement.");
            delBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteSelectedWeight();
                }
            });
            CRONOMETER.fixButton(delBtn);
        }
        return delBtn;
    }

    private void addMeasure() {
        model.getWeights().add(new Measure(1.0, "Serving", 1.0));
        model.fireTableDataChanged();
        int n = model.getWeights().size() - 1;
        weightTable.getSelectionModel().setSelectionInterval(n, n);
        setDirty(true);
    }

    private void deleteSelectedWeight() {
        int row = weightTable.getSelectedRow();
        if (row >= 0) {
           model.getWeights().remove(row);
           model.fireTableDataChanged();
           setDirty(true);
        }
    }

    private void setDirty(boolean val) {
        dirty = val;
        if (dirty) {
           fireChangeEvent();
        }
    }

    private boolean isDirty() {
        return dirty;
    }

    private JComponent getWeightTable() {
        //      model = new WeightTableModel(curWeights);
        if (null == weightTable) {

            weightTable = new PrettyTable(getWeightTableModel()); 
            weightTable.getSelectionModel().setSelectionMode(
                    ListSelectionModel.SINGLE_SELECTION);
            weightTable
                    .setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
            weightTable.getTableHeader().setReorderingAllowed(false);
            weightTable.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                        public void valueChanged(ListSelectionEvent e) {
                            if (e.getValueIsAdjusting())
                                return;
                            ListSelectionModel lsm = (ListSelectionModel) e
                                    .getSource();
                            if (!lsm.isSelectionEmpty()) {
                                getDeleteButton().setEnabled(true);
                            } else {
                                getDeleteButton().setEnabled(false);
                            }
                        }
                    });
            weightTable.getColumnModel().getColumn(0).setMaxWidth(60);
            weightTable.getColumnModel().getColumn(2).setMaxWidth(80);
        }
        return weightTable;
    }

    private WeightTableModel getWeightTableModel() {
        if (null == model) {
           model = new WeightTableModel();
        }
        return model;
    }

    public class WeightTableModel extends PrettyTableModel {
        List weights = new ArrayList();
        
        private String[] columnNames = { "Amount", "Measure", "Grams" };

        public String getColumnName(int col) {
            return columnNames[col].toString();
        }

        public int getRowCount() {
            return weights.size();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public List getWeights() {
           return weights;
        }
        
        public Measure getWeight(int i) {
           if (weights.size() > i) {
              return (Measure) weights.get(i);
           }
           return null;
        }

        public Object getValueAt(int row, int col) {
            Measure w = getWeight(row);
            if (w != null) {
                switch (col) {
                case 0:
                    return new Double(w.getAmount());
                case 1:
                    return w.getDescription();
                case 2:
                    return new Double(w.getGrams());
                }
            }
            return "";
        }

        public Class getColumnClass(int col) {
            Object o = getValueAt(0, col);
            if (o != null) {
                return o.getClass();
            }
            return String.class;
        }

        public boolean isCellEditable(int row, int col) {
            if (getWeight(row) == Measure.GRAM)
                return false;
            return (true);
        }

        public void setValueAt(Object value, int row, int col) {
            Measure w = getWeight(row);
            if (w != null) {
                if (col == 0) {
                   double val = ((Double) value).doubleValue();
                   if (val > 0) {
                      w.setAmount(val);
                   } else {
                      JOptionPane.showMessageDialog(getWeightTable(), 
                            "An amount cannot be zero!", 
                            "Error!", JOptionPane.ERROR_MESSAGE);
                   }
                } else if (col == 1) {
                    w.setDescription((String) value);
                } else if (col == 2) {
                   double val = ((Double) value).doubleValue();
                   if (val > 0) {
                      w.setGrams(val);
                   } else {
                      JOptionPane.showMessageDialog(getWeightTable(), 
                            "A measure cannot be zero grams!", 
                            "Error!", JOptionPane.ERROR_MESSAGE);
                   }
                }
            }
            setDirty(true);
            fireTableCellUpdated(row, col);
        }

      public String getToolTipText(int r, int c) {          
         return null;
      }

      public void sort() { }

    }
}
