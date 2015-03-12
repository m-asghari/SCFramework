package edu.usc.infolab.sc.Algorithms;

import java.util.HashMap;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public abstract class Algorithm {
	protected HashMap<Integer, Worker> _workers;
	protected HashMap<Integer, Task> _tasks;
	
	public Algorithm(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers) {
		this._tasks = new HashMap<>(tasks);
		this._workers = new HashMap<>(workers);
	}
	
	public abstract int Run();
}
