package edu.usc.infolab.sc;

import java.awt.geom.Point2D;
import java.util.Random;

import org.w3c.dom.Element;

import edu.usc.infolab.sc.Main.Log;

public class Grid {
	
	double minx;
	double miny;
	double maxx;
	double maxy;
	double width;
	double length;
	int rowCount;
	int colCount;
	double maxDistance;
	//public ArrayList<Double> cellProb;
	//public ArrayList<Integer> cellCount;
	//public int totalCount;
	//CountDistribution dist;
	//private int size;
	
	public Grid(Element e) {
		minx = Double.parseDouble(e.getAttribute("minx"));
		miny = Double.parseDouble(e.getAttribute("miny"));
		maxx = Double.parseDouble(e.getAttribute("maxx"));
		maxy = Double.parseDouble(e.getAttribute("maxy"));
		width = Double.parseDouble(e.getAttribute("w"));
		length = Double.parseDouble(e.getAttribute("l"));
		rowCount = (int)((maxy - miny)/width);
		colCount = (int)((maxx - minx)/length);
		maxDistance = this.GetCellMidPoint(0).distance(this.GetCellMidPoint(this.size() - 1));
		//size = rowCount * colCount;
		//dist = new CountDistribution(this.size());
	}
	
	public int size() {
		return rowCount * colCount;
	}
	
	public Grid(Point2D.Double min, Point2D.Double max, double w, double l) {
		minx = min.getX();
		miny = min.getY();
		maxx = max.getX();
		maxy = max.getY();
		width = w;
		length = l;
		rowCount = (int)((maxy - miny)/width);
		colCount = (int)((maxx - minx)/length);
		maxDistance = this.GetCellMidPoint(0).distance(this.GetCellMidPoint(this.size() - 1));
		//dist = new CountDistribution(this.size());
		
	}
	
	public boolean In(SpatialEntity se) {
		return this.In(se.location);
	}
	
	public boolean In(Point2D.Double p) {
		if (p.getX() < minx || p.getX() > maxx || p.getY() < miny || p.getY() > maxy)
			return false;
		return true;		
	}
	
	public int GetCell(Point2D.Double p) {
		if (!this.In(p)) {
			Log.Add(2, "x:%.2f, y:%.2f", p.x, p.y);
			return -1;
		}
		int col = (int)((p.getX() - minx)/length);
		int row = (int)((p.getY() - miny)/width);
		return (row * colCount) + col;
	}
	
	public Point2D.Double GetCellMidPoint(int cell) {
		int row = cell / colCount;
		int col = cell % colCount;
		double x = (col * width) + (width / 2);
		double y = (row * length) + (length / 2);
		return new Point2D.Double(x, y);
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
