package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class NearestNeighbor extends OnlineAlgorithm {

	public NearestNeighbor(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers) {
		super(tasks, workers);
	}
	
	@Override
	protected Boolean AssignTask(Task task) {
		double minDistance = Double.MAX_VALUE;
		Worker minWorker = null;
		ArrayList<Task> bestOrder = new ArrayList<Task>();
		for (Worker worker : workers) {
			ArrayList<Task> taskOrder = new ArrayList<Task>();
			if ((taskOrder = worker.CanPerform(task)) != null) {
				if (worker.location.distance(task.location) < minDistance) {
					minWorker = worker;
					minDistance = worker.location.distance(task.location);
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

}
