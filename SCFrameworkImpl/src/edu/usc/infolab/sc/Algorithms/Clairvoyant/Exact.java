package edu.usc.infolab.sc.Algorithms.Clairvoyant;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Main.Log;

public class Exact extends ClairvoyantAlgorithm{
	
	public Exact(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers) {
		super(tasks, workers);
	}
	
	public void Run() {
		FindPTSs();
		
		Graph PTS_Graph = new Graph(new ArrayList<Worker>(_workers.values()));
		Log.Add(1, "Finished Building Graph");
		
		ArrayList<Graph.Node> maxClique = PTS_Graph.FindMaxClique();
		
		for (Graph.Node n : maxClique) {
			Log.Add(1, n.toString());
			Worker w = _workers.get(n.workerId);
			for (Task t : n.pts.list) {
				w.AddTask(t);
				w.SetSchedule(w.CanPerform(t, 0));
				t.AssignTo(w);
			}
			w.travledDistance = w.GetCompleteTime(w.GetSchedule(), 0);
		}
	}
}
