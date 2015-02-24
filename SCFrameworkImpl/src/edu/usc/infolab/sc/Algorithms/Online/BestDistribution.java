package edu.usc.infolab.sc.Algorithms.Online;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.usc.infolab.sc.CountDistribution;
import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Main.Log;

public class BestDistribution extends OnlineAlgorithm {
	private CountDistribution distW;
	private CountDistribution distT;
	
	public BestDistribution(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Object...args) {
		super(tasks, workers);
		this.grid = (Grid)args[0];
		this.distT = (CountDistribution)args[1];
	}
	
	@Override
	public void AdvanceTime() {
		// Check to see if any worker becomes available in current
		while (!upcomingWorkers.isEmpty() && 
				upcomingWorkers.get(0).releaseFrame <= currentFrame) {
			availableWorkers.add(upcomingWorkers.get(0));
			upcomingWorkers.remove(0);
		}
		
		distW = new CountDistribution(grid, true);
		for (Worker w : availableWorkers) {
			distW.Inc(grid.GetCell(w.location));
		}
				
		// Check to see if any task arrives in current frame
		while (!upcomingTasks.isEmpty() &&
				upcomingTasks.get(0).releaseFrame <= currentFrame) {
			//distT.Inc(grid.GetCell(upcomingTasks.get(0).location));
			if (AssignTask(upcomingTasks.get(0))) {
				//tasks.add(upcomingTasks.get(0));
				assignedTasksCntr++;
			}
			upcomingTasks.remove(0);
		}
				
		for (Iterator<Worker> it = availableWorkers.iterator(); it.hasNext();) {
			Worker worker = it.next();
			
			// What the worker has to do in current frame
			worker.UpdateLocation(1);
			
			// Check to see if any worker should be retracted in current frame
			if (worker.retractFrame == currentFrame) {
				it.remove();
			}
		}
	}
	
	
	/*@Override
	protected Boolean AssignTask(Task task) {
		double minDistance = Double.MAX_VALUE;
		Worker minWorker = null;
		ArrayList<Task> bestOrder = new ArrayList<Task>();
		for (Worker w : availableWorkers) {
			ArrayList<Task> taskOrder = new ArrayList<Task>();
			if ((taskOrder = w.CanPerform(task)) != null) {
				double dist = Diff(w, task);
				if (dist < minDistance) {
					minWorker = w;
					minDistance = dist;
					bestOrder = new ArrayList<Task>(taskOrder);
				}
			}
		}
		if (minWorker != null) {
			minWorker.SetSchedule(bestOrder);
			task.AssignTo(minWorker);
			minWorker.AddTask(task);
			Result.AssignedTasks++;
			Result.GainedValue += task.value;
			return true;
		}
		return false;
	}*/
	
	@Override
	protected Boolean AssignTask(Task task) {
		Log.Add(2, "Task %d:", task.id);
		Log.Add(2, distT.toString());
		double maxInfluence = 0;
		Worker bestWorker = null;
		ArrayList<Task> bestOrder = new ArrayList<Task>();
		for (Worker w : availableWorkers) {
			ArrayList<Task> taskOrder = new ArrayList<Task>();
			if ((taskOrder = w.CanPerform(task, currentFrame)) != null) {
				double inf = MoveInfluence(w.location, task.location);
				Log.Add(2, "\tmaxInf: %.2f, inf for worker %d -> %.2f", maxInfluence, w.id, inf);
				if (inf >= maxInfluence) {
					bestWorker = w;
					maxInfluence = inf;
					bestOrder = new ArrayList<>(taskOrder);
				}
			}
		}
		if (bestWorker != null) {
			Log.Add(2, "Assigned task %d to worker %d", task.id, bestWorker.id);
			bestWorker.SetSchedule(bestOrder);
			task.AssignTo(bestWorker);
			bestWorker.AddTask(task);
			//Result.AssignedTasks++;
			//Result.GainedValue += task.value;
			return true;
		}
		return false;
	}
	
	public double Diff(Worker w, Task t) {
		CountDistribution distW_c = new CountDistribution(grid, distW.cellCount);
		distW_c.Dec(grid.GetCell(w.location));
		distW_c.Inc(grid.GetCell(t.location));
		return CountDistribution.JSD(distT, distW_c);
		//return CountDistribution.SKLD(distT, distW_c);
	}
	
	public double MoveInfluence(Point2D.Double src, Point2D.Double dst) 
	{
		if (grid.GetCell(src) == grid.GetCell(dst)) return 0;
		double srcInf = distT.GetPointInfluence(src);
		double dstInf = distT.GetPointInfluence(dst);
		Log.Add(2, "\t\tSrcInf = %.2f, DstInf = %.2f", srcInf, dstInf);
		int srcCnt = (int)distW.cellCount[grid.GetCell(src)];
		int dstCnt = (int)distW.cellCount[grid.GetCell(dst)];
		Log.Add(2, "\t\tSrcCnt = %d, DstCnt = %d", srcCnt, dstCnt);
		double srcDelta = (srcCnt > 1) ? ((double)srcCnt / (srcCnt - 1)) - 1 : 2;
		double dstDelta = (dstCnt > 0) ? ((double)(dstCnt + 1) / dstCnt) - 1 : 2;
		Log.Add(2, "\t\tSrcDelta = %.2f, DstDelta = %.2f", srcDelta, dstDelta);
		return (dstDelta * dstInf) - (srcDelta * srcInf);
	}
}
