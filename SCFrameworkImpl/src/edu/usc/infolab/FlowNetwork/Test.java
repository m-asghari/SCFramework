package edu.usc.infolab.FlowNetwork;

import java.util.ArrayList;

public class Test {

	public static void main(String[] args) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		nodes.add(new Node(2));
		nodes.add(new Node(1));
		nodes.add(new Node(-1));
		nodes.add(new Node(-2));
		
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges.add(new Edge(nodes.get(0), nodes.get(2), 1));
		edges.add(new Edge(nodes.get(0), nodes.get(3), 2));
		edges.add(new Edge(nodes.get(1), nodes.get(2), 2));
		edges.add(new Edge(nodes.get(1), nodes.get(3), 1));
		
		Graph graph = new Graph(nodes, edges);
		graph.FindMinFlow();
		
		for (Edge e : graph.edges.values()) {
			System.out.println(String.format("Start: %d, End: %d, Flow: %.1f", e.start.id, e.end.id, e.flow));
		}
	}

}
