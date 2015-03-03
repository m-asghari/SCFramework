package edu.usc.infolab.sc;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.w3c.dom.Element;

import edu.usc.infolab.sc.Main.Log;

public class Worker extends SpatialEntity{
	public static Integer idCntr = 0;
	//private Boolean active;
	private ArrayList<Task> assignedTasks;
	private ArrayList<Task> remainingTasks;
	public Integer maxNumberOfTasks;
	public Double travledDistance;
	private PTSs ptsSet;
	
	private Worker(Worker w) {
		super(w);
		this.assignedTasks = new ArrayList<Task>(w.assignedTasks);
		this.remainingTasks = new ArrayList<Task>(w.remainingTasks);
		this.maxNumberOfTasks = w.maxNumberOfTasks;
		this.travledDistance = w.travledDistance;
		this.ptsSet = w.ptsSet.clone();
	}
		
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
		//active = false;
	}
	
	public ArrayList<Task> GetAssignedTasks() {
		return this.assignedTasks;
	}
	
	public void AddTask(Task task) {
		assignedTasks.add(task);
	}
	
	public ArrayList<Task> GetSchedule() {
		return this.remainingTasks;
	}
	
	public void SetSchedule(ArrayList<Task> tasks) {
		this.remainingTasks = new ArrayList<Task>(tasks);
	}
	

	public PTSs GetPTSSet() {
		return this.ptsSet;
	}
	
	public Element Fill(Element w) {
		w = super.Fill(w);
		w.setAttribute("max", Integer.toString(maxNumberOfTasks));
		return w;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Worker %d:\n", this.id));
		sb.append(super.toString());
		sb.append("Assigned Tasks:");
		for (Task t : assignedTasks)
			sb.append(String.format("t%d, ",t.id));
		sb.append("\nRemaining Tasks:");
		for (Task t : remainingTasks)
			sb.append(String.format("t%d, ",  t.id));
		sb.append(String.format("\nMax: %d\n", maxNumberOfTasks));
		sb.append(String.format("Traveled: %.2f\n", travledDistance));
		return sb.toString();
	}
	
	@Override
	public Worker clone(){
		return new Worker(this);
	}
	
	public ArrayList<Task> FastCanPerform(Task task, int currentFrame) {
		if (assignedTasks.size() == maxNumberOfTasks) {
			return null;
		}
		ArrayList<Task> tasks = new ArrayList<Task>(remainingTasks);
		tasks.add(task);
		Pair<ArrayList<Task>, Double> bestResult = CanPerform(new ArrayList<Task>(), tasks, currentFrame, null, Double.MAX_VALUE);
		if (bestResult.First != null && bestResult.First.size() == tasks.size())
			return bestResult.First;
		else
			return null;
	}
	
	public ArrayList<Task> CanPerform(Task task, int currentFrame) {
		ArrayList<Task> bestOrder = new ArrayList<Task>();
		Double bestTime = Double.MAX_VALUE;
		if (assignedTasks.size() == maxNumberOfTasks) {
			return null;
		}
		ArrayList<Task> tasks = new ArrayList<Task>(remainingTasks);
		tasks.add(task);
		ArrayList<ArrayList<Task>> taskPerms = Utils.Permutations(tasks);
		for (ArrayList<Task> p : taskPerms) {
			Double time = Double.MAX_VALUE;
			if ((time = CanComplete(p, currentFrame)) < bestTime) {
				bestOrder = new ArrayList<Task>(p);
				bestTime = time;
			}
		}
		return (bestOrder.size() > 0) ? bestOrder : null;
	}
	
	public Double GetCompleteTime(ArrayList<Task> tasks, int currentFrame) {
		return CanComplete(tasks, currentFrame);
	}
	
	private Pair<ArrayList<Task>, Double> CanPerform(ArrayList<Task> fixed, ArrayList<Task> remaining, int currentFrame,
			ArrayList<Task> bestOrder, Double bestTime) {
		for (Task t : remaining) {
			ArrayList<Task> newFixed = new ArrayList<Task>(fixed);
			newFixed.add(t);
			Double time = CanComplete(newFixed, currentFrame);
			if (time.compareTo(bestTime) < 0) {
				/*if (remaining.size() == 1) {
					return new Pair<ArrayList<Task>, Double>(newFixed, time);
				}*/
				ArrayList<Task> newRemaining = new ArrayList<Task>(remaining);
				newRemaining.remove(t);
				if (newRemaining.size() == 0) {
					return new Pair<ArrayList<Task>, Double>(newFixed, time);
				}
				Pair<ArrayList<Task>, Double> result = CanPerform(newFixed, newRemaining, currentFrame, bestOrder, bestTime);
				if (result.Second.compareTo(bestTime) < 0) {
					bestOrder = new ArrayList<>(result.First);
					bestTime = result.Second;
				}
			}
		}
		return new Pair<ArrayList<Task>, Double>(bestOrder, bestTime);
	}
	
	private Double CanComplete(ArrayList<Task> tasks, int currentFrame) {
		StringBuilder sb = new StringBuilder();
		for (Task t : tasks) sb.append(String.format("t%d, ", t.id));
		Log.Add(6, "Worker: %d, currentFrame: %d, inputTasks: %s", this.id, currentFrame, sb.toString());
		Point2D.Double loc = this.location;
		double time = Math.max(this.releaseFrame, currentFrame);
		Log.Add(6,  "initLoc: x=%.2f y=%.2f, initTime: %.2f", loc.x, loc.y, time);
		for (int i = 0; i < tasks.size(); ++i) {
			Task current = tasks.get(i);
			Log.Add(6, "currentTask: t%d, distance: %.2f", current.id, loc.distance(current.location));
			double nextTime = Math.max((double)current.releaseFrame, time + loc.distance(current.location));
			Log.Add(6, "nextTime: %.2f", nextTime);
			if (nextTime <= (double)current.retractFrame && nextTime <= (double)this.retractFrame) {
				loc = current.location;
				time = nextTime;
				Log.Add(6,  "Loc: x=%.2f y=%.2f, Time: %.2f", loc.x, loc.y, time);
			}
			else {
				Log.Add(6, "return: %.2f", Double.MAX_VALUE);
				return Double.MAX_VALUE;
			}
		}
		Log.Add(6, "return: %.2f", time);
		return time;
	}
	
	private Boolean CanComplete(Task task, int currentFrame) {
		ArrayList<Task> taskArr = new ArrayList<Task>();
		taskArr.add(task);
		return (CanComplete(taskArr, currentFrame) != Double.MAX_VALUE);
	}
	
	public Boolean IsPTS(PTS pts, int currentFrame) {
		ArrayList<ArrayList<Task>> taskPerms = Utils.Permutations(pts.list);
		for (ArrayList<Task> p : taskPerms) {
			if (CanComplete(p, currentFrame) != Double.MAX_VALUE)
				return true;
		}
		return false;
	}
	
	//Methods for Online Algorithms
	public ArrayList<Task> UpdateLocation(double length) {
		ArrayList<Task> finished = new ArrayList<Task>();
		if (remainingTasks.isEmpty()) return finished;
		Point2D.Double dest = remainingTasks.get(0).location;
		Double dist = location.distance(dest);
		if (dist > length) {
			MoveToward(dest, length);
			travledDistance += length;
		}
		else {
			// TODO(masghari): set assignedTask.get(0) as Done!
			finished.add(remainingTasks.get(0));
			
			// TODO(masghari): remove done task from the list!
			this.location = dest;
			remainingTasks.remove(0);
			travledDistance += dist;
			
			// Start moving toward new task
			if (remainingTasks.size() > 0 ) {
				finished.addAll(UpdateLocation(length - dist));
			}
		}
		return finished;
	}
	
	private void MoveToward(Point2D dest, Double length) {
		Double dist = location.distance(dest);
		if (dist < length)
			length = dist;
		Double deltaX = length * (dest.getX() - location.getX()) / dist;
		Double newX = location.getX() + deltaX;
		Double deltaY = length * (dest.getY() - location.getY()) / dist;
		Double newY = location.getY() + deltaY;
		location.setLocation(newX, newY);
	}
	
	// Methods for Offline Algorithms
	public PTSs FindPTSs(PTS prefix, ArrayList<Task> tasks) {
		PTSs result = this.GetPTSs(prefix, tasks);
		this.ptsSet = result;
		return result;
	}
	
	public PTS GetGoodPTS(ArrayList<Task> tasks) {
		PTS retPTS = new PTS();
		for (Task t : tasks) {
			PTS pts = new PTS(retPTS);
			pts.AddTask(t);
			if (IsPTS(pts, 0)) {
				retPTS.AddTask(t);
				if (retPTS.size() == this.maxNumberOfTasks)
					break;
			}
		}
		return retPTS;
	}
	
	public PTS GetBestPTS(ArrayList<Task> tasks) {
		ArrayList<Task> possibleTasks = new ArrayList<>();
		int posValue = 0;
		for (Task t : tasks) {
			if (this.CanComplete(t, 0)) {
				possibleTasks.add(t);
				posValue += t.value;
			}
		}
		return GetBestPTS(new PTS(), possibleTasks, posValue, Integer.MIN_VALUE);
	}
	
	private PTS GetBestPTS(PTS prefix, ArrayList<Task> remaining, int rValue, int bestValue) {
		PTS retPTS = null;
		
		ArrayList<Task> remaining_c = new ArrayList<>(remaining);
		for (Task t : remaining) {
			remaining_c.remove(t);
			rValue -= t.value;
			PTS pts = new PTS(prefix);
			pts.AddTask(t);
			if (IsPTS(pts, 0)) {
				if (pts.value > bestValue) {
					bestValue = pts.value;
					retPTS = new PTS(pts);
				}
				if (pts.size() < this.maxNumberOfTasks && remaining_c.size() > 0 
					&& pts.value + rValue > bestValue) {
					PTS newPTS = this.GetBestPTS(pts, remaining_c, rValue, bestValue);
					if (newPTS != null && newPTS.value > bestValue) {
						bestValue = newPTS.value;
						retPTS = new PTS(newPTS);
					}
				}
			}
		}
		return retPTS;
	}
	
	private PTSs GetPTSs(PTS prefix, ArrayList<Task> tasks) {
		PTSs retPTSs = new PTSs();
		ArrayList<Task> tasks_c = new ArrayList<Task>(tasks);
		for (Task t : tasks) {
			tasks_c.remove(t);
			if (!this.Overlap(t)) continue;
			PTS pts = new PTS(prefix.list);
			pts.AddTask(t);
			if (IsPTS(pts, 0)) {
				retPTSs.AddSubset(pts);
				if (pts.size() < this.maxNumberOfTasks && tasks_c.size() > 0) {
					PTSs newPTSs = this.GetPTSs(pts, tasks_c);
					retPTSs.addAll(newPTSs);
				}
			}
		}
		return retPTSs;
	}
}