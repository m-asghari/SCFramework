package edu.usc.infolab.sc.AssignmentAlgorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public abstract class TaskAssignmentAlgorithm {
	Integer currentFrame;
	Integer assignedTasksCntr;
	Integer finishedTasksCntr;
	Double totalTraveledDistance;
	ArrayList<Worker> workers;
	ArrayList<Task> tasks;
	ArrayList<Task> upcomingTasks;
	ArrayList<Worker> upcomingWorkers;
	protected Grid grid;
	
	public TaskAssignmentAlgorithm(ArrayList<Task> tasks, ArrayList<Worker> workers) {
		currentFrame = 0;
		this.upcomingTasks = new ArrayList<Task>(tasks);
		Collections.sort(upcomingTasks);
		this.upcomingWorkers = new ArrayList<Worker>(workers);
		Collections.sort(upcomingWorkers);
	}
	
	public void AdvanceTime() {
		// Check to see if any worker becomes available in current frame
		while (upcomingWorkers.get(0).releaseFrame <= currentFrame) {
			workers.add(upcomingWorkers.get(0));
			upcomingWorkers.remove(0);
		}
		
		// Check to see if any task arrives in current frame
		while (upcomingTasks.get(0).releaseFrame <= currentFrame) {
			if (AssignTask(upcomingTasks.get(0))) {
				tasks.add(upcomingTasks.get(0));
				assignedTasksCntr++;
			}
			upcomingTasks.remove(0);
		}
		
		for (Iterator<Worker> it = workers.iterator(); it.hasNext();) {
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
