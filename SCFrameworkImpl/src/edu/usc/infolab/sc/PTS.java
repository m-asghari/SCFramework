package edu.usc.infolab.sc;

import java.util.ArrayList;

public class PTS implements Comparable<PTS>{
	public ArrayList<Task> list;
	public Integer value;
	
	public PTS() {
		list = new ArrayList<Task>();
		value = 0;
	}
	
	public PTS(ArrayList<Task> l) {
		list = new ArrayList<Task>(l);
		value = 0;
		for (Task t : list) 
			value += t.value;
	}
	
	public PTS(PTS pts) {
		list = new ArrayList<Task>(pts.list);
		value = pts.value;
	}
	
	public void AddTask(Task t) {
		list.add(t);
		value += t.value;
	}
	
	public Task GetTask(int index) {
		return list.get(index);
	}
	
	public int size() {
		return list.size();
	}
	
	public boolean Contains(ArrayList<Task> tasks) {
		for (Task t : list) {
			if (tasks.contains(t))
				return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Tasks: ");
		for (Task t : list) {
			sb.append(String.format("%d, ", t.id));
		}
		sb.append(String.format("Value: %d\n", value));
		return sb.toString();
	}

	@Override
	public int compareTo(PTS pts) {
		return this.value.compareTo(pts.value);
	}
}
