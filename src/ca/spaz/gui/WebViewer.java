package ca.spaz.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.net.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
 
/**
 * @author Aaron Davidson
 */
public class WebViewer extends JPanel {
   private JEditorPane htmlPane = new JEditorPane();
   private JScrollPane jsp;
   private boolean external = false;
   
   public WebViewer(URL url) {      
      jsp = makeHTMLPane();
      setHTML(url);
      this.setLayout(new BorderLayout());
      this.add(jsp, BorderLayout.CENTER);
   }
   
   public JScrollPane getScrollPane() {
      return jsp;
   }
   
   public void setPreferredSize(Dimension d) {
      super.setPreferredSize(d);
      if (jsp != null) {
         jsp.setPreferredSize(d);
         jsp.setMinimumSize(d);
      }
   }
   
   public void setScrollbarPolicies(int horizPolicy, int vertPolicy) {
      jsp.setHorizontalScrollBarPolicy(horizPolicy);
      jsp.setVerticalScrollBarPolicy(vertPolicy);
   }
   
   public WebViewer(String html) {
      jsp = makeHTMLPane();
      setHTML(html);
      this.setLayout(new BorderLayout());
      this.add(jsp, BorderLayout.CENTER);
   }
   
   public WebViewer() {
      jsp = makeHTMLPane();
      this.setLayout(new BorderLayout());
      this.add(jsp, BorderLayout.CENTER);
   }
    
 
   private JScrollPane makeHTMLPane() {
      htmlPane.setContentType("text/html");
      htmlPane.setEditable(false);
      htmlPane.setBorder(new EmptyBorder(4, 4, 4, 4));
      htmlPane.addHyperlinkListener(new Hyperactive());
      JScrollPane htmlView = new JScrollPane(htmlPane);
      htmlView.setBorder(new BevelBorder(BevelBorder.LOWERED));
      //htmlView.setPreferredSize(new Dimension(50, 50));
      return htmlView;
   }
   
   public void setURL(String url) {
      try {
         setHTML(new URL(url));
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }
   }
   
   public void setHTML(final URL url) {
      Runnable proc = new Runnable() {
         public void run() {
            try {         
               htmlPane.setPage(url);               
            } catch (UnknownHostException ex) {
               setHTML( "<html><body>" +
                        "<h2 align=\"center\">Unknown Host Exception:</h2>" +
                        "<h3 align=\"center\">'" + url.getHost() + "'</h3>" +
                        "</body></html>");
            } catch (ConnectException ex) {
               setHTML( "<html><body>" +
                        "<h2 align=\"center\">Connect Exception:"+ex.getLocalizedMessage()+"</h2>" +
                        "<h3 align=\"center\">'" + url.getHost() + "'</h3>" +
                        "</body></html>");
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      };
      if (SwingUtilities.isEventDispatchThread()) {
         proc.run();
      } else {
         SwingUtilities.invokeLater(proc);
      }
   }

   public void setHTML(String html) {
      htmlPane.setContentType("text/html");
      htmlPane.setText(html);
      scrollToTop();
   }
   

   public void setText(String text) {
      htmlPane.setContentType("text/ascii");
      htmlPane.setText(text);
      scrollToTop();
   }

   public void clearAll() {
      try {
         Document doc = htmlPane.getDocument();
         doc.remove(0,doc.getLength());
      } catch (Exception e) { e.printStackTrace(); }
   }
   
   public void scrollToTop() {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            try {
               htmlPane.setCaretPosition(0);
            } catch (Exception e) { e.printStackTrace(); }
         }
      });
   }
   
   public void setHTML(File f) {
      try {
         setHTML(f.toURI().toURL());
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }
   }
      
   
   public class Hyperactive implements HyperlinkListener {

      public Hyperactive() {}

      public void hyperlinkUpdate(HyperlinkEvent e) {       
         if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            JEditorPane pane = (JEditorPane)e.getSource();
            if (e instanceof HTMLFrameHyperlinkEvent) {
               HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent)e;
               HTMLDocument doc = (HTMLDocument)pane.getDocument();
               doc.processHTMLFrameHyperlinkEvent(evt);
            } else {
               try {
                  if (external) {
                     //launchURL(getHTMLPane(), e.getURL().toString());
                  } else {
                     setHTML(e.getURL());
                  }
               } catch (Exception t) {
                  t.printStackTrace();
               }
            } 
         }
      }
   }
   
   /**
    * Set to launch hyperlinks in external system browser, or in internal
    * window instead.
    * 
    * @param val true if external browser should load links.
    */
   public void setExternal(boolean val) {
      external = val;
   }
   
   
   public JEditorPane getHTMLPane() {
      return htmlPane;
   }
}
