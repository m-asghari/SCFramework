package edu.usc.infolab.sc.Algorithms.Online;

import java.util.HashMap;

import edu.usc.infolab.sc.CountDistribution;
import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public abstract class BestDistribution extends SCOnlineAlgorithm {
	protected CountDistribution distW;
	protected CountDistribution distT;
	protected Boolean updateDistT;
	
	public BestDistribution(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid, Object...args) {
		super(tasks, workers, grid);
		this.distT = (CountDistribution)args[0];
		updateDistT = false;
	}
	
	public BestDistribution(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid) {
		super(tasks, workers, grid);
		this.distT = new CountDistribution(grid, false);
		updateDistT = true;
	}
	
	@Override
	protected void UpdateWorkerDistribution() {
		distW = new CountDistribution(grid, true);
		for (Worker w : availableWorkers) {
			int availabilty = w.GetAvailability(currentFrame).intValue();
			for (int i = 0; i < availabilty; i++) {
				distW.Inc(grid.GetCell(w.location));
			}
		}
	}
}
