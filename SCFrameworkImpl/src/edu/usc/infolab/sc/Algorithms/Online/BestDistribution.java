package edu.usc.infolab.sc.Algorithms.Online;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.CountDistribution;
import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Logging.Log;

public class BestDistribution extends OnlineAlgorithm {
	private CountDistribution distW;
	private CountDistribution distT;
	
	public BestDistribution(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid, Object...args) {
		super(tasks, workers, grid);
		this.distT = (CountDistribution)args[0];
	}
	
	@Override
	protected void UpdateWorkerDistribution() {
		distW = new CountDistribution(grid, true);
		for (Worker w : availableWorkers) {
			distW.Inc(grid.GetCell(w.location));
		}
	}
	
	@Override
	protected Worker AssignTask(Task task) {
		Log.Add(5, "Task %d:", task.id);
		Log.Add(5, distT.toString());
		Double maxInfluence = -2.0 * distT.GetMaxInfluence();
		Worker bestWorker = null;
		ArrayList<Task> bestOrder = new ArrayList<Task>();
		for (Worker w : availableWorkers) {
			ArrayList<Task> taskOrder = new ArrayList<Task>();
			if ((taskOrder = w.FastCanPerform(task, currentFrame)) != null) {
				double inf = MoveInfluence(w.location, task.location);
				Log.Add(5, "maxInf: %.2f, inf for worker %d -> %.2f", maxInfluence, w.id, inf);
				if (inf >= maxInfluence) {
					bestWorker = w;
					maxInfluence = inf;
					bestOrder = new ArrayList<>(taskOrder);
				}
			}
			else {
				Log.Add(5, "Worker %d cannot perform task %d", w.id, task.id);
			}
		}
		if (bestWorker != null) {
			Double diff = bestWorker.GetCompleteTime(bestOrder, currentFrame) - bestWorker.GetCompleteTime(bestWorker.GetSchedule(), currentFrame);
			Log.Add(5, "Assigned task %d to worker %d -> diff: %.2f", task.id, bestWorker.id, diff);
			bestWorker.SetSchedule(bestOrder);
			task.AssignTo(bestWorker);
			bestWorker.AddTask(task);
		}
		return bestWorker;
	}
	
	/*private double Diff(Worker w, Task t) {
		CountDistribution distW_c = new CountDistribution(grid, distW.cellCount);
		distW_c.Dec(grid.GetCell(w.location));
		distW_c.Inc(grid.GetCell(t.location));
		return CountDistribution.JSD(distT, distW_c);
	}*/
	
	private double MoveInfluence(Point2D.Double src, Point2D.Double dst) 
	{
		if (grid.GetCell(src) == grid.GetCell(dst)) return 0;
		double srcInf = distT.GetPointInfluence(src);
		double dstInf = distT.GetPointInfluence(dst);
		Log.Add(5, "\tSrcInf = %.2f, DstInf = %.2f", srcInf, dstInf);
		int srcCnt = (int)distW.cellCount[grid.GetCell(src)];
		int dstCnt = (int)distW.cellCount[grid.GetCell(dst)];
		Log.Add(5, "\tSrcCnt = %d, DstCnt = %d", srcCnt, dstCnt);
		double srcDelta = (srcCnt > 1) ? ((double)srcCnt / (srcCnt - 1)) - 1 : 2;
		double dstDelta = (dstCnt > 0) ? ((double)(dstCnt + 1) / dstCnt) - 1 : 2;
		Log.Add(5, "\tSrcDelta = %.2f, DstDelta = %.2f", srcDelta, dstDelta);
		return (dstDelta * dstInf) - (srcDelta * srcInf);
	}
}
