package edu.usc.infolab.sc.Clairvoyant;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.PTS;
import edu.usc.infolab.sc.PTSs;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class Graph {
	
	public ArrayList<ArrayList<Node>> layers;
	public ArrayList<Node> nodes;
	public HashMap<Integer, Integer> maxCliqueSizes;
	
	public class Node {
		public int index;
		public PTS pts;
		public int workerId;
		public int ptsIndex;
		
		ArrayList<Node> adjList;
		
		public Node() {
			this.index = -1;
		}
		
		public Node(int w, int index, PTS pts) {
			this.workerId = w;
			this.ptsIndex = index;
			this.pts = new PTS(pts);
			this.index = -1;
		}
		
		public void AddNeighbor(Node n) {
			adjList.add(n);
		}
		
		public ArrayList<Node> Neighbors() {
			return adjList;
		}
		
		public int degree() {
			return adjList.size();
		}
		
		@Override
		public boolean equals(Object obj) {
			Node n = (Node)obj;
			if (this.workerId == n.workerId && this.ptsIndex == n.ptsIndex)
				return true;
			return false;
		}
	}
	
	private void AddNewNode(Node n){
		int index = 0;
		while (nodes.get(index).degree() < n.degree()) index++;
		nodes.add(index, n);
	}
	
	public Graph(ArrayList<Worker> workers) {
		nodes = new ArrayList<Graph.Node>();
		maxCliqueSizes = new HashMap<Integer, Integer>();
		layers = new ArrayList<ArrayList<Node>>();
		for (Worker w : workers) {
			PTSs p = new PTSs(w.GetPTSSet());
			ArrayList<Node> layer = new ArrayList<Node>();
			for (int i = 0; i < p.Size(); ++i) {
				Node newNode = new Node(w.id, i, p.GetSubset(i));
				layer.add(newNode);
				AddNewNode(newNode);
			}
			layers.add(layer);
		}
		
		for (int i = 0; i < nodes.size(); ++i) {
			nodes.get(i).index = i;
		}
		
		// Discovering Edges
		for (int i = 0; i < layers.size() - 1; ++i) {
			ArrayList<Node> layer_i = layers.get(i);
			for (int j = i + 1; j < layers.size(); ++j) {
				ArrayList<Node> layer_j = layers.get(j);
				for (int ii = 0; ii < layer_i.size(); ii++) {
					Node n_i = layer_i.get(ii);
					for (int jj = 0; jj < layer_j.size(); jj++) {
						Node n_j = layer_j.get(jj);
						Boolean connect = true;
						for (Task t : n_i.pts.list) {
							if (n_j.pts.list.contains(t)) {
								connect = false;
								break;
							}
						}
						if (connect) {
							n_i.AddNeighbor(n_j);
							n_j.AddNeighbor(n_i);
						}
					}
				}
			}
		}
		
		int last = nodes.size() - 1;
		maxCliqueSizes.put(last, nodes.get(last).pts.value);
	}
	
	public ArrayList<Node> GetMaxClique(ArrayList<Node> fixedSet, ArrayList<Node> workingSet, int lb) {
		ArrayList<Node> maxClique = null;
		int currentMaxSize = lb;
		
		int fixedValue = 0;
		for (Node n : fixedSet) fixedValue += n.pts.value;
		
		for (int i = 0; i < workingSet.size(); ++i) {
			Node fixed = workingSet.get(i);
			int upperBound = fixedValue + maxCliqueSizes.get(fixed.index);
			if (upperBound <= currentMaxSize) continue;
			ArrayList<Node> newFixedSet = new ArrayList<Graph.Node>(fixedSet);
			newFixedSet.add(fixed);
			
			ArrayList<Node> newWorkingSet = new ArrayList<Graph.Node>();
			int newWorkingValue = 0;
			for (int j = i + 1; j < workingSet.size(); ++j) {
				if (fixed.Neighbors().contains(workingSet.get(j))) {
					newWorkingSet.add(workingSet.get(j));
					newWorkingValue += workingSet.get(j).pts.value;
				}
			}
			if (fixedValue + fixed.pts.value + newWorkingValue <= currentMaxSize) continue;
			ArrayList<Node> clique = GetMaxClique(newFixedSet, newWorkingSet, currentMaxSize);
			if (clique != null) {
				int cliqueSize = 0;
				for (Node n : clique) cliqueSize += n.pts.value;
				if (cliqueSize > currentMaxSize) {
					currentMaxSize = cliqueSize;
					maxClique = new ArrayList<Graph.Node>(clique);
				}
			}
		}
		return maxClique;
	}

}
