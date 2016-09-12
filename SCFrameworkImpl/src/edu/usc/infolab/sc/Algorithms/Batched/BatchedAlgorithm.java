package edu.usc.infolab.sc.Algorithms.Batched;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Utils;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Algorithms.Algorithm;

public abstract class BatchedAlgorithm extends Algorithm {
	private class BatchStats {
		int numOfTasks;
		long processingTime;
		double assignmentRate;
		double avgDelay;
		
		public BatchStats() {
			numOfTasks = 0;
			processingTime = -1;
			assignmentRate = -1;
			avgDelay = 0;
		}
	}
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
		ArrayList<BatchStats> batchStatsList = new ArrayList<BatchStats>();
		int batchStart = 0;
		int batchEnd = 500;
		
		Collections.sort(upcomingTasks_);
		Collections.sort(upcomingWorkers_);
				
		ArrayList<Worker> availableWorkers = new ArrayList<Worker>();
		while (!upcomingTasks_.isEmpty()) {
			ArrayList<Task> currentBatch = new ArrayList<Task>();
			int cutOff = -1;
			BatchStats batchStats = new BatchStats();
			while (true) {
				if (!upcomingTasks_.isEmpty() && upcomingTasks_.get(0).releaseFrame <= batchEnd) {
					cutOff = upcomingTasks_.get(0).releaseFrame;
					upcomingTasks_.get(0).assignmentStat.delayedStart = batchEnd - upcomingTasks_.get(0).releaseFrame;
					batchStats.numOfTasks++;
					batchStats.avgDelay += (batchEnd - upcomingTasks_.get(0).releaseFrame);
					currentBatch.add(upcomingTasks_.remove(0));
				}
				else {
					break;
				}
			}
			batchStats.avgDelay /= batchStats.numOfTasks;
			
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
			int assignedTasks = ProcessBatch(currentBatch, availableWorkers, cutOff);
			Calendar end = Calendar.getInstance();
			long processTimesMillis = end.getTimeInMillis() - start.getTimeInMillis();
			for (Task t : currentBatch) {
				t.assignmentStat.totalTime = processTimesMillis / currentBatch.size();
			}
			long processTimes = processTimesMillis / 6000;
			batchStats.processingTime = processTimes;
			batchStats.assignmentRate = (double)assignedTasks/batchStats.numOfTasks;
			batchStart = cutOff;
			batchEnd += (processTimes < 15) ? 15 : processTimes;
			batchStatsList.add(batchStats);
			
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
		SaveBatchStats(batchStatsList);
		return 0;
	}
	
	private void SaveBatchStats(ArrayList<BatchStats> batchStatsList) {
		try {
			FileWriter fw = new FileWriter(String.format("BatchStats//BatchStats_%s.csv", Utils.PathFileFormatter.format(Calendar.getInstance().getTime())));
			BufferedWriter bw = new BufferedWriter(fw);
			
			int i = 1;
			for (BatchStats batchStats : batchStatsList) {
				bw.write(String.format("%d, %d, %d, %.2f, %.2f\n", i++, batchStats.numOfTasks, batchStats.processingTime, batchStats.assignmentRate, batchStats.avgDelay));
			}
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}

	protected abstract int ProcessBatch(ArrayList<Task> tasks, ArrayList<Worker> workers, int startTime);
}
