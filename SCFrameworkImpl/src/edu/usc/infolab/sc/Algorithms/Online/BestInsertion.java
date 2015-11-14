package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class BestInsertion extends SCOnlineAlgorithm {

	public BestInsertion(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid) {
		super(tasks, workers, grid);
	}
	
	@Override
	protected Worker SelectWorker(
			HashMap<Worker, ArrayList<Task>> eligibleWorkers, Task task) {
		Worker selectedWorker = null;
		Double minDiff = Double.MAX_VALUE;
		long maxTime = 0;
		for (Worker w : eligibleWorkers.keySet()) {
			Calendar start = Calendar.getInstance();
			Double diff = w.GetCompleteTime(eligibleWorkers.get(w), currentFrame) - w.GetCompleteTime(w.GetSchedule(), currentFrame);
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
}
