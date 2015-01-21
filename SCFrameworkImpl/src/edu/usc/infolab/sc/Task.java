package edu.usc.infolab.sc;

import java.util.ArrayList;

public class Task extends SpatialEntity{
	ArrayList<Worker> assignedWorkers;
	Integer value;
	
	public void AssignTo(Worker worker) {
		assignedWorkers.add(worker);
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
		
	}
}
