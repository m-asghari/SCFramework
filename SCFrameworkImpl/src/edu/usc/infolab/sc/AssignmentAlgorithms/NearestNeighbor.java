package edu.usc.infolab.sc.AssignmentAlgorithms;

import java.util.ArrayList;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class NearestNeighbor extends TaskAssignmentAlgorithm {

	public NearestNeighbor(ArrayList<Task> tasks, ArrayList<Worker> workers) {
		super(tasks, workers);
	}
	
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
