package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class Ranking extends OnlineAlgorithm {

	public Ranking(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid) {
		super(tasks, workers, grid);
	}
	
	@Override
	protected Worker SelectWorker(HashMap<Worker, ArrayList<Task>> eligibleWorkers, Task task) {
		Worker selectedWorker = null;
		int minId = Integer.MAX_VALUE;
		for (Worker w : eligibleWorkers.keySet()) {
			if (w.id < minId) {
				selectedWorker = w;
				minId = w.id;
			}
		}
		return selectedWorker;
	}

}
