package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Main.Log;

public class BestInsertion extends OnlineAlgorithm {

	public BestInsertion(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers) {
		super(tasks, workers);
	}
	
	@Override
	protected Boolean AssignTask(Task task) {
		Log.Add(2, "Task %d:", task.id);
		Double minDiff = Double.MAX_VALUE;
		Worker bestWorker = null;
		ArrayList<Task> bestOrder = new ArrayList<Task>();
		for (Worker w : availableWorkers) {
			ArrayList<Task> taskOrder = new ArrayList<Task>();
			if ((taskOrder = w.CanPerform(task, currentFrame)) != null) {
				Log.Add(2, "\tWorker %d: currentCompleteTime: %.2f, futureCompleteTime: %.2f", w.id, w.GetCompleteTime(w.GetSchedule(), currentFrame), w.GetCompleteTime(taskOrder, currentFrame));
				Double diff = w.GetCompleteTime(taskOrder, currentFrame) - w.GetCompleteTime(w.GetSchedule(), currentFrame);
				Log.Add(2, "\tminDiff: %.2f, diff: %.2f", minDiff, diff);
				if (diff < minDiff) {
					bestWorker = w;
					minDiff = diff;
					bestOrder = new ArrayList<Task>(taskOrder);
				}
			}
		}
		if (bestWorker != null) {
			Log.Add(2, "Task %d assigned to worker %d", task.id, bestWorker.id);
			bestWorker.SetSchedule(bestOrder);
			bestWorker.AddTask(task);
			task.AssignTo(bestWorker);
			//Result.AssignedTasks++;
			//Result.GainedValue += task.value;
			return true;
		}
		return false;
	}

}
