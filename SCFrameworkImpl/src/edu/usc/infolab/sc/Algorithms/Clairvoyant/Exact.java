package edu.usc.infolab.sc.Algorithms.Clairvoyant;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Logging.Log;

public class Exact extends ClairvoyantAlgorithm{
	
	public Exact(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers) {
		super(tasks, workers);
	}
	
	public int Run() {
		FindPTSs();
		
		Graph PTS_Graph = new Graph(new ArrayList<Worker>(_workers.values()));
		Log.Add(1, "Finished Building Graph");
		
		ArrayList<Graph.Node> maxClique = PTS_Graph.FindMaxClique();
		
		Double cutOffTime = 0.0;
		for (Graph.Node n : maxClique) {
			Log.Add(0, n.toString());
			Worker w = _workers.get(n.workerId);
			for (Task t : n.pts.list) {
				w.AddTask(t);
				w.SetSchedule(w.CanPerform(t, 0));
				t.AssignTo(w);
			}
			w.travledDistance = w.GetCompleteTime(w.GetSchedule(), 0);
			cutOffTime = (w.travledDistance.compareTo(cutOffTime) > 0) ? w.travledDistance : cutOffTime;
		}
		
		return cutOffTime.intValue();
	}
}
