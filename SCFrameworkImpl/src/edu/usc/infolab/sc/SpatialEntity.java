package edu.usc.infolab.sc;

import java.awt.geom.Point2D;

public abstract class SpatialEntity {
	public Integer id;
	public Point2D.Double location;
	public Integer releaseFrame;
	public Integer retractFrame;
	
	public Integer Distance(SpatialEntity se) {
		return (int)location.distance(se.location);
	}
	
	public Boolean Overlap(SpatialEntity se) {
		if (this.releaseFrame > se.retractFrame ||
				this.retractFrame < se.releaseFrame) 
			return false;
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		SpatialEntity se = (SpatialEntity)obj;
		return this.id == se.id;
	}
}
