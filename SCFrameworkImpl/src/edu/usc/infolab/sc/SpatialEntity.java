package edu.usc.infolab.sc;

import java.awt.geom.Point2D;

public abstract class SpatialEntity {
	Point2D.Double location;
	
	public Double Distance(SpatialEntity se) {
		return location.distance(se.location);
	}
}
