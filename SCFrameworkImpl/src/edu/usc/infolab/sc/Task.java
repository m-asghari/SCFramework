package edu.usc.infolab.sc;

import java.util.ArrayList;

import org.w3c.dom.Element;

public class Task extends SpatialEntity{
	ArrayList<Worker> assignedWorkers;
	public Integer value;
	
	public Task() {
		Initialize();
	}
	
	public Task(Element e) {
		super(e);
		Initialize();
		this.value = Integer.parseInt(e.getAttribute("value"));
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
}
