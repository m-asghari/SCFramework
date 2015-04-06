package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class Random extends OnlineAlgorithm {

	public Random(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid) {
		super(tasks, workers, grid);
	}
	
	@Override
	protected Worker SelectWorker(
			HashMap<Worker, ArrayList<Task>> eligibleWorkers, Task task) {
		Worker selectedWorker = null;		
		java.util.Random rnd = new java.util.Random();
		int index = rnd.nextInt(eligibleWorkers.size());
		int count = 0;
		for (Worker w : eligibleWorkers.keySet()) {
			if (count == index) {
				selectedWorker = w;
				break;
			}
			count++;
		}
		return selectedWorker;
	}
}
