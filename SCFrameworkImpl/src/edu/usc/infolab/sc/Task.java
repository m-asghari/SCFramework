package edu.usc.infolab.sc;

import java.util.ArrayList;

import org.w3c.dom.Element;

public class Task extends SpatialEntity{
	public static Integer idCntr = 0;
	ArrayList<Worker> assignedWorkers;
	public Integer value;
	
	public Task() {
		Initialize();
		this.id = idCntr++;
	}
	
	public Task(Element e) {
		super(e);
		Initialize();
		this.id = idCntr++;
		this.value = Integer.parseInt(e.getAttribute("value"));
		this.value = 1;
	}
	
	private void Initialize() {
		assignedWorkers = new ArrayList<Worker>();
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
}
