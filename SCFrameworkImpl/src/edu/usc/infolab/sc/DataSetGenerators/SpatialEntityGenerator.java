package edu.usc.infolab.sc.DataSetGenerators;

import java.awt.geom.Point2D;

import org.w3c.dom.Element;

import edu.usc.infolab.sc.SpatialEntity;

public abstract class SpatialEntityGenerator {
	private static enum ReleaseMode {
		Independent, InterArrival
	};
	private ReleaseMode releaseMode;
	private Integer lastRelease;
	protected RandomGenerator<Point2D.Double> location;
	protected RandomGenerator<Double> releaseTime;
	protected RandomGenerator<Double> duration;
	
	public SpatialEntityGenerator(Element e) {
		location = new RandomGenerator<Point2D.Double>(
				(Element) e.getElementsByTagName("Location").item(0));
		Element releaseTimeElement = (Element) e.getElementsByTagName("ReleaseTime").item(0);
		releaseMode = (releaseTimeElement.getAttribute("mode").equals("InterArrival")) ?
				ReleaseMode.InterArrival : ReleaseMode.Independent;
		lastRelease = 0;
		releaseTime = new RandomGenerator<Double>(releaseTimeElement);
		duration = new RandomGenerator<Double>(
				(Element) e.getElementsByTagName("Duration").item(0));
	}
	
	public SpatialEntity Fill(SpatialEntity se) {
		se.location = location.Sample();
		while (!Main.grid.In(se.location)) se.location = location.Sample();
		Double release = releaseTime.Sample();
		lastRelease += release.intValue();
		se.releaseFrame = (releaseMode == ReleaseMode.Independent) ? 
				release.intValue() : lastRelease;
		Double retract = se.releaseFrame + duration.Sample();
		se.retractFrame = retract.intValue();
		return se;
	}
	

}
