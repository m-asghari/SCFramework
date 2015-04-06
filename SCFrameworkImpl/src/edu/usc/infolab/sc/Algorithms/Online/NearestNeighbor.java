package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class NearestNeighbor extends OnlineAlgorithm {

	public NearestNeighbor(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid) {
		super(tasks, workers, grid);
	}
	
	@Override
	protected Worker SelectWorker(
			HashMap<Worker, ArrayList<Task>> eligibleWorkers, Task task) {
		Worker selectedWorker = null;
		Double minDistance = Double.MAX_VALUE;
		for (Worker w : eligibleWorkers.keySet()) {
			Double dist = w.location.distance(task.location);
			if (dist < minDistance) {
				selectedWorker = w;
				minDistance = dist;
			}
		}
		return selectedWorker;
	}
}
