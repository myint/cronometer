package ca.spaz.gui;

import java.awt.*;
import java.util.Hashtable;

/** 
 */

public class SpazLayout implements LayoutManager2 {
    Hashtable compTable = new Hashtable();
    
    /**
     */
    public SpazLayout() { }
    
    public void setConstraints(Component comp, SpazPosition constraints) {
        compTable.put(comp, new SpazPosition(constraints));
    }
    
    /**
     * Adds the specified component with the specified name to
     * the layout.  This does nothing in GraphPaperLayout, since constraints
     * are required.
     */
    public void addLayoutComponent(String name, Component comp) {}

    /**
     * Removes the specified component from the layout.
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp) {
        compTable.remove(comp);
    }
    
    public SpazPosition getPosition(Component comp) {
    	return  (SpazPosition)compTable.get(comp);
    }

    public void setPosition(Component comp, SpazPosition lp) {
    	compTable.put(comp, lp);
    }


    /**
     * Calculates the preferred size dimensions for the specified 
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     *  
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container parent) {
    	int ncomponents = parent.getComponentCount();
		if (ncomponents == 0) return new Dimension(1,1);
		Rectangle totalRect = new Rectangle(0,0,1,1);
		Dimension size = parent.getSize();
      Insets insets = parent.getInsets();		
		int totalW = size.width - (insets.left + insets.right);
		int totalH = size.height - (insets.top + insets.bottom);
		for ( int i = 0; i < ncomponents; i++ ) {
			Component c = parent.getComponent(i);
			SpazPosition lp = (SpazPosition)compTable.get(c);
         Rectangle rect = lp.getRectangle(totalW, totalH);
         if ( rect != null ) 
         	totalRect = totalRect.union(rect); 

		}
		return new Dimension(totalRect.width,totalRect.height);
    }

    /** 
     * Calculates the minimum size dimensions for the specified 
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent) {
    	int ncomponents = parent.getComponentCount();
		if (ncomponents == 0) return new Dimension(1,1);
		Rectangle totalRect = new Rectangle(0,0,1,1);
		for ( int i = 0; i < ncomponents; i++ ) {
			Component c = parent.getComponent(i);
			SpazPosition lp = (SpazPosition)compTable.get(c);
         Rectangle rect = lp.getMinRectangle();
         if ( rect != null ) 
         	totalRect = totalRect.union(rect); 

		}
		return new Dimension(totalRect.width,totalRect.height);
    }
    

    /** 
     * Lays out the container in the specified container.
     * @param parent the component which needs to be laid out 
     */
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            if (ncomponents == 0) return;
            
            // Total parent dimensions
            Dimension size = parent.getSize();
            int totalW = size.width - (insets.left + insets.right);
            int totalH = size.height - (insets.top + insets.bottom);

            for ( int i = 0; i < ncomponents; i++ ) {
                Component c = parent.getComponent(i);
                SpazPosition lp = getPosition(c);
                Rectangle rect = lp.getRectangle(totalW, totalH);
                if ( rect != null ) {
                    int x = insets.left + rect.x;
                    int y = insets.top + rect.y;
                    c.setBounds(x, y, rect.width,  rect.height);
                }
            }
        }
    }
    
    // LayoutManager2 /////////////////////////////////////////////////////////
    
    /**
     * Adds the specified component to the layout, using the specified
     * constraint object.
     * @param comp the component to be added
     * @param constraints  where/how the component is added to the layout.
     */
    public void addLayoutComponent(Component comp, Object constraints) {
        if (constraints instanceof SpazPosition) {
            SpazPosition cons = (SpazPosition)constraints;
            setConstraints(comp, cons);
        } else if (constraints != null) {
            throw new IllegalArgumentException(
                "cannot add to layout: constraint must be a SpazPostion");
        }
    }

    /** 
     * Returns the maximum size of this component.
     * @see java.awt.Component#getMinimumSize()
     * @see java.awt.Component#getPreferredSize()
     * @see LayoutManager
     */
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other 
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    /**
     * Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other 
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }

    /**
     * Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded.
     */
    public void invalidateLayout(Container target) {
        // Do nothing
    }
}
