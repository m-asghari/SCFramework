package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class NearestNeighbor extends OnlineAlgorithm {

	public NearestNeighbor(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid) {
		super(tasks, workers, grid);
	}
	
	@Override
	protected Worker SelectWorker(
			HashMap<Worker, ArrayList<Task>> eligibleWorkers, Task task) {
		Worker selectedWorker = null;
		Double minDistance = Double.MAX_VALUE;
		long maxTime = 0;
		for (Worker w : eligibleWorkers.keySet()) {
			Calendar start = Calendar.getInstance();
			Double dist = w.location.distance(task.location);
			if (dist < minDistance) {
				selectedWorker = w;
				minDistance = dist;
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
