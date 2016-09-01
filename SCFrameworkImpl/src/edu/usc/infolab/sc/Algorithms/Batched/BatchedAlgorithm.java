package edu.usc.infolab.sc.Algorithms.Batched;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Algorithms.Algorithm;

public abstract class BatchedAlgorithm extends Algorithm {
	private ArrayList<Task> upcomingTasks_;
	private ArrayList<Worker> upcomingWorkers_;
	
	public BatchedAlgorithm(HashMap<Integer, Task> tasks,
			HashMap<Integer, Worker> workers) {
		super(tasks, workers);
		upcomingTasks_ = new ArrayList<>(tasks.values());
		upcomingWorkers_ = new ArrayList<>(workers.values());
	}	
	
	@Override
	public int Run() {
		int batchStart = 0;
		int batchEnd = 500;
		
		Collections.sort(upcomingTasks_);
		Collections.sort(upcomingWorkers_);
				
		ArrayList<Worker> availableWorkers = new ArrayList<Worker>();
		while (!upcomingTasks_.isEmpty()) {
			ArrayList<Task> currentBatch = new ArrayList<Task>();
			int cutOff = -1;
			while (true) {
				if (!upcomingTasks_.isEmpty() && upcomingTasks_.get(0).releaseFrame <= batchEnd) {
					cutOff = upcomingTasks_.get(0).releaseFrame;
					upcomingTasks_.get(0).assignmentStat.delayedStart = batchEnd - upcomingTasks_.get(0).releaseFrame;
					currentBatch.add(upcomingTasks_.remove(0));
				}
				else {
					break;
				}
			}
			
			Iterator<Worker> availableIt = availableWorkers.iterator();
			while (availableIt.hasNext()) {
				Worker w = availableIt.next();
				if (w.retractFrame < batchStart) {
					availableIt.remove();
				}
			}
			while (true) {
				if (!upcomingWorkers_.isEmpty() && upcomingWorkers_.get(0).releaseFrame < cutOff)
					availableWorkers.add(upcomingWorkers_.remove(0));
				else {
					break;
				}
			}
			
			Calendar start = Calendar.getInstance();
			ProcessBatch(currentBatch, availableWorkers, cutOff);
			Calendar end = Calendar.getInstance();
			long processTimes = (end.getTimeInMillis() - start.getTimeInMillis()) / 1000;
			batchStart = cutOff;
			batchEnd += (processTimes < 60) ? 60 : processTimes;
			
			for (int time = 0; time < processTimes; time++) {
				for (Iterator<Worker> it = availableWorkers.iterator(); it.hasNext();) {
					Worker worker = it.next();
					
					// What the worker has to do in current frame
					worker.UpdateLocation(1);
										
					if (worker.retractFrame.equals(cutOff + time)) {
						it.remove();
					}
				}
			}
		}
		return 0;
	}

	protected abstract void ProcessBatch(ArrayList<Task> tasks, ArrayList<Worker> workers, int startTime);
}
