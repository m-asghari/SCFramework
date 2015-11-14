package edu.usc.infolab.sc;

import java.util.ArrayList;

import org.w3c.dom.Element;

public class Task extends SpatialEntity{
	
	public class AssignmentStat {
		public int assigned;
		public int completed;
		public int eligibleWorkers;
		public int availableWorkers;
		public ArrayList<Integer> workerFreeTimes;
		public long decideEligibilityTime;
		public long selectWorkerTime;
		public long totalTime;
		
		private AssignmentStat(AssignmentStat as) {
			this.assigned = as.assigned;
			this.eligibleWorkers = as.eligibleWorkers;
			this.availableWorkers = as.availableWorkers;
			this.workerFreeTimes = new ArrayList<Integer>(as.workerFreeTimes);
		}
		
		public AssignmentStat() {
			this.assigned = 0;
			this.completed = 0;
			this.eligibleWorkers = 0;
			this.availableWorkers = 0;
			this.decideEligibilityTime = 0;
			this.selectWorkerTime = 0;
			this.totalTime = 0;
			workerFreeTimes = new ArrayList<Integer>();
		}
		
		public AssignmentStat clone() {
			return new AssignmentStat(this);
		}
	}
	
	public static Integer idCntr = 0;
	ArrayList<Worker> assignedWorkers;
	public AssignmentStat assignmentStat;
	public Integer value;
	
	private Task(Task t) {
		super(t);
		this.assignedWorkers = new ArrayList<Worker>(t.assignedWorkers);
		this.assignmentStat = t.assignmentStat.clone();
		this.value = t.value;
	}
	
	public Task() {
		Initialize();
		this.id = idCntr++;
	}
	
	public Task(Element e) {
		super(e);
		Initialize();
		this.id = idCntr++;
		this.value = Integer.parseInt(e.getAttribute("value"));
		this.assignmentStat = new AssignmentStat();
		this.value = 1;
	}
	
	private void Initialize() {
		assignedWorkers = new ArrayList<Worker>();
		assignmentStat = new AssignmentStat();
	}
	
	public void AssignTo(Worker worker) {
		assignedWorkers.add(worker);
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);		
	}
	
	public Element Fill(Element t) {
		t = super.Fill(t);
		t.setAttribute("value", Integer.toString(value));
		return t;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Task %d:\n", this.id));
		sb.append(super.toString());
		sb.append("Assigned Worker: ");
		for (Worker w : assignedWorkers)
			sb.append(String.format("w%d, ", w.id));
		sb.append(String.format("\nValue: %d\n", value));
		return sb.toString();
	}
	
	@Override
	public Task clone(){
		return new Task(this);
	}
}
