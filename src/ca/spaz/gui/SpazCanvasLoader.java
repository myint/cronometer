package ca.spaz.gui;

import javax.swing.JComponent;
import javax.swing.JPanel;


/**
 * An XML definition of a GUI layout.
 *
 * @author Aaron Davidson 
 * @date   September 2002
 */
 
public class SpazCanvasLoader extends SpazCanvas {
	public JPanel proxy;
	
	public SpazCanvasLoader(String file, JPanel proxy) {
		super(file, null);
		this.proxy = proxy;
		this.actions = null;
	}

	public void add(JComponent c, SpazPosition lp) {
		proxy.add(c, lp);
	}

}
