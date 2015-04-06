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
		Worker firstWorker = null;
		for (Worker w : availableWorkers) {
			task.assignmentStat.workerFreeTimes.add(w.retractFrame - w.GetCompleteTime(currentFrame).intValue());
			Log.Add(5, "Worker %d has %d tasks scheduled.", w.id, w.GetSchedule().size());
			ArrayList<Task> taskOrder = new ArrayList<Task>();
			if ((taskOrder = w.CanPerform(task, currentFrame)) != null )  {
				task.assignmentStat.eligibleWorkers++;
				if (firstWorker == null) {
					Log.Add(5, "\tWorker %d will perform the task", w.id);
					firstWorker = w;
					bestOrder = new ArrayList<Task>(taskOrder);
				}
			}
			Log.Add(5, "\tWorker %d cannot perform the task", w.id);
		}
		if (firstWorker != null) {
			task.AssignTo(firstWorker);
			firstWorker.AddTask(task);
			firstWorker.SetSchedule(bestOrder);
		}
		return firstWorker;
	}

}
