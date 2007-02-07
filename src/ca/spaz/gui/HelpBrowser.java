package ca.spaz.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class HelpBrowser extends JFrame {

   private WebViewer viewer;
   private File base;
   private JTree contents;
   private JSplitPane splitPane;
   private JScrollPane contentsScrollPane;
   private Page contentsModel;
   
   public HelpBrowser(String title, File base) {
      this.setTitle(title);
      this.base = base;
      setLayout(new BorderLayout());      
      add(getSplitPane(), BorderLayout.CENTER); 
      setPage("index.html");
      pack();
      setVisible(true);
   }
   
   public void setPage(String name) {
      getViewer().setHTML(new File(base, name));
   }
   
   public WebViewer getViewer() {
      if (viewer == null) {
         viewer = new WebViewer();
         viewer.setPreferredSize(new Dimension(600,500));
      }
      return viewer;
   }
   
   
   private JSplitPane getSplitPane() {
      if (splitPane == null) {
         splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getContentsPanel(), getViewer());
      }
      return splitPane;
   }
   

   private JScrollPane getContentsPanel() {
      if (contentsScrollPane == null) {
         contentsScrollPane = new JScrollPane(getContents());
         contentsScrollPane.setPreferredSize(new Dimension(200,500));

      }
      return contentsScrollPane;
   }

   private JTree getContents() {
      if (contents == null) {
         contents = new JTree(getHelpContentsModel());
         contents.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
      }
      return contents;
   }
   
   private Page getHelpContentsModel() {
      if (contentsModel == null) {
         try {
            FileInputStream in = new FileInputStream(new File(base, "help.xml"));
            contentsModel = loadContents(in);
            in.close();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      return contentsModel;
   }

   private Page loadContents(InputStream in) throws ParserConfigurationException, SAXException, IOException {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document d = db.parse(in);
      Element e = d.getDocumentElement();
      return new Page(e);
  }
   
   
   protected class Page extends DefaultMutableTreeNode {
      private String title;
      private String url;
   
      public Page(Element e) {
         super();
         load(e);
      }   
      public Page(String title, String url) {
         super();
         this.title = title;
         this.url = url;
      }
      public String getTitle() {
         return title;
      }
      public void setTitle(String title) {
         this.title = title;
      }
      public String getUrl() {
         return url;
      }
      public void setUrl(String url) {
         this.url = url;
      }     
      public String toString() {
         return title;
      }
      public void load(Element e){
         title = e.getAttribute("title");
         url = e.getAttribute("url");
         NodeList nl = e.getChildNodes();
         for (int i=0; i<nl.getLength(); i++) {
            if (nl.item(i).getNodeType()== Node.ELEMENT_NODE) {
               if (((Element)nl.item(i)).getTagName().equals("page")) {
                  add(new Page((Element)nl.item(i)));
               }
            }
         }
      }
   }

   
   
}
