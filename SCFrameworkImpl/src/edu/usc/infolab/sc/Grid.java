package edu.usc.infolab.sc;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Random;

import org.w3c.dom.Element;

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
	
	private Grid(Grid g) {
		this.minx = g.minx;
		this.miny = g.miny;
		this.maxx = g.maxx;
		this.maxy = g.maxy;
		this.width = g.width;
		this.length = g.length;
		this.rowCount = g.rowCount;
		this.colCount = g.colCount;
		this.maxDistance = g.maxDistance;
	}
	
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
	
	public int size() {
		return rowCount * colCount;
	}
	
	public int GetLength() {
		return (int)(maxx - minx);
	}
	
	public int GetWidth() {
		return (int)(maxy - miny);
	}
	
	public Element Fill(Element g) {
		g.setAttribute("maxx", Double.toString(this.maxx));
		g.setAttribute("minx", Double.toString(this.minx));
		g.setAttribute("miny", Double.toString(this.miny));
		g.setAttribute("maxy", Double.toString(this.maxy));
		g.setAttribute("l", Double.toString(this.length));
		g.setAttribute("w", Double.toString(this.width));
		return g;
	}
	
	public boolean In(Point2D.Double p) {
		if (p.getX() < minx || p.getX() > maxx || p.getY() < miny || p.getY() > maxy)
			return false;
		return true;		
	}
	
	public int GetCell(Point2D.Double p) {
		if (!this.In(p)) {
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
	
	public void Draw(Graphics2D g, int scale) {
		for (double i = minx + length; i < maxx; i+=length) {
			g.drawLine((int)(i * scale) , (int)(miny * scale), (int)(i * scale), (int)(maxy * scale));
		}
		for (double j = miny + width; j < maxy; j+= width) {
			g.drawLine((int)(minx * scale), (int)(j * scale), (int)(maxx * scale), (int)(j * scale));
		}
	}
	
	@Override
	public Grid clone() {
		return new Grid(this);
	}
}
