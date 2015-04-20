package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class MostFreeTime extends OnlineAlgorithm {

	public MostFreeTime(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid) {
		super(tasks, workers, grid);
	}
	
	@Override
	protected Worker SelectWorker(
			HashMap<Worker, ArrayList<Task>> eligibleWorkers, Task task) {
		Worker selectedWorker = null;
		Double maxFree = 0.0;
		for (Worker w : eligibleWorkers.keySet()) {
			Double freeTime = (double)w.retractFrame - w.GetCompleteTime(eligibleWorkers.get(w), currentFrame);
			if (freeTime > maxFree) {
				selectedWorker = w;
				maxFree = freeTime;
			}
		}
		return selectedWorker;
	}

}
