package edu.usc.infolab.sc.Algorithms.Online;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Logging.Log;

public class BestDistributionAdhoc extends BestDistribution {

	public BestDistributionAdhoc(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid, Object...args) {
		super(tasks, workers, grid, args);
	}
	
	public BestDistributionAdhoc(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid) {
		super(tasks, workers, grid);
	}
	
	@Override
	protected Worker SelectWorker(HashMap<Worker,ArrayList<Task>> eligibleWorkers, Task task) {
		Worker selectedWorker = null;
		if (updateDistT) distT.Inc(grid.GetCell(task.location));
		Double maxInfluence = -2.0 * distT.GetMaxInfluence();
		long maxTime = 0;
		for (Worker w : eligibleWorkers.keySet()) {
			Calendar start = Calendar.getInstance();
			double inf = MoveInfluence(w.location, task.location);
			if (inf >= maxInfluence) {
				selectedWorker = w;
				maxInfluence = inf;
			}
			Calendar end = Calendar.getInstance();
			long time = end.getTimeInMillis() - start.getTimeInMillis();
			if (time > maxTime)
				maxTime = time;
		}
		task.assignmentStat.selectWorkerTime = maxTime;
		return selectedWorker;
	}
	
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
