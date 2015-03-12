package edu.usc.infolab.sc.Algorithms.Clairvoyant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.usc.infolab.sc.PTS;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Algorithms.Algorithm;
import edu.usc.infolab.sc.Logging.Log;

public abstract class ClairvoyantAlgorithm extends Algorithm {	
	public ClairvoyantAlgorithm(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers) {
		super(tasks, workers);
	}
	
	public void FindPTSs() {
		ArrayList<Task> tasks = new ArrayList<Task>(_tasks.values());
		ArrayList<Worker> workers = new ArrayList<Worker>(_workers.values());
		Collections.sort(workers);
		for (Worker w : workers) {
			//Log.Add(String.format("Starting FindPTSs for worker %d", w.id));
			w.FindPTSs(new PTS(), tasks);
			Log.Add(2, "Finished FindPTSs for worker %d", w.id);			
		}
	}
	
	public abstract void Run();

}
