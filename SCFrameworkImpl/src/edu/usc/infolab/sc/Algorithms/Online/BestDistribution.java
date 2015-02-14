package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.CountDistribution;
import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

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
		super.AdvanceTime();
		distW = new CountDistribution(grid.size(), true);
		for (Worker w : workers) {
			distW.Inc(grid.GetCell(w.location));
		}
	}
	
	@Override
	protected Boolean AssignTask(Task task) {
		double minDistance = Double.MAX_VALUE;
		Worker minWorker = null;
		ArrayList<Task> bestOrder = new ArrayList<Task>();
		for (Worker w : workers) {
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
			return true;
		}
		return false;
	}
	
	public double Diff(Worker w, Task t) {
		CountDistribution distW_c = distW;
		distW_c.Dec(grid.GetCell(w.location));
		distW_c.Inc(grid.GetCell(t.location));
		return CountDistribution.JSD(distT, distW_c);
	}
}
