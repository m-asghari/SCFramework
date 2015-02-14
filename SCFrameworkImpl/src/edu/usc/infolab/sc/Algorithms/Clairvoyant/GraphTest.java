package edu.usc.infolab.sc.Algorithms.Clairvoyant;

import java.util.ArrayList;

public class GraphTest {

	public static void main(String[] args) {
		Graph graph = new Graph();
		
		// Creating Nodes
		graph.nodes.add(graph.new Node(0, 1));
		graph.nodes.add(graph.new Node(1, 1));
		graph.nodes.add(graph.new Node(2, 1));
		graph.nodes.add(graph.new Node(3, 3));
		graph.nodes.add(graph.new Node(4, 4));
		graph.nodes.add(graph.new Node(5, 1));
		graph.nodes.add(graph.new Node(6, 1));
		graph.nodes.add(graph.new Node(7, 1));
		
		//Creating Edges
		graph.nodes.get(0).AddNeighbor(graph.nodes.get(1));
		graph.nodes.get(0).AddNeighbor(graph.nodes.get(2));
		graph.nodes.get(0).AddNeighbor(graph.nodes.get(3));
		
		graph.nodes.get(1).AddNeighbor(graph.nodes.get(0));
		graph.nodes.get(1).AddNeighbor(graph.nodes.get(2));
		graph.nodes.get(1).AddNeighbor(graph.nodes.get(3));
		
		graph.nodes.get(2).AddNeighbor(graph.nodes.get(0));
		graph.nodes.get(2).AddNeighbor(graph.nodes.get(1));
		graph.nodes.get(2).AddNeighbor(graph.nodes.get(3));
		
		graph.nodes.get(3).AddNeighbor(graph.nodes.get(0));
		graph.nodes.get(3).AddNeighbor(graph.nodes.get(1));
		graph.nodes.get(3).AddNeighbor(graph.nodes.get(2));
		graph.nodes.get(3).AddNeighbor(graph.nodes.get(4));
		
		graph.nodes.get(4).AddNeighbor(graph.nodes.get(3));
		graph.nodes.get(4).AddNeighbor(graph.nodes.get(5));
		graph.nodes.get(4).AddNeighbor(graph.nodes.get(6));
		
		graph.nodes.get(5).AddNeighbor(graph.nodes.get(4));
		graph.nodes.get(5).AddNeighbor(graph.nodes.get(6));
		
		graph.nodes.get(6).AddNeighbor(graph.nodes.get(4));
		graph.nodes.get(6).AddNeighbor(graph.nodes.get(5));
		graph.nodes.get(6).AddNeighbor(graph.nodes.get(7));
		
		graph.nodes.get(7).AddNeighbor(graph.nodes.get(6));
		
		//Find Max Clique
		ArrayList<Graph.Node> clique = graph.FindMaxClique();
		for (Graph.Node n : clique)
			System.out.print(n.toString());
	}
}
