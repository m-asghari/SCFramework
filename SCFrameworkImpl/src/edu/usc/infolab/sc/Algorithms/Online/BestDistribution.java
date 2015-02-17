package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.usc.infolab.sc.CountDistribution;
import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Main.Result;

public class BestDistribution extends OnlineAlgorithm {
	private CountDistribution distW;
	private CountDistribution distT;
	
	public BestDistribution(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Object...args) {
		super(tasks, workers);
		this.grid = (Grid)args[0];
		this.distT = (CountDistribution)args[1];
	}
	
	@Override
	public void AdvanceTime() {
		// Check to see if any worker becomes available in current
		while (!upcomingWorkers.isEmpty() && 
				upcomingWorkers.get(0).releaseFrame <= currentFrame) {
			availableWorkers.add(upcomingWorkers.get(0));
			upcomingWorkers.remove(0);
		}
		
		distW = new CountDistribution(grid.size(), true);
		for (Worker w : availableWorkers) {
			distW.Inc(grid.GetCell(w.location));
		}
				
		// Check to see if any task arrives in current frame
		while (!upcomingTasks.isEmpty() &&
				upcomingTasks.get(0).releaseFrame <= currentFrame) {
			if (AssignTask(upcomingTasks.get(0))) {
				//tasks.add(upcomingTasks.get(0));
				assignedTasksCntr++;
			}
			upcomingTasks.remove(0);
		}
				
		for (Iterator<Worker> it = availableWorkers.iterator(); it.hasNext();) {
			Worker worker = it.next();
			
			// What the worker has to do in current frame
			worker.UpdateLocation();
			
			// Check to see if any worker should be retracted in current frame
			if (worker.retractFrame == currentFrame) {
				it.remove();
			}
		}
	}
	
	@Override
	protected Boolean AssignTask(Task task) {
		double minDistance = Double.MAX_VALUE;
		Worker minWorker = null;
		ArrayList<Task> bestOrder = new ArrayList<Task>();
		for (Worker w : availableWorkers) {
			ArrayList<Task> taskOrder = new ArrayList<Task>();
			if ((taskOrder = w.CanPerform(task)) != null) {
				double dist = Diff(w, task);
				if (dist < minDistance) {
					minWorker = w;
					minDistance = dist;
					bestOrder = new ArrayList<Task>(taskOrder);
				}
			}
		}
		if (minWorker != null) {
			minWorker.SetSchedule(bestOrder);
			task.AssignTo(minWorker);
			minWorker.AddTask(task);
			Result.AssignedTasks++;
			Result.GainedValue += task.value;
			return true;
		}
		return false;
	}
	
	public double Diff(Worker w, Task t) {
		CountDistribution distW_c = new CountDistribution(distW.cellCount);
		distW_c.Dec(grid.GetCell(w.location));
		distW_c.Inc(grid.GetCell(t.location));
		return CountDistribution.JSD(distT, distW_c);
	}
}
