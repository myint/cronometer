package ca.spaz.gui;

import java.awt.Toolkit;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import ca.spaz.util.ToolBox;


/**
 * A JMenuBar that can be created from an XML description.
 * Reflection is used to automatically dispatch the menu selections
 * without need of registering listeners for each item.
 *
 *
 * @author Aaron Davidson 
 * @date   September 2002
 */
 
/* EXAMPLE:
			<menubar>
				<menu title="File">
					<item title="Quit" key="q" action="doQuit"/>
				</menu>
				<menu title="Plot">
					<item title="GC Density" key="g" action="doGCDensity"/>
					<item title="AT Density" key="a" action="doATDensity"/>
				</menu>
			</menubar>
*/

public class SpazMenuBar extends JMenuBar implements ActionListener {
	private Hashtable actions;
	private Object listener;
	
   public SpazMenuBar(InputStream file, Object listener) {
      super();
      this.listener = listener;
      this.actions = new Hashtable();
      loadXML(file);
   }
   
   public SpazMenuBar(String file, Object listener) throws FileNotFoundException {
      super();
      this.listener = listener;
      this.actions = new Hashtable();      
      BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
      loadXML(in);
   }
	

	public void loadXML(InputStream file) {
      try {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         dbf.setNamespaceAware(true);
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document d = db.parse(file);    
         loadXML(d);
      } catch (Exception e) {
        e.printStackTrace();
      }
   }    
   
	public void loadXML(Document d) {
      try {
   	   Element e = d.getDocumentElement();
   	   NodeList nl = e.getElementsByTagName("menu");
   	   for (int n=0; n<nl.getLength(); n++) {
	         Element elm = (Element)(nl.item(n));  
				loadXMLMenu(elm);
	      }
      } catch (Exception e) {
        e.printStackTrace();
      }
   }
   


   public void loadXMLMenu(Element elm) {
      String title = elm.getAttribute("title");
      JMenu menu = new JMenu(title);
      String key = elm.getAttribute("key");      
      if (key != null && key.length() > 0) {
        menu.setMnemonic(key.charAt(0));
      }
      String index = elm.getAttribute("index");
      if (index != null && index.length() > 0) {
          menu.setDisplayedMnemonicIndex(Integer.parseInt(index));	
      }
      loadXMLMenuItems(elm, menu);
      add(menu);
   }


   public void loadXMLMenuItems(Element elm, JMenu menu) {     
      NodeList nl = elm.getChildNodes();
      for (int n = 0; n < nl.getLength(); n++) {
         if (nl.item(n) instanceof Element) {
            Element e = (Element)(nl.item(n));
            if (e.getTagName().equals("item")) {
               loadXMLMenuItem(e, menu);
            } else if (e.getTagName().equals("submenu")) {
               String title = e.getAttribute("title");
               JMenu submenu = new JMenu(title);
               menu.add(submenu);
               loadXMLMenuItems(e, submenu);
               actions.put(submenu, "SUBMENU_"+title);
            }
         }
      }     
   }
   
   public void loadXMLMenuItem(Element elm, JMenu jm) {
      if (ToolBox.isMacOSX()) {
         String macosx = elm.getAttribute("macosx");
         if (macosx != null && macosx.equalsIgnoreCase("false")) {
            return; // bail out on mac os x
         }
      }

      String separator = elm.getAttribute("separator");
      if (separator != null) {
         if (separator.equals("true")) {
            jm.addSeparator();
            return;
         }
      }

      String title = elm.getAttribute("title");
      String tooltip = elm.getAttribute("tooltip");
      String action = elm.getAttribute("action");
      String key = elm.getAttribute("key");
      String index = elm.getAttribute("index");      
      String acc = elm.getAttribute("acc");
      String checkbox = elm.getAttribute("checkbox");
      JMenuItem item = new JMenuItem(title);
      if (checkbox != null) {
         if (checkbox.equals("true")) {
            item = new JCheckBoxMenuItem(title);
         }
      }
      jm.add(item);
      item.addActionListener(this);
      actions.put(item, action);
      if (key != null && key.length() > 0) {
         item.setMnemonic(key.charAt(0));
      }

      if (index != null && index.length() > 0) {
    	  item.setDisplayedMnemonicIndex(Integer.parseInt(index));	
      }      
      if (acc != null && acc.length() > 0) {
         try {
            Class c = KeyEvent.class;
            item.setAccelerator(  
               KeyStroke.getKeyStroke(c.getDeclaredField(acc).getInt(c),
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));            
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      if (tooltip != null) {
         item.setToolTipText(tooltip);
      }
   }  
   
   public void setEnabled(String action, boolean on) {
   	JMenuItem item = getItem(action);
   	if (item != null) {
			item.setEnabled(on);   
		}
   }
   
   public JMenuItem getItem(String action) {
   	Enumeration e = actions.keys();
   	while (e.hasMoreElements()) {
   		JMenuItem item = (JMenuItem)e.nextElement();
   		String s = (String)actions.get(item);
   		if (s.equals(action)) {
   			return item;
   		}
   	}
   	return null;
   }
   
   
   public boolean isSelected(String name) {
		JCheckBoxMenuItem item = (JCheckBoxMenuItem)getItem(name);		
		return item.isSelected();
   }
   
   public void actionPerformed(ActionEvent e) {
   	String methodName = (String)actions.get(e.getSource());
   	if (methodName != null) {
   		try {
	   		Class[] params = null;
   			Method m = listener.getClass().getMethod(methodName, params);
	   		Object[] params2 = null;
   			m.invoke(listener, params2);
   		} catch (NoSuchMethodException me) { 
   			JOptionPane.showMessageDialog(null, "No Menu Handler for  '"+methodName+"'");
/*   		} catch (SecurityException se) {
   		} catch (IllegalAccessException ae) {
   		} catch (InvocationTargetException  ie) {*/
   		} catch (Exception ex) {
				ex.printStackTrace();
   		}
   	}
   }
   

}
