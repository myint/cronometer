/*
 * Created on 5-Jun-2005
 */
package ca.spaz.wizard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

import ca.spaz.util.ToolBox;

/**
 * A Wizard Dialog.
 * 
 * A Wizard contains one or more Wizard Panels
 * When a user clicks 'FINISH' on the last panel,
 * each Wizard Panel will be called in order, to 
 * commit it's settings. If the user clicks cancel,
 * nothing will be called.
 */
public class Wizard extends JDialog {
   private String title;
   private JPanel mainPanel;
   private JPanel outlinePanel;
   private JPanel titlePanel;
   private JPanel contentPanel;
   private JPanel navPanel;   
   private JLabel curPanelTitle;
   private CardLayout contentCards;
   private Vector wizardPanels;
   private JButton cancelBtn, nextBtn, prevBtn, finishBtn;
   private HashMap titleMap;
   
   private int curPanel = 0;
   
   public Wizard(JFrame parent, String title) {
      super(parent);
      this.title = title;
      setModal(true);
      setTitle(getTitle());
      getContentPane().setLayout(new BorderLayout(4,4));
      getContentPane().add(getMainPanel(), BorderLayout.CENTER);      
   }
   
   public void startWizard() {
      showPanel(0);
      pack();
      ToolBox.centerFrame(this);
      setVisible(true);
   }
   
   public void addWizardPanel(WizardPanel wp) {     
      getWizardPanels().add(wp);
      getWizardPanel().add(wp, wp.getWizardPanelTitle());
      JLabel wpLabel = makeOutlineLabel(wp);
      getTitleMap().put(wp, wpLabel);
      
      getOutlinePanel().add(wpLabel);
      getOutlinePanel().add(Box.createVerticalStrut(10));
   }   
   
   private HashMap getTitleMap() {
      if (titleMap == null) {
         titleMap = new HashMap();
      }
      return titleMap;
   }
   
   private JLabel makeOutlineLabel(WizardPanel wp) {     
      JLabel label = new JLabel(wp.getWizardPanelTitle());
      label.setForeground(Color.DARK_GRAY);
      label.setAlignmentX(0.5f);
      return label;
   }
   
   private WizardPanel getWizardPanel(int i) {
      return (WizardPanel)getWizardPanels().get(i);
   }
   
   private void showPanel(int i) {
      WizardPanel wp = getWizardPanel(curPanel);
      if (wp != null) {
         JLabel label = (JLabel)getTitleMap().get(wp);
         if (label != null) {
            label.setForeground(Color.DARK_GRAY);
            label.setFont(label.getFont().deriveFont(Font.PLAIN));
         }
      }

      wp = getWizardPanel(i);
      assert (wp != null);
      if (wp != null) {
         getContentCards().show(getWizardPanel(), wp.getWizardPanelTitle());
         getCurrentPanelTitleLabel().setText(wp.getWizardPanelTitle());
      }
      curPanel = i;
      getPrevButton().setEnabled(i>0);
      getNextButton().setEnabled(getWizardPanels().size() > i+1);
      getFinishButton().setEnabled(getWizardPanels().size() == i+1);
      JLabel label = (JLabel)getTitleMap().get(wp);
      if (label != null) {
         label.setForeground(Color.BLACK);
         label.setFont(label.getFont().deriveFont(Font.BOLD));
      }
   }
   
   private Vector getWizardPanels() {
      if (wizardPanels == null) {
         wizardPanels = new Vector();
      }
      return wizardPanels;
   }
   
   public int getNumPanels() {
      return getWizardPanels().size();
   }
   
   private void showNext() {
      if (curPanel < getNumPanels() - 1) {
         showPanel(curPanel+1);
      }
   }
   
   private void showPrevious() {
      if (curPanel > 0) {
         showPanel(curPanel-1);
      }
   }
   
   private JPanel getMainPanel() {
      if (mainPanel == null) {
         mainPanel = new JPanel(new BorderLayout(4,4));
         mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         mainPanel.add(getTitlePanel(), BorderLayout.NORTH);
         mainPanel.add(getOutlinePanel(), BorderLayout.WEST);
         mainPanel.add(getWizardPanel(), BorderLayout.CENTER);
         mainPanel.add(getNavPanel(), BorderLayout.SOUTH);
      }
      return mainPanel;
   }
   
