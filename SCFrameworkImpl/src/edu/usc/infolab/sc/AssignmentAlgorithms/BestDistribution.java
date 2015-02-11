package edu.usc.infolab.sc.AssignmentAlgorithms;

import java.util.ArrayList;

import edu.usc.infolab.sc.CountDistribution;
import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class BestDistribution extends TaskAssignmentAlgorithm {
	private CountDistribution distW;
	private CountDistribution distT;
	
	public BestDistribution(ArrayList<Task> tasks, ArrayList<Worker> workers, Object...args) {
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
		for (Worker w : workers) {
			if (w.CanPerform(task)) {
				double dist = Diff(w, task);
				if (dist < minDistance) {
					minWorker = w;
					minDistance = dist;
				}
			}
		}
		if (minWorker != null) {
			minWorker.AddTask(task);
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
