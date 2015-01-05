package edu.usc.infolab.sc.AssignmentAlgorithms;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class NearestNeighbor extends TaskAssignmentAlgorithm {

	@Override
	protected Boolean AssignTask(Task task) {
		double minDistance = Double.MAX_VALUE;
		Worker minWorker = null;
		for (Worker worker : workers) {
			if (worker.CanPerform(task)) {
				if (worker.location.distance(task.location) < minDistance) {
					minWorker = worker;
					minDistance = worker.location.distance(task.location);
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

}
