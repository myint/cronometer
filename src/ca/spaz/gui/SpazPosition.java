package ca.spaz.gui;

import java.awt.Rectangle;

/** 
 */

public class SpazPosition {
	public int leftA, rightA, topA, bottomA;
	public double leftR, rightR, topR, bottomR;

	public SpazPosition(){}
	
	public SpazPosition(SpazPosition copy) {
		this.leftA = copy.leftA;
		this.leftR = copy.leftR;
		this.rightA = copy.rightA;
		this.rightR = copy.rightR;
		this.topA = copy.topA;
		this.topR = copy.topR;
		this.bottomA = copy.bottomA;
		this.bottomR = copy.bottomR;				
	}
	
	public void setHPos(double lR, int lA, double rR, int rA) {
		this.leftR = lR;
		this.leftA = lA;
		this.rightR = rR;
		this.rightA = rA;
	}
	
	public void setVPos(double tR, int tA, double bR, int bA) {
		this.topR = tR;
		this.topA = tA;
		this.bottomR = bR;
		this.bottomA = bA;
	}

	public Rectangle getRectangle(int width, int height) {
		int top = (int)(topR*height) + topA;
		int bottom = (int)(bottomR*height) + bottomA;
		int left = (int)(leftR*width) + leftA;
		int right = (int)(rightR*width) + rightA;
		return ( new Rectangle(left, top, (right - left), (bottom - top)) );
	}	
	
	public Rectangle getMinRectangle() {
		return ( new Rectangle(leftA, topA, (rightA - leftA), (bottomA - topA)) );
	}	
	
	public String toString() {
		return (leftA + ", " + rightA + ", " + topA + ", " + bottomA);
	}
	
	public void adjustLeft(int val, int width) {
		int cur = (int)(width * leftR);
		this.leftA = (val - cur);
	}

	public void adjustRight(int val, int width) {
		int cur = (int)(width * rightR);
		this.rightA = (val - cur);
	}

	public void adjustTop(int val, int height) {
		int cur = (int)(height * topR);
		this.topA = (val - cur);
	}

	public void adjustBottom(int val, int height) {
		int cur = (int)(height * bottomR);
		this.bottomA = (val - cur);
	}

	public boolean valid() {
		if ((topA + (topR*500)) > (bottomA + (bottomR*500))) return false;
		if ((leftA + (leftR*500)) > (rightA + (rightR*500))) return false;
		if (leftR < 0 || leftR > 1) return false;
		if (rightR < 0 || rightR > 1) return false;
		if (topR < 0 || topR > 1) return false;
		if (bottomR < 0 || bottomR > 1) return false;
		return true;
	}


	public String toXMLString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<position ");
		sb.append(" left_rel=\""+ leftR + '"');
		sb.append(" left_abs=\""+ leftA + '"');
		sb.append(" right_rel=\""+ rightR + '"');
		sb.append(" right_abs=\""+ rightA + '"');
		sb.append(" top_rel=\""+ topR + '"');
		sb.append(" top_abs=\""+ topA + '"');
		sb.append(" bottom_rel=\""+ bottomR + '"');
		sb.append(" bottom_abs=\""+ bottomA + '"');
		sb.append("/>\n");
		return sb.toString();		
	}

	
}