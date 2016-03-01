package edu.usc.infolab.sc.Algorithms.Batched.FordFulkerson;

public class Test {

	public static void main(String[] args) {
		IntegralDirectedGraph<Node> graph = new IntegralDirectedGraph<>();
		Node s = new Node(1);
		Node n2 = new Node(2);
		Node n3 = new Node(3);
		Node n4 = new Node(4);
		Node n5 = new Node(5);
		Node t = new Node(6);
		
		graph.addNode(s);
		graph.addNode(t);
		graph.addNode(n2);
		graph.addNode(n3);
		graph.addNode(n4);
		graph.addNode(n5);
		
		graph.addEdge(s, n2, 10);
		graph.addEdge(s, n3, 10);
		graph.addEdge(n2, n3, 2);
		graph.addEdge(n2, n4, 4);
		graph.addEdge(n2, n5, 8);
		graph.addEdge(n3, n5, 9);
		graph.addEdge(n4, t, 10);
		graph.addEdge(n5, n4, 6);
		graph.addEdge(n5, t, 10);
		
		FlowNetwork<Node> flow = FordFulkerson.maxFlow(graph, s, t);
		System.out.print(flow.getEdge(n5, n4).getFlow());

	}

}
