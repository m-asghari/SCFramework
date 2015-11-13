package edu.usc.infolab.FlowNetwork;

import java.util.ArrayList;

public class Test {

	public static void main(String[] args) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		nodes.add(new Node(2));
		nodes.add(new Node(1));
		nodes.add(new Node(-1));
		nodes.add(new Node(-2));
		/*nodes.add(new Node(-53.3966));
		nodes.add(new Node(-46.0365));
		nodes.add(new Node(18.0060));
		nodes.add(new Node(16.2721));
		nodes.add(new Node(2.4675));
		nodes.add(new Node(11.2371));
		nodes.add(new Node(22.8076));
		nodes.add(new Node(5.5018));
		nodes.add(new Node(23.1410));*/
		
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges.add(new Edge(nodes.get(0), nodes.get(2), 1));
		edges.add(new Edge(nodes.get(0), nodes.get(3), 2));
		edges.add(new Edge(nodes.get(1), nodes.get(2), 2));
		edges.add(new Edge(nodes.get(1), nodes.get(3), 1));
		/*edges.add(new Edge(nodes.get(3), nodes.get(0), 212.13));
		edges.add(new Edge(nodes.get(3), nodes.get(1), 192.09));
		edges.add(new Edge(nodes.get(7), nodes.get(0), 403.61));
		edges.add(new Edge(nodes.get(7), nodes.get(1), 384.19));
		edges.add(new Edge(nodes.get(5), nodes.get(0), 256.32));
		edges.add(new Edge(nodes.get(5), nodes.get(1), 247.39));
		edges.add(new Edge(nodes.get(8), nodes.get(0), 335.41));
		edges.add(new Edge(nodes.get(8), nodes.get(1), 331.36));
		edges.add(new Edge(nodes.get(2), nodes.get(0), 161.55));
		edges.add(new Edge(nodes.get(2), nodes.get(1), 134.16));
		edges.add(new Edge(nodes.get(4), nodes.get(0), 342.05));
		edges.add(new Edge(nodes.get(4), nodes.get(1), 318.90));
		edges.add(new Edge(nodes.get(6), nodes.get(0), 366.20));
		edges.add(new Edge(nodes.get(6), nodes.get(1), 349.86));*/
		
		Graph graph = new Graph(nodes, edges);
		graph.FindMinFlow();
		
		for (Edge e : graph.edges.values()) {
			System.out.println(String.format("Start: %d, End: %d, Flow: %.1f", e.start.id, e.end.id, e.flow));
		}
	}

}
