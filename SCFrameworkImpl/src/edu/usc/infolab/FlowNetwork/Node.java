package edu.usc.infolab.FlowNetwork;

import java.util.ArrayList;

public class Node {
	private static int cntr = 0;
	public int id;
	public double b;
	public double potential;
	public ArrayList<Integer> adjList;
	public ArrayList<Edge> inEdges;
	public ArrayList<Edge> outEdges;
	
	public Node(double b) {
		this.id = cntr++;
		this.b = b;
		this.inEdges = new ArrayList<Edge>();
		this.outEdges = new ArrayList<Edge>();
		this.adjList = new ArrayList<Integer>();
		this.potential = 0;
	}
	
	public Node(double b, ArrayList<Edge> in, ArrayList<Edge> out) {
		this.id = cntr++;
		this.b = b;
		this.inEdges = new ArrayList<Edge>(in);
		this.outEdges = new ArrayList<Edge>(out);
		this.adjList = new ArrayList<Integer>();
		for (Edge e : out) {
			adjList.add(e.end.id);
		}
		this.potential = 0;
	}
	
	public double GetImbalance() {
		double input = 0;
		for (Edge e : inEdges) {
			input += e.flow;
		}
		double output = 0;
		for (Edge e: outEdges) {
			output += e.flow;
		}
		return this.b + input - output;
	}
	
	@Override
	public boolean equals(Object obj) {
		Node n = (Node) obj;
		return n.id == this.id;
	}
	
	@Override
	public String toString() {
		return String.format("%d", id);
	}
}
