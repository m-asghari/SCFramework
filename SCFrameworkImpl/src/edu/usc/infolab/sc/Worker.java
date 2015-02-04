package edu.usc.infolab.sc;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.w3c.dom.Element;

public class Worker extends SpatialEntity{
	public Boolean active;
	public ArrayList<Task> assignedTasks;
	public Integer maxNumberOfTasks;
	Double travledDistance;
	PTSs ptsSet;
		
	public Worker() {
		Initialize();
	}
	
	public Worker(Element e) {
		super(e);
		Initialize();
		this.maxNumberOfTasks = Integer.parseInt(e.getAttribute("max"));
	}
	
	private void Initialize() {
		ptsSet = new PTSs();
		assignedTasks = new ArrayList<Task>();
		travledDistance = 0.0;
		active = false;
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
	
	public Boolean IsPTS(PTS pts) {
		ArrayList<ArrayList<Task>> taskPerms = Utils.Permutations(pts.list);
		for (ArrayList<Task> p : taskPerms) {
			Boolean possible = true;
			Point2D.Double loc = this.location;
			int time = this.releaseFrame;
			for (int i = 0; i < p.size(); ++i) {
				Task current = p.get(i);
				int nextTime = Math.max(current.releaseFrame, time + (int)loc.distance(current.location));
				if (nextTime < current.retractFrame && nextTime < this.retractFrame) {
					loc = current.location;
					time = nextTime;
				}
				else {
					possible = false;
					break;
				}
			}
			if (!possible) continue;
			else return true;
		}
		return false;
	}
	
	public PTSs GetPTSSet() {
		return this.ptsSet;
	}
	
	public void FindPTSs(PTS prefix, ArrayList<Task> tasks) {
		this.ptsSet = this.GetPTSs(prefix, tasks);
	}
	
	private PTSs GetPTSs(PTS prefix, ArrayList<Task> tasks) {
		PTSs retPTSs = new PTSs();
		ArrayList<Task> tasks_c = new ArrayList<Task>(tasks);
		for (Task t : tasks) {
			tasks_c.remove(t);
			if (!this.Overlap(t)) continue;
			PTS pts = new PTS(prefix.list);
			pts.AddTask(t);
			if (IsPTS(pts)) {
				retPTSs.AddSubset(pts);
				if (pts.size() < this.maxNumberOfTasks && tasks_c.size() > 0) {
					PTSs newPTSs = this.GetPTSs(pts, tasks_c);
					retPTSs.addAll(newPTSs);
				}
			}
		}
		return retPTSs;
	}

	public Element Fill(Element w) {
		w = super.Fill(w);
		w.setAttribute("max", Integer.toString(maxNumberOfTasks));
		return w;
	}
}
