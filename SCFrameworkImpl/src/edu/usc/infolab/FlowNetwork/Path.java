package edu.usc.infolab.FlowNetwork;

import java.util.ArrayList;

public class Path {
	public ArrayList<Integer> nodes;
	public Double distance;
	
	public Path() {
		nodes = new ArrayList<Integer>();
		distance = 0.;
	}
	
	public void AddNode(Node n, Double dist) {
		nodes.add(n.id);
		distance += dist;
	}
}
