package edu.usc.infolab.FlowNetwork;

public class REdge extends Edge {
	public double rCapacity;
	
	public REdge(Node start, Node end, double cost, double flow, double rCapacity) {
		super(start, end, cost, flow);
		this.rCapacity = rCapacity;
	}

	public Double GetReducedCost() {
		return cost - start.potential + end.potential;
	}
}
