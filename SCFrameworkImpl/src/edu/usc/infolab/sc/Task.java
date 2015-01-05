package edu.usc.infolab.sc;

import java.util.ArrayList;

public class Task extends SpatialEntity{
	ArrayList<Worker> assignedWorkers;
	long deadlineFrame;
	
	public void AssignTo(Worker worker) {
		assignedWorkers.add(worker);
	}
}
