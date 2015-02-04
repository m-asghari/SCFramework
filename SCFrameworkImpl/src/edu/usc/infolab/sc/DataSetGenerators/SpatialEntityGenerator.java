package edu.usc.infolab.sc.DataSetGenerators;

import java.awt.geom.Point2D;

import org.w3c.dom.Element;

import edu.usc.infolab.sc.SpatialEntity;

public abstract class SpatialEntityGenerator {
	protected RandomGenerator<Point2D.Double> location;
	protected RandomGenerator<Double> releaseTime;
	protected RandomGenerator<Double> duration;
	
	public SpatialEntityGenerator(Element e) {
		location = new RandomGenerator<Point2D.Double>(
				(Element) e.getElementsByTagName("Location").item(0));
		releaseTime = new RandomGenerator<Double>(
				(Element) e.getElementsByTagName("ReleaseTime").item(0));
		duration = new RandomGenerator<Double>(
				(Element) e.getElementsByTagName("Duration").item(0));
	}
	
	public SpatialEntity Fill(SpatialEntity se) {
		se.location = location.Sample();
		Double release = releaseTime.Sample();
		se.releaseFrame = release.intValue();
		Double retract = release + duration.Sample();
		se.retractFrame = retract.intValue();
		return se;
	}
	

}
