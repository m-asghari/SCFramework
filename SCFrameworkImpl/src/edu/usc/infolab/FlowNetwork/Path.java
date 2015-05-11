package edu.usc.infolab.FlowNetwork;

import java.util.ArrayList;

public class Path {
	public ArrayList<Integer> nodes;
	public int distance;
	
	public Path() {
		nodes = new ArrayList<Integer>();
		distance = 0;
	}
	
	public void AddNode(Node n, int dist) {
		nodes.add(n.id);
		distance += dist;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Integer id : nodes) {
			sb.append(id);
			sb.append("-");
		}
		return sb.toString();
	}
}
