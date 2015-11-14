package edu.usc.infolab.FlowNetwork;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

public class Graph {
	public HashMap<Integer, Node> nodes;
	public HashMap<String, Edge> edges;
	
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
	
	private void SimplexMethod() {
		ArrayList<Edge> edgesArr = new ArrayList<Edge>(edges.values());
		ArrayList<Node> nodesArr = new ArrayList<Node>(nodes.values());
		double[] coef = new double[edgesArr.size()];
		for (int i = 0; i < edgesArr.size(); i++) {
			//System.out.print(edgesArr.get(i).GetID());
			coef[i] = edgesArr.get(i).cost;
		}
		//System.out.print("\n");
		LinearObjectiveFunction f = new LinearObjectiveFunction(coef, 0);
		ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
		for (int i = 0; i < nodesArr.size(); i++) {
			//System.out.println("Node: " + nodesArr.get(i).toString());
			double[] Av = new double[edgesArr.size()];
			for (int j = 0; j < edgesArr.size(); j++) {
				if (edgesArr.get(j).start.id == i) {
					//Av[j] = edgesArr.get(j).cost;
					Av[j] = 1;
				}
				else if (edgesArr.get(j).end.id == i) {
					//Av[j] = edgesArr.get(j).cost * -1.0;
					Av[j] = -1;
				}
				else {
					Av[j] = 0;
				}
			}
			//Print("Coef", Av);
			constraints.add(new LinearConstraint(Av, Relationship.EQ, nodesArr.get(i).b));
		}
		
		SimplexSolver solver = new SimplexSolver();
		PointValuePair optSolution = solver.optimize(
				new MaxIter(100),
				f,
				new LinearConstraintSet(constraints),
				GoalType.MINIMIZE,
				new NonNegativeConstraint(true));
		
		double[] solution = optSolution.getPoint();
		for (int i = 0; i < edgesArr.size(); i++) {
			edgesArr.get(i).flow = solution[i];
		}
		for (Edge e : edgesArr) {
			edges.get(String.format("%d-%d", e.start.id, e.end.id)).flow = e.flow;
		}
		
	}
	
	public void Print(String string, double[] av) {
		StringBuilder sb = new StringBuilder();
		sb.append(string + ": ");
		for (int i = 0; i < av.length; i++) {
			sb.append(Double.toString(av[i]) + ", ");
		}
		System.out.println(sb.toString());
	}

	public void FindMinFlow() {
		SimplexMethod();
		/*Double maxB = 0.;
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
		}*/
	}
	
	/*private void AddFlow(int start, int end, Double f) {
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
	}*/
	
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
		HashMap<Integer, Integer> distances = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> preds = new HashMap<Integer, Integer>();
		for (Node n : nodes.values()) {
			distances.put(n.id, Integer.MAX_VALUE);
			preds.put(n.id, null);
		}
		distances.put(start.id, 0);		
		
		for (int round = 1; round < nodes.size(); round++) {
			for (Edge e : GetEdges()) {
				BigDecimal dStart = new BigDecimal(distances.get(e.start.id));
				BigDecimal dEnd = new BigDecimal(distances.get(e.end.id));
				BigDecimal cost = new BigDecimal(GetEdgeCost(String.format("%d-%d", e.start.id, e.end.id)));
				if (dStart.add(cost).compareTo(dEnd) < 0) {
					distances.put(e.end.id, dStart.add(cost).intValue());
					preds.put(e.end.id, e.start.id);
				}
			}
		}
		
		for (Edge e : GetEdges()) {
			BigDecimal dStart = new BigDecimal(distances.get(e.start.id));
			BigDecimal dEnd = new BigDecimal(distances.get(e.end.id));
			BigDecimal cost = new BigDecimal(GetEdgeCost(String.format("%d-%d", e.start.id, e.end.id)));
			if (dStart.add(cost).compareTo(dEnd) < 0) {
				System.out.println(String.format("Processing edge %s", e.toString()));
				System.out.println("Graph Contains Negative Cycle");
				System.out.println(this.toString());
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
