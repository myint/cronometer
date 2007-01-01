/*******************************************************************************
 * ******************************************************************************
 * Copyright (c) 2005 Chris Rose and AIMedia All rights reserved.
 * USDAImportDialog and the accompanying materials are made available under the
 * terms of the Common Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/cpl-v10.html Contributors:
 * Chris Rose
 ******************************************************************************/
package ca.spaz.cron.ui;

import java.awt.*;
import java.awt.event.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.SoftBevelBorder;

import se.datadosen.component.RiverLayout;
import ca.spaz.cron.datasource.USDAImport.USDAImporter;
import ca.spaz.task.*;
import ca.spaz.util.Logger;

/**
 * This dialog encapsulates the behaviour involved in downloading and installing
 * the USDA NR17 food info database.
 * 
 * @author Chris Rose
 */
public class USDAImportDialog extends JDialog implements TaskListener {
   private static final String TITLE = "USDA sr17 Importer";

   /**
    * This action is fired to start the downloading process.
    * 
    * @author Chris Rose
    */
   public class ImportAction extends AbstractAction {
 
      /**
       * Construct a new ImportAction instance.
       */
      public ImportAction() {
         super("Import");
         putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
         putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I,
               InputEvent.CTRL_DOWN_MASK));
      }

      /*
       * (non-Javadoc)
       * 
       * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
       */
      public void actionPerformed(ActionEvent e) {          
         USDAImporter worker = new USDAImporter(null);
         worker.setSourceURL(getFoodSourceURL());
         getTaskBar().executeTask(worker);
         /*
         worker.addFinishListener(owner);
         getNextButton().setAction(getFinishAction());
         getFinishAction().setEnabled(false);
         worker.start();*/         
      }

      /**
       * @return
       */
      private URL getFoodSourceURL() {
         URL url = null;
         try {
            url = new URL("http://www.nal.usda.gov/fnic/foodcomp/Data/SR17/dnload/sr17.zip");
         } catch (MalformedURLException e) {
            Logger.error("getFoodSourceURL()", e);
         }
         return url;
      }

   }

   /**
    * This action is to be fired when moving through the dialog.
    * 
    * @author Chris Rose
    */
   public class NextAction extends AbstractAction {
 
      /**
       * Construct a new instance of NextAction
       */
      public NextAction() {
         super("Next");
         putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
         putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N,
               InputEvent.CTRL_DOWN_MASK));
      }

      /*
       * (non-Javadoc)
       * 
       * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
       */
      public void actionPerformed(ActionEvent e) {
         getNextButton().setAction(getImportAction());
         getJContentPane().remove(getExplanation());
         getJContentPane().add(getProgressPanel(), BorderLayout.CENTER);
      }

   }

   /**
    * This action is to be triggered when the import process is complete.
    * 
    * @author Chris Rose
    */
   public class FinishAction extends AbstractAction {

      /**
       * Construct a new FinishAction instance.
       */
      public FinishAction() {
         super("Finish");
         putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
         putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F,
               InputEvent.CTRL_DOWN_MASK));
      }

      /*
       * (non-Javadoc)
       * 
       * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
       */
      public void actionPerformed(ActionEvent e) {
         doCancel();
      }

   }

   private javax.swing.JPanel jContentPane = null;

   private JPanel controlPanel;

   private JTextArea progressField;

   private JButton cancelButton;

   private Action cancelAction;

   private JPanel explanation;

   private static final String EXPLANATION_TEXT = "<html>The next part of this dialog will download<br>"
         + "the USDA food database to your local machine.<br>"
         + "This takes a bit of time, but when it is done you<br>"
         + "will have a large collection of food to choose<br>"
         + "your daily diet from.<br>"
         + "<br>"
         + "Please, press \"Next\" to continue...</html>";

   private JButton advanceButton;

   private Action nextAction;

   private Action importAction;

   private Action finishAction;

   private JPanel progressPanel;

   private TaskBar taskBar;

   /**
    * This is the default constructor
    */
   public USDAImportDialog(Frame owner) {
      super(owner);
      initialize();
   }

   /**
    * This method initializes this
    * 
    * @return void
    */
   private void initialize() {
      this.setSize(400, 300);
      this.setTitle(TITLE);
      this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      this.setContentPane(getJContentPane());
   }

   /**
    * This method initializes jContentPane
    * 
    * @return javax.swing.JPanel
    */
   private javax.swing.JPanel getJContentPane() {
      if (jContentPane == null) {
         jContentPane = new javax.swing.JPanel();
         jContentPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
         jContentPane.setLayout(new java.awt.BorderLayout());
         jContentPane.add(getExplanation(), BorderLayout.CENTER);
         jContentPane.add(getControlPanel(), BorderLayout.SOUTH);
      }
      return jContentPane;
   }

   /**
    * @return
    */
   private JPanel getControlPanel() {
      if (null == controlPanel) {
         controlPanel = new JPanel(new GridLayout());
         controlPanel.setBorder(BorderFactory.createEmptyBorder(14,4,4,4));
         GridBagConstraints gc = new GridBagConstraints();
         gc.gridx = 0;
         gc.gridy = 0;
         gc.anchor = GridBagConstraints.WEST;
         controlPanel.add(getCancelButton(), gc);
         gc.gridx = 1;
         gc.fill = GridBagConstraints.BOTH;
         controlPanel.add(Box.createHorizontalGlue(), gc);
         gc.gridx = 2;
         gc.fill = GridBagConstraints.NONE;
         gc.anchor = GridBagConstraints.EAST;
         controlPanel.add(getNextButton(), gc);
      }
      return controlPanel;
   }

   /**
    * @return
    */
   private JButton getNextButton() {
      if (null == advanceButton) {
         advanceButton = new JButton();
         advanceButton.setAction(getNextAction());
      }
      return advanceButton;
   }

   /**
    * @return
    */
   private Action getNextAction() {
      if (null == nextAction) {
         nextAction = new NextAction();
      }
      return nextAction;
   }

   private Action getFinishAction() {
      if (null == finishAction) {
         finishAction = new FinishAction();
      }
      return finishAction;
   }

   /**
    * @return
    */
   private JButton getCancelButton() {
      if (null == cancelButton) {
         cancelButton = new JButton("Cancel");
         cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doCancel();
            }            
         });
      }
      return cancelButton;
   }
 
   private Action getImportAction() {
      if (null == importAction) {
         importAction = new ImportAction();
      }
      return importAction;
   }

   private JPanel getExplanation() {
      if (null == explanation) {
         explanation = new JPanel();
         explanation.setLayout(new RiverLayout());
         explanation.add(new JLabel(EXPLANATION_TEXT));
      }
      return explanation;
   }

   /**
    * @return
    */
   private JTextArea getProgressField() {
      if (null == progressField) {
         progressField = new JTextArea();
      }
      return progressField;
   }

   /**
    * @return
    */
   private JPanel getProgressPanel() {
      if (null == progressPanel) {
         progressPanel = new JPanel();
         progressPanel.setLayout(new BorderLayout(4,4));
         JScrollPane jsp = new JScrollPane(getProgressField());
         jsp.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
         progressPanel.add(jsp, BorderLayout.CENTER);
         progressPanel.add(getTaskBar(), BorderLayout.SOUTH);
      }
      return progressPanel;
   }
   
   private TaskBar getTaskBar() {
      if (taskBar == null) {
         taskBar = new TaskBar();
         taskBar.addTaskListener(this);
      }
      return taskBar;
   }
   

   /*
    * (non-Javadoc)
    * 
    * @see com.aimedia.ui.ICancellable#doCancel()
    */
   public void doCancel() {
      this.setVisible(false);
      this.dispose();
   }
   
   /* (non-Javadoc)
    * @see ca.spaz.task.TaskListener#taskStarted(ca.spaz.task.Task)
    */
   public void taskStarted(Task t) {
      getFinishAction().setEnabled(false);
      getImportAction().setEnabled(false);
      getCancelButton().setEnabled(false);
      getNextButton().setAction(getFinishAction());
   }

   /* (non-Javadoc)
    * @see ca.spaz.task.TaskListener#taskFinished(ca.spaz.task.Task)
    */
   public void taskFinished(Task t) {
      getFinishAction().setEnabled(true);
   }

   /* (non-Javadoc)
    * @see ca.spaz.task.TaskListener#taskAborted(ca.spaz.task.Task)
    */
   public void taskAborted(Task t) {
      getCancelButton().setEnabled(true);
   }

}
