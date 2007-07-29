/*
 * Created on 7-Jan-2006
 */
package ca.spaz.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class WrapperDialog extends JDialog {
   private JPanel mainPanel;
   private JPanel outlinePanel;
   private JPanel titlePanel;
   private JPanel centerPanel;
   private JPanel navPanel;
   
   private boolean accepted = false;
   private JButton okButton, cancelButton;
   
   private JLabel logo;
   private WrappedPanel wrappedPanel;
   
   public WrapperDialog(Component parent, WrappedPanel wp) {
      super(JOptionPane.getFrameForComponent(parent));
      init(wp);
      setResizable(false);      
   } 
   
   /**
    * Construct an instance specifying the parent, a panel, and whether the dialog is resizeable.
    * @param parent
    * @param wp
    * @param resizeable
    */
   public WrapperDialog(Component parent, WrappedPanel wp, boolean resizeable) {
	   super(JOptionPane.getFrameForComponent(parent));
	   init(wp);
	   setResizable(resizeable);      
   }    
   
   private void init(WrappedPanel wp) {
      this.wrappedPanel = wp;
      setModal(true);
      setTitle(wp.getTitle());
      getContentPane().setLayout(new BorderLayout(4,4));
      getContentPane().add(getMainPanel(), BorderLayout.CENTER);      
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      pack();   
   }
  
   public static boolean showDialog(Component parent, WrappedPanel wp) {
      WrapperDialog wd = new WrapperDialog(parent, wp);
      wd.setLocationRelativeTo(parent);
      wd.setVisible(true);
      return wd.accepted;
   }

   /**
    * Show a dialog specifying the parent, a panel, and whether the dialog is resizeable.
    * @param parent
    * @param wp
    * @param isResizeable
    * @return
    */
   public static boolean showDialog(Component parent, WrappedPanel wp, boolean isResizeable) {
	   WrapperDialog wd = new WrapperDialog(parent, wp, isResizeable);
	   wd.setLocationRelativeTo(parent);
	   wd.setVisible(true);
	   return wd.accepted;
   }

   private JPanel getMainPanel() {
      if (mainPanel == null) {
         mainPanel = new JPanel(new BorderLayout(4,4));
         mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         if (getWrappedPanel().showSidebar()) {
            mainPanel.add(getOutlinePanel(), BorderLayout.WEST);
         }
         mainPanel.add(getCenterPanel(), BorderLayout.CENTER);
         mainPanel.add(getNavPanel(), BorderLayout.SOUTH);
      }
      return mainPanel;
   }
   
   private WrappedPanel getWrappedPanel() {
      return wrappedPanel;
   }
   
   private JPanel getCenterPanel() {
      if (centerPanel == null) {
         centerPanel = new JPanel(new BorderLayout(4,4));
         centerPanel.setBorder(BorderFactory.createEtchedBorder());
         if (getWrappedPanel().getSubtitle() != null) {
            centerPanel.add(getTitlePanel(), BorderLayout.NORTH);
         }
         centerPanel.add(getWrappedPanel(), BorderLayout.CENTER);
      }
      return centerPanel;
   }
    
   private JPanel getTitlePanel() {
      if (titlePanel == null) {                     
         JLabel titleLabel = new JLabel(getWrappedPanel().getSubtitle(), JLabel.CENTER);
         titleLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
         titleLabel.setForeground(Color.WHITE);
         
         titlePanel = new JPanel(new BorderLayout(4,4));
         titlePanel.setBackground(Color.GRAY);
         titlePanel.setBorder(BorderFactory.createEtchedBorder());
         titlePanel.add(titleLabel, BorderLayout.CENTER);        
      }
      return titlePanel;
   }
   
   private JPanel getOutlinePanel() {
      if (outlinePanel == null) {
         outlinePanel = new JPanel();
         outlinePanel.setLayout(new BorderLayout(8,8));
         outlinePanel.setBackground(Color.LIGHT_GRAY);
         outlinePanel.setBorder(BorderFactory.createCompoundBorder(
               BorderFactory.createEtchedBorder(),
               BorderFactory.createEmptyBorder(8,8,8,8) ));
         outlinePanel.add(getLogo(), BorderLayout.NORTH);
      }
      return outlinePanel;
   }
   
   private JLabel getLogo() {
      if (logo == null) {
         logo = new JLabel(
            "<html><div align=\"center\">"+
            getWrappedPanel().getInfoString()+"</div></html>",
            getWrappedPanel().getIcon(),
            JLabel.CENTER);
         logo.setVerticalTextPosition(JLabel.BOTTOM);
         logo.setHorizontalTextPosition(JLabel.CENTER);
         logo.setFont(logo.getFont().deriveFont(11.0f));
      }
      return logo;
   }
   

    
   public void dispose() { 
      wrappedPanel = null;
      super.dispose();
   }
       
   private JButton getCancelButton() {
      if (cancelButton == null) {
         cancelButton = new JButton("Cancel");
         cancelButton.setEnabled(true);
         cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               getWrappedPanel().doCancel(); 
               dispose();
            }
         });
      }
      return cancelButton;
   }   
   
   private JButton getOKButton() {
      if (okButton == null) {
         okButton = new JButton("OK");
         okButton.setEnabled(true);
         okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               accepted = getWrappedPanel().doAccept();
               if (accepted) {
                  dispose();
               }
            }
         });
      }
      return okButton;
   }   
   
   private JPanel getNavPanel() {
      if (navPanel == null) {
         navPanel = new JPanel();
         navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.X_AXIS));
         navPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         navPanel.add(Box.createHorizontalGlue());
         if (getWrappedPanel().isCancellable()) {
            navPanel.add(getCancelButton());
         }
         navPanel.add(Box.createHorizontalStrut(20));
         navPanel.add(getOKButton());
         navPanel.add(Box.createHorizontalStrut(10));
      }
      return navPanel;
   }
 
}
