package edu.usc.infolab.sc.Clairvoyant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.usc.infolab.sc.PTS;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Main.Log;

public abstract class ClairvoyantAlgorithm {
	HashMap<Integer, Task> _tasks;
	HashMap<Integer, Worker> _workers;
	
	public ClairvoyantAlgorithm(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers) {
		_tasks = tasks;
		_workers = workers;
	}
	
	public void FindPTSs() {
		ArrayList<Task> tasks = new ArrayList<Task>(_tasks.values());
		ArrayList<Worker> workers = new ArrayList<Worker>(_workers.values());
		Collections.sort(workers);
		for (Worker w : workers) {
			//Log.Add(String.format("Starting FindPTSs for worker %d", w.id));
			w.FindPTSs(new PTS(), tasks);
			Log.Add(String.format("Finished FindPTSs for worker %d", w.id));			
		}
	}
	
	public abstract void Run();

}
