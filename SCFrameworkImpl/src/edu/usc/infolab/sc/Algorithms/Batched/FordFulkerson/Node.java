package edu.usc.infolab.sc.Algorithms.Batched.FordFulkerson;

import edu.usc.infolab.sc.SpatialEntity;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class Node {
	private static int idCtr = 0;
	int id;
	public int type;
	public Worker w;
	public Task t;
	
	public Node() {
		this.id = idCtr++;
	}
	
	public Node(SpatialEntity se, int type) {
		this.id = idCtr++;
		this.type = type;
		if (type == 0) {
			//t = (Task)se.clone();
			t = (Task)se;
			w = null;
		} else if (type == 1) {
			t = null;
			//w = (Worker)se.clone();
			w = (Worker)se;
		}
	}
	
	public Node(int id) {
		this.id = id;
	}
}
