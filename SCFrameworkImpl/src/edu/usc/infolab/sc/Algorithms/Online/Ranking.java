package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Logging.Log;

public class Ranking extends OnlineAlgorithm {

	public Ranking(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid) {
		super(tasks, workers, grid);
	}
	
	@Override
	protected Worker AssignTask(Task task) {
		Log.Add(5, "Task %d:", task.id);
		ArrayList<Task> bestOrder = new ArrayList<Task>();
		for (Worker worker : availableWorkers) {
			Log.Add(5, "Worker %d has %d tasks scheduled.", worker.id, worker.GetSchedule().size());
			if ((bestOrder = worker.FastCanPerform(task, currentFrame)) != null )  {
				Log.Add(5, "\tWorker %d will perform the task", worker.id);
				task.AssignTo(worker);
				worker.AddTask(task);
				worker.SetSchedule(bestOrder);
				//Result.AssignedTasks++;
				//Result.GainedValue += task.value;
				return worker;
			}
			Log.Add(5, "\tWorker %d cannot perform the task", worker.id);
		}
		return null;
	}

}