package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Logging.Log;

public class Random extends OnlineAlgorithm {

	public Random(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid) {
		super(tasks, workers, grid);
	}
	
	@Override
	protected Worker AssignTask(Task task) {
		Log.Add(5, "Task %d:", task.id);
		ArrayList<Task> bestOrder = new ArrayList<Task>();
		ArrayList<Worker> potentialWorkers = new ArrayList<Worker>();
		for (Worker worker : availableWorkers) {
			Log.Add(5, "Worker %d has %d tasks scheduled.", worker.id, worker.GetSchedule().size());
			if (worker.FastCanPerform(task, currentFrame) != null )  {
				task.eligibleWorkers++;
				potentialWorkers.add(worker);
			}
			Log.Add(5, "\tWorker %d cannot perform the task", worker.id);
		}
		java.util.Random rnd = new java.util.Random();
		Worker rndWorker = (potentialWorkers.size() > 0) ? potentialWorkers.get(rnd.nextInt(potentialWorkers.size())) : null;
		bestOrder = rndWorker.FastCanPerform(task, currentFrame);
		
		if (rndWorker != null) {
			task.AssignTo(rndWorker);
			rndWorker.AddTask(task);
			rndWorker.SetSchedule(bestOrder);
		}
		return rndWorker;
	}

}
