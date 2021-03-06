package edu.usc.infolab.sc.Algorithms.Clairvoyant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import edu.usc.infolab.sc.PTS;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class BestPTSFirst extends ClairvoyantAlgorithm{
	HashMap<Worker, ArrayList<PTS>> _allPTSs;
	
	public BestPTSFirst(HashMap<Integer, Task> tasks,
			HashMap<Integer, Worker> workers) {
		super(tasks, workers);
		_allPTSs = new HashMap<Worker, ArrayList<PTS>>();
	}

	@Override
	public int Run() {
		HashMap<Worker, PTS> seletedPTSs = new HashMap<Worker, PTS>();
		
		ArrayList<Task> remainingTasks = new ArrayList<Task>(_tasks.values());
		ArrayList<Worker> remainingWorkers = new ArrayList<>(_workers.values());
		
		while (!remainingWorkers.isEmpty()) {
			Worker bestWorker = null;
			PTS bestPTS = null;
			int bestValue = Integer.MIN_VALUE;
			for (Worker w : remainingWorkers) {
				PTS pts = w.GetBestPTS(remainingTasks);
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
		}
		
		/*FindPTSs();
		
		for (Worker w : _workers.values()) {
			_allPTSs.put(w, new ArrayList<PTS>(w.GetPTSSet().GetList()));
		}
		
		ArrayList<Task> assignedTasks = new ArrayList<Task>();
		while (true) {
			Pair<Worker, PTS> r = FindMaxPTS();
			if (r.x == null) break;
			
			for (Task t : r.y.list) {
				assignedTasks.add(t);
			}
			
			seletedPTSs.put(r.x, r.y);
			
			Log.Add("Worker: %d, PTS: %s", r.x.id, r.y.toString());
			
			UpdateSortedPTSs(r.x, assignedTasks);
		}*/
		
		Double cutOffTime = 0.0;
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
			cutOffTime = (w.travledDistance.compareTo(cutOffTime) > 0) ? w.travledDistance : cutOffTime;
		}
		
		return cutOffTime.intValue();
	}

	/*private void UpdateSortedPTSs(Worker w, ArrayList<Task> assignedTasks) {
		_allPTSs.put(w, new ArrayList<PTS>());
		for (Entry<Worker, ArrayList<PTS>> e : _allPTSs.entrySet()) {
			ArrayList<PTS> currentList = e.getValue();
			int pts = 0;
			while (pts < currentList.size()) {
				if (currentList.get(pts).Contains(assignedTasks))
					currentList.remove(pts);
				else
					pts++;
			}
		}		
	}

	private Pair<Worker, PTS> FindMaxPTS() {
		int maxValue = Integer.MIN_VALUE;
		PTS maxPTS = null;
		Worker maxWorker = null;
		for (Entry<Worker, ArrayList<PTS>> e : _allPTSs.entrySet()) {
			if (e.getValue().size() > 0) {
				PTS newPTS = Collections.max(e.getValue());
				if (newPTS.value > maxValue) {
					maxValue = newPTS.value;
					maxWorker = e.getKey();
					maxPTS = newPTS;
				}
			}
		}
		return new Pair<Worker, PTS>(maxWorker, maxPTS);
	}*/
	
	

}
