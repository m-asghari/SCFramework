package edu.usc.infolab.sc.Clairvoyant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.usc.infolab.sc.PTS;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Main.Log;

public class ExactAlgorithm {
	HashMap<Integer, Task> _tasks;
	HashMap<Integer, Worker> _workers;
	
	public ExactAlgorithm(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers) {
		_tasks = tasks;
		_workers = workers;
	}
	
	public void FindPTSs() {
		ArrayList<Task> tasks = new ArrayList<Task>(_tasks.values());
		ArrayList<Worker> workers = new ArrayList<Worker>(_workers.values());
		Collections.sort(workers);
		for (Worker w : workers) {
			//Log.Add(String.format("Starting FindPTSs for worker %d", w.id));
			w.FindPTSs(new PTS(), tasks);
			Log.Add(String.format("Finished FindPTSs for worker %d", w.id));			
		}
	}
	
	public void Run() {
		FindPTSs();
		
		Graph PTS_Graph = new Graph(new ArrayList<Worker>(_workers.values()));
		Log.Add("Finished Building Graph");
		ArrayList<Graph.Node> maxClique = PTS_Graph.FindMaxClique();
		
		for (Graph.Node n : maxClique) {
			Log.Add(n.toString());
			Worker w = _workers.get(n.workerId);
			for (Task t : n.pts.list) {
				w.AddTask(t);
				t.AssignTo(w);
			}
		}
	}
	
	public ArrayList<Graph.Node> FindMaxClique() {
		Graph PTS_Graph = new Graph(new ArrayList<Worker>(_workers.values()));
		
		// Initializing the clique search process
		int n = PTS_Graph.nodes.size() - 1;
		int currentMaxValue = PTS_Graph.nodes.get(n).value;
		ArrayList<Graph.Node> currentMax = new ArrayList<Graph.Node>();
		currentMax.add(PTS_Graph.nodes.get(n));
		PTS_Graph.maxCliqueSizes.put(n, currentMaxValue);
		
		for (int k = n - 1; k >= 0; k--) {
			Graph.Node currentNode = PTS_Graph.nodes.get(k);
			ArrayList<Graph.Node> fixedSet = new ArrayList<Graph.Node>();
			fixedSet.add(currentNode);
			
			ArrayList<Graph.Node> workingSet = new ArrayList<Graph.Node>();
			for (int l = k + 1; l < PTS_Graph.nodes.size(); ++l) {
				if (currentNode.Neighbors().contains(PTS_Graph.nodes.get(l)))
					workingSet.add(PTS_Graph.nodes.get(l));	
			}
			
			ArrayList<Graph.Node> clique = PTS_Graph.GetMaxClique(fixedSet, workingSet, currentMaxValue);
			if (clique != null) {
				int cliqueValue = 0;
				for (Graph.Node node : clique) cliqueValue += node.value;
				if (cliqueValue > PTS_Graph.maxCliqueSizes.get(k+1)) {
					currentMax = new ArrayList<Graph.Node>(clique);
					PTS_Graph.maxCliqueSizes.put(k, cliqueValue);
				}
				else {
					PTS_Graph.maxCliqueSizes.put(k, PTS_Graph.maxCliqueSizes.get(k+1));
				}
			}
		}
		
		return currentMax;
	}
}
