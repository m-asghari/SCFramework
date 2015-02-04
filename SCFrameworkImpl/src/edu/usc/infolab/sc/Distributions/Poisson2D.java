package edu.usc.infolab.sc.Distributions;

import java.awt.geom.Point2D;
import java.util.Random;

import org.w3c.dom.Element;

import edu.usc.infolab.sc.Grid;

public class Poisson2D extends Distribution<Point2D.Double> {
	Grid grid;
	Double intensity;
	Random rand;
	
	public static Poisson2DConfig ParseConfig(Element e) {
		Poisson2DConfig c = new Poisson2DConfig();
		c.intensity = Double.parseDouble(((Element)e.getElementsByTagName("intensity").item(0)).getAttribute("value"));
		Double minx = Double.parseDouble(((Element)e.getElementsByTagName("grid").item(0)).getAttribute("minx"));
		Double miny = Double.parseDouble(((Element)e.getElementsByTagName("grid").item(0)).getAttribute("miny"));
		Double maxx = Double.parseDouble(((Element)e.getElementsByTagName("grid").item(0)).getAttribute("maxx"));
		Double maxy = Double.parseDouble(((Element)e.getElementsByTagName("grid").item(0)).getAttribute("maxy"));
		Double w = Double.parseDouble(((Element)e.getElementsByTagName("grid").item(0)).getAttribute("w"));
		Double l = Double.parseDouble(((Element)e.getElementsByTagName("grid").item(0)).getAttribute("l"));
		c.grid = new Grid(new Point2D.Double(minx, miny), new Point2D.Double(maxx, maxy), w, l);
		return c;
	}
	
	public Poisson2D(Poisson2DConfig config) {
		this.grid = config.grid;
		this.intensity = config.intensity;
		rand = new Random();		
	}
	
	public void Initialize() {
		Poisson p = new Poisson(new PoissonConfig(this.intensity));
		Double sum = 0.0;
		for (int cell = 0; cell < grid.cellProb.size(); ++cell) {
			double prob = p.Next()*1.0;
			grid.cellProb.add(cell, prob);
			sum += prob;			
		}
		for (int cell = 0; cell < grid.cellProb.size(); ++cell) {
			double old = grid.cellProb.get(cell);
			grid.cellProb.set(cell, old/sum);
		}
	}
	
	@Override
	public Point2D.Double Sample() {
		Double p = rand.nextDouble();
		int cell = 0;
		double cdf = 0.0;
		while ((cdf += grid.cellProb.get(cell++)) < p );
		return grid.RandomPoint(cell);
	}
	
	public static Class<?> GetConfigClass() {
		return Poisson2DConfig.class;
	}
}
