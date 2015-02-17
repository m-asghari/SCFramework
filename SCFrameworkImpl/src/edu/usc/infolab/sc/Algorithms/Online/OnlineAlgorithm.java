package edu.usc.infolab.sc.Algorithms.Online;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Algorithms.Algorithm;
import edu.usc.infolab.sc.Main.Log;

public abstract class OnlineAlgorithm extends Algorithm{
	Integer currentFrame;
	Integer assignedTasksCntr;
	Integer finishedTasksCntr;
	Double totalTraveledDistance;
	ArrayList<Worker> availableWorkers;
	//ArrayList<Task> tasks;
	ArrayList<Task> upcomingTasks;
	ArrayList<Worker> upcomingWorkers;
	protected Grid grid;
	
	public OnlineAlgorithm(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers) {
		super(tasks, workers);
		currentFrame = 0;
		this.upcomingTasks = new ArrayList<Task>(tasks.values());
		Collections.sort(upcomingTasks);
		this.upcomingWorkers = new ArrayList<Worker>(workers.values());
		Collections.sort(upcomingWorkers);
		availableWorkers = new ArrayList<Worker>();
		assignedTasksCntr = 0;
	}
	
	@Override
	public void Run() {
		while (!upcomingTasks.isEmpty()) {
			AdvanceTime();
			currentFrame++;
			Log.Add("Current Time Frame: %d", currentFrame);
		}
	}
	
	public void AdvanceTime() {
		// Check to see if any worker becomes available in current frame
		while (!upcomingWorkers.isEmpty() && 
				upcomingWorkers.get(0).releaseFrame <= currentFrame) {
			availableWorkers.add(upcomingWorkers.get(0));
			upcomingWorkers.remove(0);
		}
		
		// Check to see if any task arrives in current frame
		while (!upcomingTasks.isEmpty() &&
				upcomingTasks.get(0).releaseFrame <= currentFrame) {
			if (AssignTask(upcomingTasks.get(0))) {
				//tasks.add(upcomingTasks.get(0));
				assignedTasksCntr++;
			}
			upcomingTasks.remove(0);
		}
		
		for (Iterator<Worker> it = availableWorkers.iterator(); it.hasNext();) {
			Worker worker = it.next();
			
			// What the worker has to do in current frame
			worker.UpdateLocation();
			
			// Check to see if any worker should be retracted in current frame
			if (worker.retractFrame == currentFrame) {
				it.remove();
			}
		}
	}
	
	protected abstract Boolean AssignTask(Task task);
	
}
