package edu.usc.infolab.sc.Algorithms.Clairvoyant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import edu.usc.infolab.sc.PTS;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Main.Log;

public class GoodPTSFirst extends ClairvoyantAlgorithm {

	public GoodPTSFirst(HashMap<Integer, Task> tasks,
			HashMap<Integer, Worker> workers) {
		super(tasks, workers);
	}
	
	@Override
	public void Run() {
		HashMap<Worker, PTS> seletedPTSs = new HashMap<Worker, PTS>();
		
		ArrayList<Task> remainingTasks = new ArrayList<Task>(_tasks.values());
		ArrayList<Worker> remainingWorkers = new ArrayList<>(_workers.values());
		
		int it = 0;
		while (!remainingWorkers.isEmpty()) {
			Worker bestWorker = null;
			PTS bestPTS = null;
			int bestValue = Integer.MIN_VALUE;
			
			remainingTasks = SortTasks(remainingTasks);
			
			for (Worker w : remainingWorkers) {
				PTS pts = w.GetGoodPTS(remainingTasks);
				Log.Add(2, "Found Good PTS for worker %d in iteration %d", w.id, it);
				Log.Add(2, pts.toString());
				if (pts.value > bestValue) {
					bestPTS = pts;
					bestWorker = w;
					bestValue = pts.value;
				}
			}
			if (bestWorker != null) {
				seletedPTSs.put(bestWorker, bestPTS);
				remainingWorkers.remove(bestWorker);
				for (Task t : bestPTS.list)
					remainingTasks.remove(t);
			}
			else {
				break;
			}
			it++;
		}
		
		for (Entry<Worker, PTS> e : seletedPTSs.entrySet()) {
			Worker w = e.getKey();
			for (Task t : e.getValue().list) {
				w.AddTask(t);
				w.SetSchedule(w.CanPerform(t, 0));
				t.AssignTo(w);
				//Result.AssignedTasks++;
				//Result.GainedValue += t.value;
			}
			w.travledDistance = w.GetCompleteTime(w.GetSchedule(), 0);
		}

	}

	private ArrayList<Task> SortTasks(ArrayList<Task> tasks) {
		ArrayList<Task> retTasks = new ArrayList<Task>();
		
		while (!tasks.isEmpty()) {
			int maxV = Integer.MIN_VALUE;
			int maxI = -1;
			for (int i = 0; i < tasks.size(); ++i) {
				if (tasks.get(i).value > maxV) {
					maxV = tasks.get(i).value;
					maxI = i;
				}
			}
			retTasks.add(tasks.get(maxI));
			tasks.remove(maxI);
		}
		return retTasks;
	}

}
