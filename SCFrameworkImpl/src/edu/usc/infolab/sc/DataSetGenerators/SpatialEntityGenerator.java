package edu.usc.infolab.sc.DataSetGenerators;

import java.awt.geom.Point2D;

import org.w3c.dom.Element;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Distributions.Distribution;

public abstract class SpatialEntityGenerator {
	
	protected RandomGenerator<Point2D.Double> location;
	protected RandomGenerator<Double> releaseTime;
	protected RandomGenerator<Double> duration;
	private Grid grid;
	
	public SpatialEntityGenerator(Element e, Grid grid) {
		location = new RandomGenerator<Point2D.Double>(
				(Element) e.getElementsByTagName("Location").item(0));
		Element releaseTimeElement = (Element) e.getElementsByTagName("ReleaseTime").item(0);
		releaseTime = new RandomGenerator<Double>(releaseTimeElement);
		duration = new RandomGenerator<Double>(
				(Element) e.getElementsByTagName("Duration").item(0));
		this.grid = grid.clone();
	}
	
	public void SetReleaseTimeDist(Distribution<Double> dist) {
		this.releaseTime = new RandomGenerator<Double>(dist);
	}
	
	public void SetDurationDist(Distribution<Double> dist) {
		this.duration = new RandomGenerator<Double>(dist);
	}
	
	public Point2D.Double NextLocation() {
		Point2D.Double p = location.Sample();
		while (!grid.In(p)) {
			p = location.Sample();
		}
		return p;
	}
	
	public Integer NextDuration() {
		return duration.Sample().intValue();
	}
	
	public Double NextRelease() {
		return releaseTime.Sample();
	}
}
