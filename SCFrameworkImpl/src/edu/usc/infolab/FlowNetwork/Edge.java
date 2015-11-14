package edu.usc.infolab.FlowNetwork;

public class Edge {
	public Node start;
	public Node end;
	public double cost;
	public double flow;
	
	public Edge(Node start, Node end, double cost) {
		this.start = start;
		this.end = end;
		this.cost = cost;
		this.flow = 0;
	}
	
	public Edge(Node start, Node end, double cost, double flow) {
		this.start = start;
		this.end = end;
		this.cost = cost;
		this.flow = flow;
	}
	
	public String GetID() {
		return String.format("%d-%d", start.id, end.id);
	}
	
	@Override
	public boolean equals(Object obj) {
		Edge e = (Edge) obj;
		return this.start == e.start && this.end == e.end;
	}
	
	@Override
	public String toString() {
		return this.GetID() + ", " + Double.toString(this.cost);
	}
}
