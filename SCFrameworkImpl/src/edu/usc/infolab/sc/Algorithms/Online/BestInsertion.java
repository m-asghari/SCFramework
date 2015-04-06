package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class BestInsertion extends OnlineAlgorithm {

	public BestInsertion(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid) {
		super(tasks, workers, grid);
	}
	
	@Override
	protected Worker SelectWorker(
			HashMap<Worker, ArrayList<Task>> eligibleWorkers, Task task) {
		Worker selectedWorker = null;
		Double minDiff = Double.MAX_VALUE;
		for (Worker w : eligibleWorkers.keySet()) {
			Double diff = w.GetCompleteTime(eligibleWorkers.get(w), currentFrame) - w.GetCompleteTime(w.GetSchedule(), currentFrame);
			if (diff < minDiff) {
				selectedWorker = w;
				minDiff = diff;
			}
		}
		return selectedWorker;
	}
}
