package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Logging.Log;

public class NearestNeighbor extends OnlineAlgorithm {

	public NearestNeighbor(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid) {
		super(tasks, workers, grid);
	}
	
	@Override
	protected Worker AssignTask(Task task) {
		Log.Add(5, "Task %d:", task.id);
		double minDistance = Double.MAX_VALUE;
		Worker minWorker = null;
		ArrayList<Task> bestOrder = new ArrayList<Task>();
		for (Worker worker : availableWorkers) {
			Log.Add(5, "Worker: %s", worker.toString());
			ArrayList<Task> taskOrder = new ArrayList<Task>();
			if ((taskOrder = worker.CanPerform(task, currentFrame)) != null) {
				Log.Add(5, "\tminDistance: %.2f, worker %d distance: %.2f", minDistance, worker.id, worker.location.distance(task.location));
				task.eligibleWorkers++;
				if (worker.location.distance(task.location) < minDistance) {
					minWorker = worker;
					minDistance = worker.location.distance(task.location);
					bestOrder = new ArrayList<Task>(taskOrder);
				}
			} else {
				Log.Add(5, "\ttaskOrder for Worker %d is null", worker.id);
			}
		}
		if (minWorker != null) {
			Log.Add(5, "\ttask %d assigned to Worker %d", task.id, minWorker.id);
			minWorker.SetSchedule(bestOrder);
			minWorker.AddTask(task);
			task.AssignTo(minWorker);
		}
		return minWorker;
	}

}
