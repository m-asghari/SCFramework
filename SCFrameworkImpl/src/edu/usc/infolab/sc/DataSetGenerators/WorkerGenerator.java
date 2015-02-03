package edu.usc.infolab.sc.DataSetGenerators;

import java.awt.geom.Point2D;

import org.w3c.dom.Element;

public class WorkerGenerator {
	
	RandomGenerator<Point2D.Double> locationGenerator;
	
	public WorkerGenerator() {
		locationGenerator = new RandomGenerator<Point2D.Double>();
	}
	
	public void Parse(Element e) {
		Element location = (Element) e.getElementsByTagName("Location").item(0);
		locationGenerator = new RandomGenerator<Point2D.Double>(location);
	}

}
