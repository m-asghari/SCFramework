package edu.usc.infolab.sc;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class Grid {
	double minx;
	double miny;
	double maxx;
	double maxy;
	double width;
	double length;
	int rowCount;
	int colCount;
	public ArrayList<Double> cellProb;
	
	public Grid(Point2D.Double min, Point2D.Double max, double w, double l) {
		minx = min.getX();
		miny = min.getY();
		maxx = max.getX();
		maxy = max.getY();
		width = w;
		length = l;
		rowCount = (int)((maxy - miny)/width);
		colCount = (int)((maxx - minx)/length);
		cellProb = new ArrayList<Double>();
	}
	
	public boolean In(Point2D.Double p) {
		if (p.getX() < minx || p.getX() > maxx || p.getY() < miny || p.getY() > maxy)
			return false;
		return true;		
	}
	
	public int GetCell(Point2D.Double p) {
		if (!this.In(p)) return -1;
		int col = (int)((p.getX() - minx)/length);
		int row = (int)((p.getY() - miny)/width);
		return (row * colCount) + col;
	}
	
	public Point2D.Double RandomPoint(int cellNum) {
		Random rand = new Random();
		Double startX = (cellNum % colCount) * length;
		Double startY = (cellNum / colCount) * width;
		double newX = (rand.nextDouble() * length) + startX;
		double newY = (rand.nextDouble() * width) + startY;
		return new Point2D.Double(newX, newY);
	}

}