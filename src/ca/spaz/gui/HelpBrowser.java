package ca.spaz.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.tree.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import ca.spaz.util.ToolBox;

/**
 * A Super Simple Help Browser
 * 
 * It uses a folder with HTML files and a simple XML Index.
 * 
 * @author Aaron Davidson
 */
public class HelpBrowser extends JFrame {

   private URL base;
   private String baseTitle;
   private JEditorPane htmlPane;
   private JTree contents;
   private JSplitPane splitPane;
   private JScrollPane contentsScrollPane;
   private Page contentsModel; 
   
   public HelpBrowser(String title, URL base) {
      this.baseTitle = title; 
      this.setTitle(baseTitle);
      this.base = base;
      this.getContentPane().setLayout(new BorderLayout());
      this.getContentPane().add(getSplitPane(), BorderLayout.CENTER);
      this.setPage("index.html");
      this.pack();
   }

   /**
    * Show window normally
    */
   public void showWindow() {
      setVisible(true); 
      toFront();
   }
   
   private JScrollPane makeHTMLScrollPane() {     
      JScrollPane jsp = new JScrollPane(getViewer());
      jsp.setBorder(new BevelBorder(BevelBorder.LOWERED)); 
      jsp.setPreferredSize(new Dimension(600,500));
      return jsp;
   }
   
   public JEditorPane getViewer() {
      if (htmlPane == null) {
         htmlPane = new JEditorPane();
         htmlPane.setPreferredSize(new Dimension(600,500));
         htmlPane.setContentType("text/html");
         htmlPane.setEditable(false);
         htmlPane.setBorder(new EmptyBorder(8, 8, 8, 8));
         htmlPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
               if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                  if (e instanceof HTMLFrameHyperlinkEvent) {
                     HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                     HTMLDocument doc = (HTMLDocument) htmlPane.getDocument();
                     doc.processHTMLFrameHyperlinkEvent(evt);
                  } else {
                     try {
                        if (e.getURL().getProtocol().equals("file")) {
                           showPage(e.getURL());
                           //htmlPane.setPage(e.getURL());
                        } else {                           
                           ToolBox.launchURL(htmlPane, e.getURL().toString()); 
                           // Java 6+ only:
                           // Desktop.getDesktop().browse(e.getURL().toURI());
                        }
                     } catch (Exception ex) {
                        ex.printStackTrace();
                     }
                  }
               }
            }
         });
      }
      return htmlPane;
   }
      
   private JSplitPane getSplitPane() {
      if (splitPane == null) {
         splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getContentsPanel(), makeHTMLScrollPane());
         splitPane.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));             
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
         contents.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION);
         contents.expandPath(new TreePath(getHelpContentsModel()));
         int row = 0;
         while (row < contents.getRowCount()) {
            contents.expandRow(row++);
         }
         contents.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
               setPage((Page)e.getPath().getLastPathComponent()); 
            }
         });          
      }
      return contents;
   }
    
   public void setPage(String name) { 
      try {
         URL f = new URL(base, name);
         getViewer().setContentType("text/html");
         getViewer().setPage(f); 
      } catch (IOException e) { 
         getViewer().setContentType("text/html");   
         getViewer().setText("<html><h3 align=\"center\">File Not Found: "+name+"</h3></html>");
         e.printStackTrace();
      } 
   }
   
   protected void setPage(Page page) {
      if (page != null) { 
         setTitle(baseTitle + " - " + page.getTitle());
         setPage(page.getUrl());      
      }
   }

   private InputStream getStream(String name) {
      InputStream in = null;
      try {
         in = (new URL(base, name)).openStream();         
      } catch (Exception e) {
         e.printStackTrace();
      }
      return in;
   }
   
   private Page getHelpContentsModel() {
      if (contentsModel == null) {
         try {
            InputStream in = getStream("help.xml");
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
   

   public void showPage(URL url) {
      Page page = findPage(url, getHelpContentsModel());
      if (page != null) {
         selectPageInTree(page);         
      }
   }
   
   public void showPage(String url) {
      Page page = findPage(url, getHelpContentsModel());
      if (page != null) {
         selectPageInTree(page);         
      }
   }
   
   private Page findPage(String url) {
      return findPage(url, getHelpContentsModel());
   }
   
   private Page findPage(String url, Page parent) {
      if (parent.getUrl().equals(url)) {
         return parent;
      }    
      for (int i=0; i<parent.getChildCount(); i++) {
         Page child = findPage(url, (Page)parent.getChildAt(i));
         if (child != null) {
            return child;
         }         
      }
      return null;
   }

   
   private Page findPage(URL url) {
      return findPage(url, getHelpContentsModel());
   }
   
   private Page findPage(URL url, Page parent) {
      if (url != null && parent.getURL().equals(url)) {
         return parent;
      }    
      for (int i=0; i<parent.getChildCount(); i++) {
         Page child = findPage(url, (Page)parent.getChildAt(i));
         if (child != null) {
            return child;
         }         
      }
      return null;
   }
   

   private void selectPageInTree(Page page) {
      TreePath path = new TreePath(page.getPath());
      getContents().expandPath(path);
      getContents().setSelectionPath(path);
      getContents().scrollPathToVisible(path); 
   }
    
   private class Page extends DefaultMutableTreeNode {
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
      public URL getURL() {          
         try {
            return new URL(base, url); 
         } catch (MalformedURLException e) {
            e.printStackTrace();
         }
         return null;
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
