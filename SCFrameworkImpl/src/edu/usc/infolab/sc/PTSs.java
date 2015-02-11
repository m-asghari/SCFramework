package edu.usc.infolab.sc;

import java.util.ArrayList;

public class PTSs {
	ArrayList<PTS> list;
	
	public PTSs() {
		list = new ArrayList<PTS>();
	}
	
	public PTSs(ArrayList<PTS> l) {
		list = new ArrayList<PTS>(l);
	}
	
	public PTSs(PTSs p) {
		list = new ArrayList<PTS>(p.list);
	}
	
	public void AddSubset(PTS pts) {
		list.add(pts);
	}
	
	public PTS GetSubset(int index) {
		return list.get(index);
	}
	
	public int Size() {
		return list.size();
	}
	
	public void addAll(PTSs p) {
		for (PTS pts : p.list) {
			this.AddSubset(pts);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PTSs:\n");
		for (PTS pts : list)
			sb.append(pts.toString());
		sb.append("\n");
		return sb.toString();
	}
}
