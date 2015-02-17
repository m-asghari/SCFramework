package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Main.Result;

public class Greedy extends OnlineAlgorithm {

	public Greedy(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers) {
		super(tasks, workers);
	}
	
	@Override
	protected Boolean AssignTask(Task task) {
		ArrayList<Task> bestOrder = new ArrayList<Task>();
		for (Worker worker : availableWorkers) {
			if ((bestOrder = worker.CanPerform(task)) != null )  {
				task.AssignTo(worker);
				worker.AddTask(task);
				worker.SetSchedule(bestOrder);
				Result.AssignedTasks++;
				Result.GainedValue += task.value;
				return true;
			}
		}
		return false;
	}

}
