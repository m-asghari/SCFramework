package edu.usc.infolab.sc.Distributions;

import java.awt.geom.Point2D;
import java.util.Random;

public class Uniform2D {
	Random rand;
	double minx;
	double miny;
	double maxx;
	double maxy;
	double dx;
	double dy;
	
	public Uniform2D(Point2D.Double min, Point2D.Double max) {
		minx = min.getX();
		miny = min.getY();
		maxx = max.getX();
		maxy = max.getY();
		dx = maxx - minx;
		dy = maxy - miny;
		
		rand = new Random();
	}
	
	public Point2D.Double Next() {
		double newX = (rand.nextDouble() * dx) + minx;
		double newY = (rand.nextDouble() * dy) + miny;
		return new Point2D.Double(newX, newY);
	}

}
