package edu.usc.infolab.FlowNetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Graph {
	protected HashMap<Integer, Node> nodes;
	protected HashMap<String, Edge> edges;
	
	public Graph(ArrayList<Node> nodes, ArrayList<Edge> edges) {
		this.nodes = new HashMap<Integer, Node>();
		this.edges = new HashMap<String, Edge>();
		for (Node n : nodes) {
			this.nodes.put(n.id, n);
		}
		for (Edge e : edges) {
			AddEdge(e);
		}
	}
	
	protected Graph(ArrayList<Node> nodes) {
		this.nodes = new HashMap<Integer, Node>();
		for (Node n : nodes) {
			this.nodes.put(n.id, n);
		}
	}
	
	public void AddEdge(Edge e) {
		edges.put(String.format("%d-%d", e.start.id, e.end.id), e);
		e.start.adjList.add(e.end.id);
		e.start.outEdges.add(e);
		e.end.inEdges.add(e);
	}
	
	public void AddEdge(Node start, Node end, Double cost) {
		Edge e = new Edge(start, end, cost);
		AddEdge(e);
	}
	
	public void FindMinFlow() {
		Double maxB = 0.;
		for (Node n : nodes.values()) {
			if (Math.abs(n.b) > maxB) {
				maxB = Math.abs(n.b);
			}
		}
		double delta = Math.pow(2, Math.ceil(Math.log(maxB)/Math.log(2)));
		while (delta >= 1) {
			ArrayList<Integer> S_delta = GetS(delta);
			ArrayList<Integer> T_delta = GetT(delta);
			while (S_delta.size() != 0 && T_delta.size() != 0) {
				Node k = nodes.get(S_delta.remove(0));
				Node v = nodes.get(T_delta.remove(0));
				ResidualGraph residualGraph = new ResidualGraph(this);
				HashMap<Integer, Path> shortestPaths = residualGraph.ComputeAllShortestPaths(k);
				for (Entry<Integer, Path> e : shortestPaths.entrySet()) {
					nodes.get(e.getKey()).potential = nodes.get(e.getKey()).potential - e.getValue().distance;
				}
				if (shortestPaths.containsKey(v.id)) {
					Path kvPath = shortestPaths.get(v.id);
					for (int i = 0; i < kvPath.nodes.size() - 1; i++) {
						int start = kvPath.nodes.get(i);
						int end = kvPath.nodes.get(i+1);
						AddFlow(start, end, delta);
					}
				}
				S_delta = GetS(delta);
				T_delta = GetT(delta);				
			}
			delta = delta / 2;
		}
	}
	
	private void AddFlow(int start, int end, Double f) {
		String edgeID = GetEdgeID(start, end);
		if (edges.containsKey(edgeID)) {
			edges.get(edgeID).flow += f;
		}
		else {
			edges.get(GetEdgeID(end, start)).flow -= f;
		}
	}
	
	protected String GetEdgeID(Integer s, Integer e) {
		return String.format("%d-%d", s, e);
	}
	
	protected ArrayList<Integer> GetS(Double delta) {
		ArrayList<Integer> retList = new ArrayList<Integer>();
		for (Node n : nodes.values()) {
			if (n.GetImbalance() >= delta) {
				retList.add(n.id);
			}
		}
		return retList;
	}
	
	protected ArrayList<Integer> GetT(Double delta) {
		ArrayList<Integer> retList = new ArrayList<Integer>();
		for (Node n : nodes.values()) {
			if (n.GetImbalance() <= -1.0 * delta) {
				retList.add(n.id);
			}
		}
		return retList;
	}
	
	protected HashMap<Integer, Path> ComputeAllShortestPaths(Node start) {
		HashMap<Integer, Double> distances = new HashMap<Integer, Double>();
		HashMap<Integer, Integer> preds = new HashMap<Integer, Integer>();
		for (Node n : nodes.values()) {
			distances.put(n.id, 1.0 * Integer.MAX_VALUE);
			preds.put(n.id, null);
		}
		distances.put(start.id, 0.);
		
		for (int round = 1; round < nodes.size(); round++) {
			for (Edge e : GetEdges()) {
				Double cost = GetEdgeCost(String.format("%d-%d", e.start.id, e.end.id));
				if (distances.get(e.start.id) + cost < distances.get(e.end.id)) {
					distances.put(e.end.id, distances.get(e.start.id) + cost);
					preds.put(e.end.id, e.start.id);
				}
			}
		}
		
		for (Edge e : GetEdges()) {
			Double cost = GetEdgeCost(String.format("%d-%d", e.start.id, e.end.id));
			if (distances.get(e.start.id) + cost < distances.get(e.end.id)) {
				System.out.println("Graph Contains Negative Cycle");
				return null;
			}
		}
		
		HashMap<Integer, Path> shortestPaths = new HashMap<Integer, Path>();
		for (Node n : nodes.values()) {
			if (preds.get(n.id) != null) {
				ArrayList<Integer> path = new ArrayList<Integer>();
				Integer current = n.id;
				while (current != null) {
					path.add(0, current);
					current = preds.get(current);
				}
				Path p = new Path();
				p.nodes = new ArrayList<Integer>(path);
				p.distance = distances.get(n.id);
				shortestPaths.put(n.id, p);
			}
		}
		return shortestPaths;
	}
	
	protected ArrayList<Edge> GetEdges() {
		return new ArrayList<Edge>(edges.values());
	}
	
	protected Double GetEdgeCost(String edgeID) {
		return edges.get(edgeID).cost;
	}
	
}
