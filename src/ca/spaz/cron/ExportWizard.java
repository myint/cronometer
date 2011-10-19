package ca.spaz.cron;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import sun.misc.BASE64Encoder;
import ca.spaz.cron.datasource.Datasources;
import ca.spaz.cron.datasource.FoodProxy;
import ca.spaz.cron.metrics.Metric;
import ca.spaz.cron.user.User;
import ca.spaz.cron.user.UserManager;
import ca.spaz.gui.ErrorReporter;
 
public class ExportWizard extends JFrame {
   
   private static String  hostname = "cronometer.com";
   private static int     port     = 80;
   private static boolean testing  = false;           // if true, data is not committed

   private JTextField      username = new JTextField(24);
   private JPasswordField  password = new JPasswordField(); 
   private JPanel          loginPanel;

   private String          token;
   private JPanel          panel;
   private JTextPane       text;
   private JButton         importBtn; 
   private JCheckBox       importDiary = new JCheckBox("Import Diary", true);
   
   public ExportWizard() {
      super("Export to cronometer.com");
      setIconImage(CRONOMETER.getWindowIcon());
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      
      text = new JTextPane();      
      
      panel = new JPanel(new BorderLayout(4,4));
      panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
      panel.setPreferredSize(new Dimension(600,300));
      
      importBtn = new JButton("Upload Data");
      importBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            importBtn.setEnabled(false);
            Thread t = new Thread(new Runnable() {
               public void run() {
                  doLogin();                  
               }
            });
            t.start();
         }
      });
         
      panel.add(new JScrollPane(text), BorderLayout.CENTER); 
      panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
      
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(getLoginPanel(), BorderLayout.SOUTH);
      getContentPane().add(panel, BorderLayout.CENTER);

      println("This will upload all of your custom foods & recipes, and all of your diary entries " +
      		  "except for exercises and biometrics other than weight. It will not import your nutritional targets.\n");
      println("To upload your data to cronometer.com, enter your cronometer.com username and password.\n");
      println("If you use facebook, google, or yahoo to login to cronometer.com, you must first set a " +
      		  "username and pasword under the Profile tab of cronometer.com.\n");
      println("Time Zone:\n\t " + TimeZone.getDefault().getID()); 
      println("Data:\n\t" + UserManager.getUserDirectory(UserManager.getCurrentUser()));
      println("");
      
      pack();
   }
 
   public JPanel getLoginPanel() {
      if (loginPanel == null) {
         loginPanel = new JPanel();
         loginPanel.setBorder(new EmptyBorder(8, 8, 18, 8));
         loginPanel.setLayout(new GridLayout(4,3,4,4));

         loginPanel.add(importDiary); 
         loginPanel.add(Box.createGlue()); 
         loginPanel.add(Box.createGlue());

         loginPanel.add(new JLabel("Username:", JLabel.RIGHT));
         loginPanel.add(username);
         loginPanel.add(Box.createGlue());
         
         loginPanel.add(new JLabel("Password:", JLabel.RIGHT));
         loginPanel.add(password);
         loginPanel.add(Box.createGlue());
         
         loginPanel.add(Box.createGlue());
         loginPanel.add(importBtn);        
         loginPanel.add(Box.createGlue()); 
      }
      return loginPanel;
   }  
 
   private String encodeCredentials() {
      try {
         return new BASE64Encoder().encode((username.getText() + '\n' + new String(password.getPassword())).getBytes("UTF-8"));
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      }
      return null;
   }
 
   private void doLogin() {
      try { 
         println("Logging in as '" + username.getText()+"'");
         URL url = new URL("http://"+hostname+"/migrate"); 
         URLConnection conn = url.openConnection();
         conn.setDoOutput(true);
         OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
         wr.write(URLEncoder.encode("login", "UTF-8") + "=" + encodeCredentials());
         wr.flush();
           
         BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         dbf.setNamespaceAware(true);
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc = db.parse(in);
         Element root = doc.getDocumentElement();        
         in.close();
        
         if (root.getAttribute("success").equals("false")) {
            println("Login Failed: " + getTextContent(root));
            JOptionPane.showMessageDialog(this, "Login Failed: " + getTextContent(root), "Login Failed", JOptionPane.ERROR_MESSAGE);
         } else {
            token = root.hasAttribute("token") ? root.getAttribute("token") : null;
            if (token != null) { 
               startImport();
            }
         }         
     } catch (Exception e) {
        ErrorReporter.showError(e, this);
        println(e.toString());
     } 
   }      
    
   public void startImport() {
      Thread t = new Thread(new Runnable() {
         public void run() {
            try {
               println("Loading data files...");
               String xmldata = getImportXML();
               println("Validating data..."); 
               System.out.println(xmldata);
               if (token != null) {
                  valdiate(xmldata);
                  if (xmldata != null) {
                     importData(xmldata, token);
                  } else {
                     println("Your data appears corrupted and cannot be imported.");
                     println("Email help@cronometer.com for assistance.");
                  }
               }
            } catch (Exception e) {
               e.printStackTrace();
               print(e.getMessage());
               ErrorReporter.showError(e, ExportWizard.this);
            }
         }   
      });     
      t.run();
   } 

   private void println(final String str) {
      print(str+"\n");
   }
   
   private void print(final String str) {
      System.out.print(str);
      if (text != null) {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               try {
                  text.getDocument().insertString(text.getDocument().getLength(), str, null);
               } catch (BadLocationException e) {
                  e.printStackTrace();
               }                  
            }
         });
      }
   }
   
   public void importData(String xml, String token) throws IOException {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            importBtn.setEnabled(false);
         }
      });
      
      println("Connecting to "+hostname+"...");
      Socket sock = new Socket(hostname, port);

      // Send header
      String path = "http://"+hostname+"/migrate?token="+token;
      OutputStreamWriter wr = new OutputStreamWriter(new BufferedOutputStream(sock.getOutputStream()), "UTF-8");
      wr.write("POST " + path + " HTTP/1.0\r\n");
      wr.write("Content-Length: " +xml.length() + "\r\n");
      wr.write("Content-Type: text/xml; charset=\"utf-8\"\r\n");
      wr.write("\r\n");

      println("Submitting data...");

      // Send data
      wr.write(xml);
      wr.flush();

      // Response
      StringBuffer sb = new StringBuffer();
      try {
         BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
         String line;
         while ((line = rd.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
         }
      } catch (IOException ex) {
         print(ex.getMessage());
      }
      println("\nFinished writing data.");
      
      parseResponse(sb.toString());

      wr.close(); 
   }
 
   private void parseResponse(String string) {
      if (testing) {
         println(string);
      }
      string = string.replaceFirst("(?s).*\\n\\n", "");
      try {         
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         dbf.setNamespaceAware(true);
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc = db.parse(new ByteArrayInputStream(string.getBytes()));
         Element root = doc.getDocumentElement();        
         handleResponse(root);
      } catch (Exception ex) {
         print(ex.getMessage());
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               importBtn.setEnabled(true);
            }
         });
      }
   }

   private void handleResponse(Element root) {
      if (root.getAttribute("success").equals("false")) {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               importBtn.setEnabled(true);
            }
         });            
         println("\nImport Failed: " + getTextContent(root));
         JOptionPane.showMessageDialog(this, "Import Failed: " + getTextContent(root), "Import Failed", JOptionPane.ERROR_MESSAGE);
      } else {
         JOptionPane.showMessageDialog(this, "Import Succeeded!");
         println("\nImport Succeeded!"); 
         dispose();
      }
   }
   
   public static String getTextContent(Node e) {
      StringBuffer sb = new StringBuffer();
      NodeList nl = e.getChildNodes();
      for (int n = 0; n<nl.getLength(); n++) {
         sb.append(nl.item(n).getTextContent());
      }
      return sb.toString();
   }
   

   public void valdiate(String xmldata) throws IOException {
      try {         
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         DocumentBuilder db = dbf.newDocumentBuilder();
         db.parse(new ByteArrayInputStream(xmldata.getBytes("UTF-8")));
      } catch (Exception e) {
         throw new IOException(e);
      } 
   }

   public String getImportXML() throws IOException {
      StringBuilder sb = new StringBuilder();

      User user = UserManager.getCurrentUser();
      println("Importing " + UserManager.getUserDirectory(user));

      // wrap it all in a root tag for importing
      sb.append(String.format("<import testing=\"%b\" timezone=\"%s\" version=\"%s\">", testing, TimeZone.getDefault().getID(), CRONOMETER.BUILD));
      
      List<FoodProxy> foods = Datasources.getUserFoods().getAllFoods();
      Collections.sort(foods, new Comparator<FoodProxy>() {
         public int compare(FoodProxy o1, FoodProxy o2) {
            return Integer.parseInt(o1.getSourceID()) - Integer.parseInt(o2.getSourceID());
         }
      });
      
      sb.append("<foods>");
      for (FoodProxy fp : foods) { 
         sb.append(fp.getFood().toXML().toString());
      }
      sb.append("</foods>");

      if (importDiary.isSelected()) { 
         if (user.getBiometricsHistory().getMetricsOfType("Weight").size() > 0) {
            String[] values = { "lbs", "kg", "Don't Import" };
            String weightUnit = (String)JOptionPane.showInputDialog(this, "In order to import your Weight measurements,\n we need to know what unit the data is in", "Weight Import", JOptionPane.QUESTION_MESSAGE, null, values, values[0]);
            if (!weightUnit.equals(values[2])) {
               Metric.WEIGHT_UNIT = weightUnit;
            }
         }
          
         sb.append(user.getFoodHistory().toXML());         
         sb.append(user.getBiometricsHistory().toXML());
         sb.append(user.getNotesHistory().toXML()); 
      }
      
      sb.append("</import>");
    
      return sb.toString();
   }
   
   public static void doExport() {
      ExportWizard ex = new ExportWizard();
      ex.setVisible(true);
   }

}
