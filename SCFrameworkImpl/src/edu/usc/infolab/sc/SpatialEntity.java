package edu.usc.infolab.sc;

import java.awt.geom.Point2D;

import org.w3c.dom.Element;

public abstract class SpatialEntity {
	public Integer id;
	public Point2D.Double location;
	public Integer releaseFrame;
	public Integer retractFrame;
	
	public SpatialEntity() {}
	
	public SpatialEntity(Element e) {
		Double x = Double.parseDouble(e.getAttribute("x"));
		Double y = Double.parseDouble(e.getAttribute("y"));
		this.location = new Point2D.Double(x, y);
		this.releaseFrame = Integer.parseInt(e.getAttribute("release"));
		this.retractFrame = Integer.parseInt(e.getAttribute("retract"));
	}
	
	protected Element Fill(Element se) {
		se.setAttribute("x", Double.toString(location.getX()));
		se.setAttribute("y", Double.toString(location.getY()));
		se.setAttribute("release", Integer.toString(releaseFrame));
		se.setAttribute("retract", Integer.toString(retractFrame));
		return se;
	}
	
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
