package edu.usc.infolab.sc.Algorithms.Batched;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import edu.usc.infolab.sc.Pair;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Algorithms.Batched.FordFulkerson.FlowNetwork;
import edu.usc.infolab.sc.Algorithms.Batched.FordFulkerson.FlowNetwork.Edge;
import edu.usc.infolab.sc.Algorithms.Batched.FordFulkerson.FordFulkerson;
import edu.usc.infolab.sc.Algorithms.Batched.FordFulkerson.IntegralDirectedGraph;
import edu.usc.infolab.sc.Algorithms.Batched.FordFulkerson.Node;

public class LALS extends BatchedAlgorithm {
	private ArrayList<ArrayList<Task>> tPartitions = new ArrayList<ArrayList<Task>>();
	private ArrayList<ArrayList<Worker>> wPartitions = new ArrayList<ArrayList<Worker>>();
	
	private ArrayList<Pair<Task, Worker>> forbiddenPairs = new ArrayList<Pair<Task,Worker>>();
	
	private static final int pCutOff = 100;
	
	public LALS(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers) {
		super(tasks, workers);
	}

	@Override
	protected void ProcessBatch(ArrayList<Task> tasks, ArrayList<Worker> workers, int currentTime) {
		if (tasks.size() == 0 || workers.size() == 0)
			return;
		forbiddenPairs = new ArrayList<Pair<Task,Worker>>();
		PopulatePartitions(tasks, workers, currentTime);
		 
		
		ArrayList<Task> unassignedTasks = new ArrayList<Task>();
		for (int p = 0; p < wPartitions.size(); p++) {
			GALS(tPartitions.get(p), wPartitions.get(p), currentTime);
			for (Task t : tPartitions.get(p)) {
				if (t.assignmentStat.assigned == 0) {
					unassignedTasks.add(t);
				}
			}
		}
		
		while (true) {
			if (GALS(unassignedTasks, workers, currentTime) == Status.TERMINATE)
				break;
			Iterator<Task> it = unassignedTasks.iterator();
			while (it.hasNext()) {
				Task t = it.next();
				if (t.assignmentStat.assigned == 1)
					it.remove();
			}
		}
		//System.out.print(String.format("Assigned: %d\n", assignedCntr));
	}

	private Status GALS(ArrayList<Task> tasks, ArrayList<Worker> workers, int currentTime) {
		if (tasks.isEmpty()) {
			return Status.TERMINATE;
		}
		IntegralDirectedGraph<Node> bGraph = new IntegralDirectedGraph<Node>();
		Node source = new Node();
		bGraph.addNode(source);
		Node sink = new Node();
		bGraph.addNode(sink);
		ArrayList<Node> tNodes = new ArrayList<Node>();
		for (Task t : tasks) {
			Node n = new Node(t, 0);
			bGraph.addNode(n);
			bGraph.addEdge(source, n, 1);
			tNodes.add(n);
		}
		ArrayList<Node> wNodes = new ArrayList<Node>();
		for (Worker w : workers) {
			Node n = new Node(w, 1);
			bGraph.addNode(n);
			bGraph.addEdge(n, sink, 1);
			wNodes.add(n);
		}
		boolean terminate = true;
		for (Node n : tNodes) {
			for (Node m : wNodes) {
				if (forbiddenPairs.contains(new Pair<Task, Worker>(n.t, m.w)))
					// perhaps switch to continue
					break;
				double dist = m.w.Distance(n.t);
				if (dist < m.w.retractFrame - currentTime && dist < n.t.retractFrame - currentTime) {
					bGraph.addEdge(n, m, 1);
					terminate = false;
				}
			}
		}
		if (terminate)
			return Status.TERMINATE;

		FlowNetwork<Node> flow = FordFulkerson.maxFlow(bGraph, source, sink);
		
 		HashMap<Worker, ArrayList<Task>> initMatch = new HashMap<Worker, ArrayList<Task>>();
		for (Node m : wNodes) {
			initMatch.put(m.w, new ArrayList<Task>());
		}
		for (Node n : tNodes) {
			for (Edge<Node> e : flow.edgesFrom(n)) {
				if (e.getFlow() > 0) {
					initMatch.get(e.getEnd().w).add(e.getStart().t);
					e.getStart().t.assignmentStat.eligibleWorkers++;
				}
			}
		}
		
		for (Entry<Worker, ArrayList<Task>> e : initMatch.entrySet()) {
			Schedule(e.getKey(), e.getValue(), currentTime);
		}
		return Status.SUCCESS;
	}

	private void Schedule(Worker w, ArrayList<Task> tasks, int currentTime) {
		ArrayList<Task> schedule = null;
		for (Task t : tasks) {
			if ((schedule = w.ApproxCanPerform(t, currentTime)) != null) {
				t.assignmentStat.assigned = 1;
				t.assignmentStat.completed = 1;
				t.AssignTo(w);
				w.AddTask(t);
				w.SetSchedule(schedule);
			}
			else {
				forbiddenPairs.add(new Pair<Task,Worker>(t, w));
			}
		}
	}

	private void PopulatePartitions(ArrayList<Task> tasks, ArrayList<Worker> workers, int startTime) {
		tPartitions = new ArrayList<ArrayList<Task>>();
		wPartitions = new ArrayList<ArrayList<Worker>>();
		
		ArrayList<Worker> CurW = new ArrayList<Worker>(workers);
		ArrayList<Task> CurT = new ArrayList<Task>(tasks);
		
		Iterator<Task> it = tasks.iterator();
		while (it.hasNext()) {
			Task t = it.next();
			if (t.retractFrame < startTime)
				it.remove();
		}
		
		while (true) {
			ArrayList<Worker> PWS = new ArrayList<Worker>();
			ArrayList<Task> PTS = new ArrayList<Task>();
			ArrayList<Worker> WS = new ArrayList<Worker>();
			ArrayList<Task> TS = new ArrayList<Task>();
			Task s = CurT.remove(0);
			TS.add(s);
			int workload = 0;
			while (true) {
				for (Task t : TS) {
					for (Worker w : workers) {
						if (w.CanReach(t, startTime)) {
							if (CurW.contains(w) && !PWS.contains(w)) {
								WS.add(w);
								PWS.add(w);
								CurW.remove(w);
								workload++;
							}
						}
					}
				}
				TS = new ArrayList<Task>();
				for (Worker w : WS) {
					for (Task t : tasks) {
						if (w.CanReach(t, startTime)) {
							if (tasks.contains(t) && !PTS.contains(t)) {
								TS.add(t);
								PTS.add(t);
								CurT.remove(t);
								workload++;
							}
						}
					}
				}
				WS = new ArrayList<Worker>();
				if (workload > pCutOff || CurT.size() == 0 || CurW.size() == 0) {
					wPartitions.add(PWS);
					tPartitions.add(PTS);
					break;
				}
				if (TS.isEmpty()) {
					s = CurT.remove(0);
					TS.add(s);
				}
			}
			if (CurT.isEmpty() || CurW.isEmpty()) {
				break;
			}
		}
		//for (int p = 0; p < wPartitions.size(); p++) {
		//	System.out.print(String.format("Partition %d - Tasks: %d,  Workers: %d\n", p, tPartitions.get(0).size(), wPartitions.get(0).size()));
		//}
		return;
	}

}
