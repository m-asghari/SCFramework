package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import edu.usc.infolab.sc.CountDistribution;
import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class BestDistributionEMD extends BestDistribution {
	
	public BestDistributionEMD(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid, Object...args) {
		super(tasks, workers, grid, args);
	}
	
	public BestDistributionEMD(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid) {
		super(tasks, workers, grid);
	}
	
	@Override
	protected Worker SelectWorker(HashMap<Worker,ArrayList<Task>> eligibleWorkers, Task task) {
		Worker selectedWorker = null;
		if (updateDistT) distT.Inc(grid.GetCell(task.location));
		Double minDiff = 1.0 * Integer.MAX_VALUE;
		long maxTime = 0;
		for (Worker w : eligibleWorkers.keySet()) {
			Calendar start = Calendar.getInstance();
			double diff = EMDDiff(w, eligibleWorkers.get(w), task);
			if (diff < minDiff) {
				selectedWorker = w;
				minDiff = diff;
			}
			Calendar end = Calendar.getInstance();
			long time = end.getTimeInMillis() - start.getTimeInMillis();
			if (time > maxTime)
				maxTime = time;
		}
		task.assignmentStat.selectWorkerTime = maxTime;
		return selectedWorker;
	}

	private Double EMDDiff(Worker w, ArrayList<Task> taskOrder, Task t) {
		CountDistribution distW_c = new CountDistribution(grid, distW.cellCount);
		int currAvailabity = w.GetAvailability(currentFrame).intValue();
		int nextAvailabity = w.retractFrame - w.GetCompleteTime(taskOrder, currentFrame).intValue();
		Task lastTask = taskOrder.get(taskOrder.size()-1);
		distW_c.Dec(grid.GetCell(w.location), currAvailabity);
		distW_c.Inc(grid.GetCell(lastTask.location), nextAvailabity);
		return CountDistribution.EMD(distW_c, distT);
	}
}