   private JLabel getCurrentPanelTitleLabel() {
      if (curPanelTitle == null) {
         curPanelTitle = new JLabel("", JLabel.CENTER);
         curPanelTitle.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         curPanelTitle.setFont(curPanelTitle.getFont().deriveFont(Font.BOLD));
         curPanelTitle.setForeground(Color.WHITE);
      }
      return curPanelTitle;
   }
   
   private JPanel getTitlePanel() {
      if (titlePanel == null) {
         
         JLabel titleLabel = new JLabel(getTitle());
         titleLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
         titleLabel.setForeground(Color.WHITE);
         
         titlePanel = new JPanel(new BorderLayout(4,4));
         titlePanel.setBackground(Color.BLACK);
         titlePanel.setBorder(BorderFactory.createRaisedBevelBorder());
         titlePanel.add(titleLabel, BorderLayout.WEST);
         titlePanel.add(getCurrentPanelTitleLabel(), BorderLayout.CENTER);
      }
      return titlePanel;
   }
   
   private JPanel getOutlinePanel() {
      if (outlinePanel == null) {
         outlinePanel = new JPanel();
         outlinePanel.setLayout(new BoxLayout(outlinePanel, BoxLayout.Y_AXIS));
         outlinePanel.setBackground(Color.LIGHT_GRAY);
         outlinePanel.setBorder(BorderFactory.createCompoundBorder(
               BorderFactory.createEtchedBorder(),
               BorderFactory.createEmptyBorder(8,8,8,8) ));
         outlinePanel.add(Box.createRigidArea(new Dimension(120,10)), BorderLayout.CENTER);
      }
      return outlinePanel;
   }
   
   private JPanel getWizardPanel() {
      if (contentPanel == null) {
         contentPanel = new JPanel(getContentCards());
         contentPanel.setBorder(BorderFactory.createLoweredBevelBorder());
         contentPanel.add(Box.createRigidArea(new Dimension(400,300)), BorderLayout.CENTER);
      }
      return contentPanel;
   }   
   
   private CardLayout getContentCards() {
      if (contentCards == null) {
         contentCards = new CardLayout();
      }
      return contentCards;
   }
   
   private JPanel getNavPanel() {
      if (navPanel == null) {
         navPanel = new JPanel();
         navPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.X_AXIS));
         navPanel.add(getCancelButton());
         navPanel.add(Box.createGlue());
         navPanel.add(Box.createHorizontalStrut(10));
         navPanel.add(getPrevButton());
         navPanel.add(Box.createHorizontalStrut(10));
         navPanel.add(getNextButton());
         navPanel.add(Box.createHorizontalStrut(40));
         navPanel.add(getFinishButton());
      }
      return navPanel;
   }

   public String getTitle() {
      return title;
   }
   
   public void doCancel() {
      dispose();
   }
   
   public void doFinish() {
      Iterator iter = getWizardPanels().iterator();
      while (iter.hasNext()) {
         ((WizardPanel)iter.next()).commitChanges();
      }
      dispose();
   }
   
   private JButton getCancelButton() {
      if (cancelBtn == null) {
         cancelBtn = new JButton("Cancel");
         cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doCancel();
            }            
         });
      }
      return cancelBtn;
   }
   
   private JButton getNextButton() {
      if (nextBtn == null) {
         nextBtn = new JButton("Next");
         nextBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (getWizardPanel(curPanel).isValid()) {
                  showNext();
               }
            }            
         });
      }
      return nextBtn;
   }
   
   private JButton getPrevButton() {
      if (prevBtn == null) {
         prevBtn = new JButton("Back");
         prevBtn.setEnabled(false);
         prevBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               showPrevious(); 
            }  
         });
      }
      return prevBtn;
   }
   
   private JButton getFinishButton() {
      if (finishBtn == null) {
         finishBtn = new JButton("Finish");
         finishBtn.setEnabled(false);
         finishBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (getWizardPanel(curPanel).isValid()) {
                  doFinish();
               }
            }
         });
      }
      return finishBtn;
   }
}
