package edu.usc.infolab.sc.Clairvoyant;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Main.Log;
import edu.usc.infolab.sc.Main.Result;

public class Exact extends ClairvoyantAlgorithm{
	
	public Exact(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers) {
		super(tasks, workers);
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
				Result.AssignedTasks++;
				Result.GainedValue += t.value;
				w.AddTask(t);
				t.AssignTo(w);
			}
		}
	}
	
	/*public ArrayList<Graph.Node> FindMaxClique() {
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
	}*/
}
