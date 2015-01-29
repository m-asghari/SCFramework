package edu.usc.infolab.sc.Distributions;

import java.awt.geom.Point2D;
import java.util.Random;

import org.w3c.dom.Element;

public class Uniform2D extends PointDistribution {
	Random rand;
	double minx;
	double miny;
	double maxx;
	double maxy;
	double dx;
	double dy;
	
	public static Uniform2DConfig Parse(Element e) {
		Uniform2DConfig c = new Uniform2DConfig();
		c.minx = Double.parseDouble(((Element) e.getElementsByTagName("minx").item(0)).getAttribute("value"));
		c.maxx = Double.parseDouble(((Element) e.getElementsByTagName("maxx").item(0)).getAttribute("value"));
		c.miny = Double.parseDouble(((Element) e.getElementsByTagName("miny").item(0)).getAttribute("value"));
		c.maxy = Double.parseDouble(((Element) e.getElementsByTagName("maxy").item(0)).getAttribute("value"));
		return c;
	}
	
	public Uniform2D(DistributionConfig c) {
		Uniform2DConfig config = (Uniform2DConfig) c;
		minx = config.minx;
		miny = config.miny;
		maxx = config.maxx;
		maxy = config.maxy;
		dx = maxx - minx;
		dy = maxy - miny;
		
		rand = new Random();
	}
	
	@Override
	public Point2D.Double Sample() {
		double newX = (rand.nextDouble() * dx) + minx;
		double newY = (rand.nextDouble() * dy) + miny;
		return new Point2D.Double(newX, newY);
	}

}
