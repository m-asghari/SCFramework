package edu.usc.infolab.sc.Clairvoyant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.usc.infolab.sc.PTS;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class BestPTSFirst extends ClairvoyantAlgorithm{
	ArrayList<ArrayList<PTS>> _sortedPTSs;
	
	public BestPTSFirst(HashMap<Integer, Task> tasks,
			HashMap<Integer, Worker> workers) {
		super(tasks, workers);
		_sortedPTSs = new ArrayList<ArrayList<PTS>>();
	}

	@Override
	public void Run() {
		FindPTSs();
		
		for (Worker w : _workers.values()) {
			ArrayList<PTS> newList = new ArrayList<PTS>(w.GetPTSSet().GetList());
			Collections.sort(newList);
			_sortedPTSs.add(newList);
		}
		
		ArrayList<PTS> seletedPTSs = new ArrayList<PTS>();
		ArrayList<Task> assignedTasks = new ArrayList<Task>();
		while (!_sortedPTSs.isEmpty()) {
			int i = FindMaxPTS();
			PTS newPTS = _sortedPTSs.get(i).get(0);
			
			for (Task t : newPTS.list) {
				assignedTasks.add(t);
			}
			
			seletedPTSs.add(newPTS);
			
			UpdateSortedPTSs(assignedTasks);
		}
	}

	private void UpdateSortedPTSs(ArrayList<Task> assignedTasks) {
		int list = 0;
		while (list < _sortedPTSs.size()) {
			ArrayList<PTS> currentList = _sortedPTSs.get(list);
			int pts = 0;
			while (pts < currentList.size()) {
				if (currentList.get(pts).Contains(assignedTasks))
					currentList.remove(pts);
				else
					pts++;
			}
			if (currentList.size() > 0) {
				_sortedPTSs.set(list, currentList);
				list++;
			}
			else {
				_sortedPTSs.remove(list);
			}
		}		
	}

	private int FindMaxPTS() {
		int maxValue = Integer.MIN_VALUE;
		int bestList = -1;
		for (int i = 0; i < _sortedPTSs.size(); ++i) {
			int currentValue = _sortedPTSs.get(i).get(0).value;
			if (currentValue > maxValue) { 
				maxValue = currentValue;
				bestList = i;
			}
		}
		return bestList;
	}
	
	

}
