package ca.spaz.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.border.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

/**
 * An XML definition of a GUI layout.
 *
 * @author Aaron Davidson 
 * @date   September 2002
 */

public class SpazCanvas extends JPanel implements ActionListener {
	protected Hashtable actions, components;
	protected Object listener;

	public SpazCanvas(String file) {
		super();
		init(file);
	}

	public SpazCanvas(String file, Object listener) {
		super();
		this.listener = listener;
		init(file);
	}

	private void init(String file) {
		this.actions = new Hashtable();
		this.components = new Hashtable();
		this.setLayout(new SpazLayout());
		loadXML(file);
	}

	public void setListener(Object l) {
		this.listener = l;
	}

	public void loadXML(String fname) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			File f = new File(fname);
			Document d = db.parse(f);
			Element e = d.getDocumentElement();
			parseChildren(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SpazPosition getPosition(JComponent c) {
		return ((SpazLayout)this.getLayout()).getPosition(c);
	}

	public String getAction(JComponent c) {
		return (String)actions.get(c);
	}

	public JComponent getComponent(String idStr) {
		return (JComponent)components.get(idStr);
	}

	public Enumeration components() {
		return components.elements();
	}

	public void parseChildren(Element e) {
		try {
			NodeList nl = e.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				// parse Elements only
				if ((nl.item(i).getNodeType() == Node.ELEMENT_NODE)) {
					Element elm = (Element)nl.item(i);
					String tag = elm.getTagName();
					if (tag.equals("canvas")) {
						parseChildren(elm);
					} else if (tag.equals("label")) {
						parseLabel(elm);
					} else if (tag.equals("textfield")) {
						parseTextField(elm);
					} else if (tag.equals("button")) {
						parseButton(elm);
					} else if (tag.equals("textarea")) {
						parseTextArea(elm);
					} else if (tag.equals("progressbar")) {
						parseProgressBar(elm);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void parseTextArea(Element elm) {
		JTextPane text = new JTextPane();
		JScrollPane jsp =
			new JScrollPane(
				text,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//text.setWrapStyleWord(true);
		jsp.setAutoscrolls(true);
		SpazPosition lp = parseSpazPosition(elm);
		if (lp == null) 
			return;
		this.add(jsp, lp);
		parseBorder(jsp, elm);
		parseAttributes(elm, jsp);
	}

	public void parseLabel(Element elm) {
		JLabel label = new JLabel();
		String title = elm.getAttribute("title");
		if (title != null)
			label.setText(title);
		SpazPosition lp = parseSpazPosition(elm);
		if (lp == null)
			return;
		this.add(label, lp);
		parseBorder(label, elm);
		parseAttributes(elm, label);
	}

	public void parseProgressBar(Element elm) {
		JProgressBar bar = new JProgressBar();
		SpazPosition lp = parseSpazPosition(elm);
		if (lp == null)
			return;
		this.add(bar, lp);
		parseBorder(bar, elm);
		parseAttributes(elm, bar);
	}

	public void parseButton(Element elm) {
		JButton btn = new JButton();
		String title = elm.getAttribute("title");
		if (title != null)
			btn.setText(title);
		SpazPosition lp = parseSpazPosition(elm);
		if (lp == null)
			return;
		this.add(btn, lp);
		parseBorder(btn, elm);
		parseAttributes(elm, btn);
		String action = elm.getAttribute("action");
		if (action.length() > 0) {
			actions.put(btn, action);
			btn.addActionListener(this);
		}

	}

	public void parseTextField(Element elm) {
		String title = "";
		JTextField textField = new JTextField(title);
		SpazPosition lp = parseSpazPosition(elm);
		if (lp == null)
			return;
		this.add(textField, lp);
		parseBorder(textField, elm);
		parseAttributes(elm, textField);
	}

	public void parseAttributes(Element elm, JComponent comp) {
		String enabled = elm.getAttribute("enabled");
		if (enabled != null)
			comp.setEnabled(!enabled.equals("false"));
		String id = elm.getAttribute("id");
		if (id.length() > 0) {
			components.put(id, comp);
			comp.setName(id);
		}
		String tooltip = elm.getAttribute("tooltip");
		if (tooltip.length() > 0) {
			comp.setToolTipText(tooltip);
		}
	}

	public SpazPosition parseSpazPosition(Element elm) {
		if (elm.getTagName().equals("position")) {
			SpazPosition lp = new SpazPosition();
			double left_rel = Double.parseDouble(elm.getAttribute("left_rel"));
			double right_rel = Double.parseDouble(elm.getAttribute("right_rel"));
			double top_rel = Double.parseDouble(elm.getAttribute("top_rel"));
			double bottom_rel = Double.parseDouble(elm.getAttribute("bottom_rel"));
			int left_abs = Integer.parseInt(elm.getAttribute("left_abs"));
			int right_abs = Integer.parseInt(elm.getAttribute("right_abs"));
			int top_abs = Integer.parseInt(elm.getAttribute("top_abs"));
			int bottom_abs = Integer.parseInt(elm.getAttribute("bottom_abs"));
			lp.setHPos(left_rel, left_abs, right_rel, right_abs);
			lp.setVPos(top_rel, top_abs, bottom_rel, bottom_abs);
			return lp;
		} else {
			NodeList nl = elm.getElementsByTagName("position");
			for (int n = 0; n < nl.getLength(); n++) {
				SpazPosition lp = parseSpazPosition((Element) (nl.item(n)));
				return lp;
			}
		}
		return null;
	}

	public void parseBorder(JComponent c, Element elm) {
		if (elm.getTagName().equals("border")) {
			String type = elm.getAttribute("type");
			if (type.equals("empty")) {
				c.setBorder(new EmptyBorder(2, 2, 2, 2));
			} else if (type.equals("titled")) {
				String title = elm.getAttribute("title");
				c.setBorder(new TitledBorder(title));
			} else if (type.equals("beveled")) {
				int btype = Integer.parseInt(elm.getAttribute("bevel"));
				c.setBorder(new BevelBorder(btype));
			}
		} else {
			NodeList nl = elm.getElementsByTagName("border");
			for (int n = 0; n < nl.getLength(); n++) {
				parseBorder(c, (Element)nl.item(n));
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (listener == null)
			return;
		String methodName = (String)actions.get(e.getSource());
		if (methodName != null) {
			try {
				Class[] params = null;
				Method m = listener.getClass().getMethod(methodName, params);
				Object[] params2 = null;
				m.invoke(listener, params2);
			} catch (NoSuchMethodException me) {
				JOptionPane.showMessageDialog(this, 
                  "No Action Handler for  '" + methodName + "'");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
