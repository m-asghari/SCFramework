package edu.usc.infolab.sc;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Worker extends SpatialEntity{
	public Integer retractFrame;
	
	Boolean active;
	ArrayList<Task> assignedTasks;
	Integer maxNumberOfTasks;
	Double travledDistance;
		
	public Worker() {		
		
	}
	
	public void UpdateLocation() {
		Point2D.Double dest = assignedTasks.get(0).location;
		Double dist = location.distance(dest);
		if (dist > 1) {
			MoveToward(dest, 1.0);
			travledDistance += 1.0;
		}
		else if (assignedTasks.size() > 1 ){
			// TODO(masghari): set assignedTask.get(0) as Done!
			// TODO(masghari): remove done task from the list!
			
			// Start moving toward new task
			dest = assignedTasks.get(0).location;
			MoveToward(dest, 1.0 - dist);
			travledDistance += 1.0;
		}
		else {
			travledDistance += dist;
		}
	}
	
	public void AddTask(Task task) {
		assignedTasks.add(task);
	}
	
	private void MoveToward(Point2D dest, Double length) {
		Double dist = location.distance(dest);
		Double deltaX = length * (dest.getX() - location.getX()) / dist;
		Double newX = location.getX() + deltaX;
		Double deltaY = length * (dest.getY() - location.getY()) / dist;
		Double newY = location.getY() + deltaY;
		location.setLocation(newX, newY);
	}
	
	public Boolean CanPerform(Task task) {
		if (assignedTasks.size() == maxNumberOfTasks) {
			return false;
		}
		return true;
	}
	
	
}
