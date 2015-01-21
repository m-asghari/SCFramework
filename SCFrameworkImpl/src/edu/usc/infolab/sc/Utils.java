package edu.usc.infolab.sc;

import java.util.ArrayList;

public class Utils {
	
	// Later try to replace this with a method that generates permutations one at a time
	// so we can stop once we have a hit!
	public static ArrayList<ArrayList<Task>> Permutations(ArrayList<Task> tasks) {
		ArrayList<ArrayList<Task>> p = new ArrayList<ArrayList<Task>>();
		
		p.add(new ArrayList<Task>());
		
		for (int i = 0; i < tasks.size(); ++i) {
			ArrayList<ArrayList<Task>> current = new ArrayList<ArrayList<Task>>();
			
			for (ArrayList<Task> l : p) {
				for (int j = 0; j < l.size() + 1; j++) {
					l.add(j, tasks.get(i));
					
					ArrayList<Task> temp = new ArrayList<Task>(l);
					current.add(temp);
					
					l.remove(j);
				}
			}
			
			p = new ArrayList<ArrayList<Task>>(current);
		}
		return p;
	}
}
