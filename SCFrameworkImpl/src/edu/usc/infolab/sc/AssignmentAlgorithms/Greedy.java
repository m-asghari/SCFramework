package edu.usc.infolab.sc.AssignmentAlgorithms;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class Greedy extends TaskAssignmentAlgorithm {

	@Override
	protected Boolean AssignTask(Task task) {
		for (Worker worker : workers) {
			if (worker.CanPerform(task)) {
				task.AssignTo(worker);
				worker.AddTask(task);
				return true;
			}
		}
		return false;
	}

}
