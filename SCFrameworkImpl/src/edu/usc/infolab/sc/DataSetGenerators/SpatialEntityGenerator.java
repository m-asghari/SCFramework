package edu.usc.infolab.sc.DataSetGenerators;

import java.awt.geom.Point2D;

import org.w3c.dom.Element;

public abstract class SpatialEntityGenerator {
	
	protected RandomGenerator<Point2D.Double> location;
	protected RandomGenerator<Double> releaseTime;
	protected RandomGenerator<Double> duration;
	
	public SpatialEntityGenerator(Element e) {
		location = new RandomGenerator<Point2D.Double>(
				(Element) e.getElementsByTagName("Location").item(0));
		Element releaseTimeElement = (Element) e.getElementsByTagName("ReleaseTime").item(0);
		releaseTime = new RandomGenerator<Double>(releaseTimeElement);
		duration = new RandomGenerator<Double>(
				(Element) e.getElementsByTagName("Duration").item(0));
	}
	
	public Point2D.Double NextLocation() {
		return location.Sample();
	}
	
	public Integer NextDuration() {
		return duration.Sample().intValue();
	}
	
	public Double NextRelease() {
		return releaseTime.Sample();
	}
}
