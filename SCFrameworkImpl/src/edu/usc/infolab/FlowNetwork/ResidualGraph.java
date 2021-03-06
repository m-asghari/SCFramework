package edu.usc.infolab.FlowNetwork;

import java.util.ArrayList;
import java.util.HashMap;

public class ResidualGraph extends Graph {
	private HashMap<String, REdge> rEdges;
	
	public ResidualGraph(Graph flowGraph) {
		super(new ArrayList<Node>(flowGraph.nodes.values()));
		rEdges = new HashMap<String, REdge>();
		for (Edge e : flowGraph.edges.values()) {
			if (e.flow > 0) {
				AddEdge(e.start, e.end, e.cost, e.flow, 1. * Integer.MAX_VALUE);
				AddEdge(e.end, e.start, -1 * e.cost, 0.0, e.flow);
			}
			else {
				AddEdge(e.start, e.end, e.cost, 0., 0.);
			}
		}
	}
	
	private void AddEdge(Node start, Node end, Double cost, Double flow, Double rCapacity) {
		REdge e = new REdge(start, end, cost, flow, rCapacity);
		rEdges.put(String.format("%d-%d", start.id, end.id), e);
		start.adjList.add(end.id);
	}
	
	@Override
	protected Double GetEdgeCost(String edgeID) {
		return rEdges.get(edgeID).GetReducedCost();
	}
	
	@Override
	protected ArrayList<Edge> GetEdges() {
		return new ArrayList<Edge>(rEdges.values());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Node n : nodes.values()) {
			sb.append(String.format("%d imbalace:%.1f, potential:%.1f", n.id, n.b, n.potential));
			sb.append("\n");
		}
		
		for (REdge re : rEdges.values()) {
			sb.append(String.format("%d-%d cost:%.1f, flow:%.1f, rCost:%f", re.start.id, re.end.id, re.cost, re.flow, re.GetReducedCost()));
			sb.append("\n");
		}
		return sb.toString();
	}
}
