/*
 * Created on 7-Aug-2005
 */
package ca.spaz.cron.ui;

import java.awt.event.*;
import java.io.File;

import javax.swing.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.actions.DeleteFoodAction;
import ca.spaz.cron.actions.ExportFoodAction;
import ca.spaz.cron.datasource.*;
import ca.spaz.cron.foods.Food;
import ca.spaz.util.ImageFactory;
import ca.spaz.util.ToolBox;

public class FoodDBToolBar extends JToolBar {
 
   private FoodProxy selectedFood;

   private JButton deleteButton;  
   private JButton editButton;
   private JButton exportButton;
   private JButton addButton;
   private JButton importButton;
   private JButton prefsButton;

   public FoodDBToolBar() {
      setFloatable(false);
      setRollover(true);      
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      setOpaque(false);
      setBorder(BorderFactory.createEmptyBorder(4,2,4,2)); 
      add(Box.createHorizontalStrut(4));
      add(getAddButton());
      add(Box.createHorizontalStrut(4));
      add(getImportButton());
      add(Box.createHorizontalGlue());
      add(Box.createHorizontalStrut(4));
      add(getEditButton());
      add(Box.createHorizontalStrut(4));
    //  addSeparator();
      add(Box.createHorizontalStrut(4));
      add(getExportButton());
      add(Box.createHorizontalStrut(4));
    //  addSeparator();
      add(Box.createHorizontalStrut(4));
      add(getDeleteButton());
      add(Box.createHorizontalStrut(4));
      /*add(Box.createHorizontalGlue());
      add(getPrefsButton());*/
      add(Box.createHorizontalStrut(4));

      setSelectedFood(null);
   }

   
   private JButton getEditButton() {
      if (null == editButton) {
         ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/edit.gif"));
         editButton = new JButton(icon);         
         fixButton(editButton);    
         editButton.setToolTipText("Edit Food");
         editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doEditFood();
            }
         });
      }
      return editButton;
   }
   
   private void doEditFood() {
     if (selectedFood != null) {
         FoodEditor.editFood(selectedFood.getFood());
      }
   }
   
   public static void fixButton(final JButton btn) {
      btn.setOpaque(false);
      btn.setFocusable(false);
      btn.setRolloverEnabled(true); 
      if (ToolBox.isMacOSX()) {
         btn.setBorderPainted(false);
         btn.setContentAreaFilled(false);
         btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
               if (btn.isEnabled()) {
                  btn.setContentAreaFilled(true);
                  btn.setOpaque(false);
                  btn.setBorderPainted(true);
               }
            } 
            public void mouseExited(MouseEvent e) {
               btn.setContentAreaFilled(false);
               btn.setBorderPainted(false);
            }
         });
      }
   }
    
   private JButton getDeleteButton() {
      if (null == deleteButton) {
         deleteButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/trash.gif")));
         deleteButton.setToolTipText("Delete Food");
         fixButton(deleteButton);
         deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               assert(getSelectedFood() != null);
               DeleteFoodAction.doDeleteFood(getSelectedFood(), deleteButton);
            }
         });
      }
      return deleteButton;
   }
   


   private JButton getImportButton() {
      if (null == importButton) {
         importButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/import.gif")));
         importButton.setToolTipText("Import Food");
         fixButton(importButton);
         importButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doImportFood();
            }
         });
      }
      return importButton;
   }

   public void doImportFood() {
      JFileChooser fd = new JFileChooser();
      if (fd.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
         File f = fd.getSelectedFile();
         if (f != null) {               
            importFood(f);            
         }            
      }
   }
   
   private void importFood(File f) {
      Food food = XMLFoodLoader.loadFood(f);
      if (food != null) {
         Datasources.getUserFoods().addFood(food);
      }      
   }

   private JButton getExportButton() {
      if (null == exportButton) {
         exportButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/export.gif")));
         exportButton.setToolTipText("Export Food");
         fixButton(exportButton);     
         exportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doExportFood();
            }
         });
      }
      return exportButton;
   }
   
   public void doExportFood() {
      assert(getSelectedFood() != null);
      ExportFoodAction.doExportFood(getSelectedFood(), this);
   }
   
  

   private JButton getAddButton() {
      if (null == addButton) {
         addButton = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/add.gif")));
         addButton.setToolTipText("Create New Food");
         fixButton(addButton);    
        // addButton.setVerticalTextPosition(JButton.BOTTOM);
       //  addButton.setHorizontalTextPosition(JButton.CENTER);  
         addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
               doAddNewFood();
            }
         });
      }
      return addButton;
   }

   public void doAddNewFood() {
      FoodEditor.editFood(new Food());
   }

   public void setSelectedFood(FoodProxy f) {
      selectedFood = f;
      getEditButton().setEnabled(f != null);
      getDeleteButton().setEnabled(f != null && f.getSource().isMutable());
      getExportButton().setEnabled(f != null);
   }

   public FoodProxy getSelectedFood() {
      return selectedFood;
   }
   
   
   private JButton getPrefsButton() {
      if (null == prefsButton) {
         ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/task.gif"));
         prefsButton = new JButton(icon);         
         fixButton(prefsButton);    
         prefsButton.setToolTipText("Edit Preferences");
         prefsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               CRONOMETER.getInstance().doEditUserSettings();
            }
         });
      }
      return prefsButton;
   }
   
}
