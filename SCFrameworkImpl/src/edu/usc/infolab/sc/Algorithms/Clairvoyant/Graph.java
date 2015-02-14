package edu.usc.infolab.sc.Algorithms.Clairvoyant;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.PTS;
import edu.usc.infolab.sc.PTSs;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Main.Log;

public class Graph {
	
	public ArrayList<ArrayList<Node>> layers;
	public ArrayList<Node> nodes;
	public HashMap<Integer, Integer> maxCliqueSizes;
	
	public class Node {
		public int index;
		public int value;
		public PTS pts;
		public int workerId;
		public int ptsIndex;
		
		ArrayList<Node> adjList;
		
		public Node() {
			this.index = -1;
			adjList = new ArrayList<Node>();
		}
		
		public Node(int index, int value) {
			this.index = index;
			this.value = value;
			this.workerId = -1;
			this.ptsIndex = -1;
			this.pts = new PTS();
			adjList = new ArrayList<Node>();
		}
		
		public Node(int w, int index, PTS pts) {
			this.workerId = w;
			this.ptsIndex = index;
			this.pts = new PTS(pts);
			this.value = pts.value;
			this.index = -1;
			adjList = new ArrayList<Node>();
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
			//if (this.workerId == n.workerId && this.ptsIndex == n.ptsIndex)
			if (this.index == n.index)
				return true;
			return false;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("Node%d:\n", index));
			sb.append(pts.toString());
			sb.append(String.format("Worker: %d\n", workerId));
			sb.append(String.format("ptsIndex: %d\n", ptsIndex));
			sb.append("Adjacent Nodes: ");
			//for (Node n : adjList) sb.append(String.format("Node%d, ", n.index));
			sb.append(String.format("\nValue: %d\n", value));
			return sb.toString();
		}
	}
	
	public Graph() {
		layers = new ArrayList<ArrayList<Node>>();
		nodes = new ArrayList<Node>();
		maxCliqueSizes = new HashMap<Integer, Integer>();
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
		maxCliqueSizes.put(last, nodes.get(last).value);
	}
	
	private void AddNewNode(Node n){
		if (nodes.size() == 0) {
			nodes.add(n);
			return;
		}
		int index = 0;
		while (nodes.get(index).degree() < n.degree()) index++;
		nodes.add(index, n);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int l = 0; l < layers.size(); l++) {
			sb.append(String.format("Layer %d:\n", l));
			for (Node n : layers.get(l)) sb.append(n.toString());
			sb.append("\n");
		}
		sb.append("\n");
		return sb.toString();
	}
	
	public ArrayList<Graph.Node> FindMaxClique() {
		// Initializing the clique search process
		int n = this.nodes.size() - 1;
		int currentMaxValue = this.nodes.get(n).value;
		ArrayList<Node> currentMax = new ArrayList<Node>();
		currentMax.add(this.nodes.get(n));
		this.maxCliqueSizes.put(n, currentMaxValue);
		
		for (int k = n - 1; k >= 0; k--) {
			Node currentNode = this.nodes.get(k);
			ArrayList<Node> fixedSet = new ArrayList<Node>();
			fixedSet.add(currentNode);
			
			ArrayList<Node> workingSet = new ArrayList<Node>();
			for (int l = k + 1; l < this.nodes.size(); ++l) {
				if (currentNode.Neighbors().contains(this.nodes.get(l)))
					workingSet.add(this.nodes.get(l));	
			}
			
			ArrayList<Graph.Node> clique = this.GetMaxClique(fixedSet, workingSet, currentMaxValue);
			if (clique != null) {
				int cliqueValue = 0;
				for (Node node : clique) cliqueValue += node.value;
				if (cliqueValue > this.maxCliqueSizes.get(k+1)) {
					currentMax = new ArrayList<Graph.Node>(clique);
					this.maxCliqueSizes.put(k, cliqueValue);
				}
				else {
					this.maxCliqueSizes.put(k, this.maxCliqueSizes.get(k+1));
				}
			}
			Log.Add("Found MaxClique for node %d", currentNode.index);
		}
		return currentMax;
	}
	
	public ArrayList<Node> GetMaxClique(ArrayList<Node> fixedSet, ArrayList<Node> workingSet, int lb) {
		ArrayList<Node> maxClique = new ArrayList<Node>(fixedSet);
		int currentMaxSize = lb;
		
		int fixedValue = 0;
		for (Node n : fixedSet) fixedValue += n.value;
		
		for (int i = 0; i < workingSet.size(); ++i) {
			Node fixed = workingSet.get(i);
			int upperBound = fixedValue + maxCliqueSizes.get(fixed.index);
			if (upperBound <= currentMaxSize) continue;
			ArrayList<Node> newFixedSet = new ArrayList<Node>(fixedSet);
			newFixedSet.add(fixed);
			
			ArrayList<Node> newWorkingSet = new ArrayList<Node>();
			int newWorkingValue = 0;
			for (int j = i + 1; j < workingSet.size(); ++j) {
				if (fixed.Neighbors().contains(workingSet.get(j))) {
					newWorkingSet.add(workingSet.get(j));
					newWorkingValue += workingSet.get(j).value;
				}
			}
			if (fixedValue + fixed.value + newWorkingValue <= currentMaxSize) continue;
			ArrayList<Node> clique = GetMaxClique(newFixedSet, newWorkingSet, currentMaxSize);
			if (clique != null) {
				int cliqueSize = 0;
				for (Node n : clique) cliqueSize += n.value;
				if (cliqueSize > currentMaxSize) {
					currentMaxSize = cliqueSize;
					maxClique = new ArrayList<Graph.Node>(clique);
				}
			}
		}
		return maxClique;
	}

}
