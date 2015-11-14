package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Logging.Log;

public abstract class SCOnlineAlgorithm extends OnlineAlgorithm {

	public SCOnlineAlgorithm(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid) {
		super(tasks, workers, grid);
	}
	
	protected Worker AssignTask(Task task) {
		Log.Add(5, "Task %d:", task.id);
		HashMap<Worker, ArrayList<Task>> eligibleWorkers = new HashMap<Worker, ArrayList<Task>>();
		
		long worstDecideEligibility = 0;
		for (Worker w : availableWorkers) {
			task.assignmentStat.workerFreeTimes.add(w.retractFrame - w.GetCompleteTime(currentFrame).intValue());
			task.assignmentStat.availableWorkers++;
			Log.Add(5, "Worker %d has %d tasks scheduled.", w.id, w.GetSchedule().size());
			Calendar startDecideEligibility = Calendar.getInstance();
			ArrayList<Task> taskOrder = w.CanPerform(task, currentFrame);
			if (taskOrder != null )  {
				task.assignmentStat.eligibleWorkers++;
				eligibleWorkers.put(w, new ArrayList<Task>(taskOrder));
				Log.Add(5, "\tWorker %d can perform the task", w.id);
			}
			Calendar endDecideEligibility = Calendar.getInstance();
			long time = endDecideEligibility.getTimeInMillis() - startDecideEligibility.getTimeInMillis();
			if (time > worstDecideEligibility)
				worstDecideEligibility = time;
			Log.Add(5, "\tWorker %d cannot perform the task", w.id);
		}
		task.assignmentStat.decideEligibilityTime = worstDecideEligibility;
		Worker selectedWorker = SelectWorker(eligibleWorkers, task);
		if (selectedWorker != null) {
			task.assignmentStat.assigned = 1;
			task.AssignTo(selectedWorker);
			selectedWorker.AddTask(task);
			selectedWorker.SetSchedule(eligibleWorkers.get(selectedWorker));
			presentTasks.add(task);
		}
		else {
			Log.Add(1, "Task %d not assigned", task.id);
		}
		return selectedWorker;
	}
	
	@Override
	protected Worker SelectWorker(
			HashMap<Worker, ArrayList<Task>> eligibleWorkers, Task task) {
		// TODO Auto-generated method stub
		return null;
	}

}
