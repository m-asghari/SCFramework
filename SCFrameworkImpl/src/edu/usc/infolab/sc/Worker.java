package edu.usc.infolab.sc;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.w3c.dom.Element;

import edu.usc.infolab.sc.Main.Log;

public class Worker extends SpatialEntity{
	public static Integer idCntr = 0;
	public Boolean active;
	public ArrayList<Task> assignedTasks;
	private ArrayList<Task> remainingTasks;
	public Integer maxNumberOfTasks;
	Double travledDistance;
	PTSs ptsSet;
		
	public Worker() {
		Initialize();
		this.id = idCntr++;
	}
	
	public Worker(Element e) {
		super(e);
		Initialize();
		this.id = idCntr++;
		this.maxNumberOfTasks = Integer.parseInt(e.getAttribute("max"));
	}
	
	private void Initialize() {
		ptsSet = new PTSs();
		assignedTasks = new ArrayList<Task>();
		remainingTasks = new ArrayList<Task>();
		travledDistance = 0.0;
		active = false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Worker %d:\n", this.id));
		sb.append(super.toString());
		sb.append("Assigned Tasks:");
		for (Task t : assignedTasks)
			sb.append(String.format("t%d, ",t.id));
		sb.append(String.format("\nMax: %d\n", maxNumberOfTasks));
		sb.append(String.format("Traveled: %.2f\n", travledDistance));
		return sb.toString();
	}
	
	public void UpdateLocation() {
		Point2D.Double dest = remainingTasks.get(0).location;
		Double dist = location.distance(dest);
		if (dist > 1) {
			MoveToward(dest, 1.0);
			travledDistance += 1.0;
		}
		else if (assignedTasks.size() > 1 ){
			// TODO(masghari): set assignedTask.get(0) as Done!
			
			// TODO(masghari): remove done task from the list!
			remainingTasks.remove(0);
			
			// Start moving toward new task
			dest = remainingTasks.get(0).location;
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
	
	public void SetSchedule(ArrayList<Task> tasks) {
		this.remainingTasks = new ArrayList<Task>(tasks);
	}
	
	private void MoveToward(Point2D dest, Double length) {
		Double dist = location.distance(dest);
		Double deltaX = length * (dest.getX() - location.getX()) / dist;
		Double newX = location.getX() + deltaX;
		Double deltaY = length * (dest.getY() - location.getY()) / dist;
		Double newY = location.getY() + deltaY;
		location.setLocation(newX, newY);
	}
	
	public ArrayList<Task> CanPerform(Task task) {
		if (assignedTasks.size() == maxNumberOfTasks) {
			return null;
		}
		ArrayList<Task> tasks = new ArrayList<Task>(remainingTasks);
		tasks.add(task);
		ArrayList<ArrayList<Task>> taskPerms = Utils.Permutations(tasks);
		for (ArrayList<Task> p : taskPerms) {
			if (CanComplete(p))
				return p;
		}
		return null;
	}
	
	private Boolean CanComplete(ArrayList<Task> tasks) {
		Point2D.Double loc = this.location;
		int time = this.releaseFrame;
		for (int i = 0; i < tasks.size(); ++i) {
			Task current = tasks.get(i);
			int nextTime = Math.max(current.releaseFrame, time + (int)loc.distance(current.location));
			if (nextTime <= current.retractFrame && nextTime <= this.retractFrame) {
				loc = current.location;
				time = nextTime;
			}
			else {
				return false;
			}
		}
		return true;
	}
	
	public Boolean IsPTS(PTS pts) {
		ArrayList<ArrayList<Task>> taskPerms = Utils.Permutations(pts.list);
		for (ArrayList<Task> p : taskPerms) {
			if (CanComplete(p))
				return true;
		}
		return false;
	}
	
	public PTSs GetPTSSet() {
		return this.ptsSet;
	}
	
	public PTSs FindPTSs(PTS prefix, ArrayList<Task> tasks) {
		PTSs result = this.GetPTSs(prefix, tasks);
		this.ptsSet = result;
		Log.Add(String.format("Visited Nodes: %d", this.nodeCount));
		Log.Add(String.format("Found PTSs: %d", this.ptsSet.Size()));
		return result;
	}
	
	private int nodeCount = 0;
	
	private PTSs GetPTSs(PTS prefix, ArrayList<Task> tasks) {
		PTSs retPTSs = new PTSs();
		ArrayList<Task> tasks_c = new ArrayList<Task>(tasks);
		for (Task t : tasks) {
			this.nodeCount++;
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