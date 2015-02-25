package edu.usc.infolab.sc.Main;

import java.util.ArrayList;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public final class Result {
	public static int AssignedTasks = 0;
	public static int GainedValue = 0;
	
	public static void GenerateReport() {
		Log.Add(0, "\n\nFinal Report:\n");
		Log.Add(0, "Total Number of Assigned Tasks: %d\n", AssignedTasks);
		Log.Add(0, "Total Gained Valued: %d\n", GainedValue);
	}
	
	public static void GenerateReport(ArrayList<Worker> workers) {
		double totalTraveledDistance = 0;
		double totalTraveledDistancePerTask = 0;
		double avgTraveledDistance = 0;
		double avgTraveledDistancePerTask = 0;
		double maxTraveledDistance = 0;
		double maxTraveledDistancePerTask = 0;
		ArrayList<Double> traveledDistances = new ArrayList<Double>();
		int workerCount = 0;
		
		for (Worker w : workers) {
			ArrayList<Task> tasks = w.GetAssignedTasks();
			for (Task t : tasks) {
				AssignedTasks++;
				GainedValue += t.value;
			}
			if (tasks.size() > 0) {
				totalTraveledDistance += w.travledDistance;
				totalTraveledDistancePerTask += w.travledDistance / tasks.size();
				traveledDistances.add(w.travledDistance);
				if (maxTraveledDistance < w.travledDistance) {
					maxTraveledDistance = w.travledDistance;
				}
				if (w.travledDistance / tasks.size() > maxTraveledDistancePerTask) {
					maxTraveledDistancePerTask = w.travledDistance / tasks.size();
				}
				workerCount++;
			}
		}
		
		if (workerCount != 0) {
			avgTraveledDistance = totalTraveledDistance / workerCount;
			avgTraveledDistancePerTask = totalTraveledDistancePerTask / workerCount;
		}
		
		Log.Add(0, "\n\nFinal Report:\n");
		Log.Add(0, "Total Number of Workers: %d", workers.size());
		Log.Add(0, "Total Number of Used Workers: %d", workerCount);
		Log.Add(0, "Total Number of Assigned Tasks: %d", AssignedTasks);
		Log.Add(0, "Total Gained Valued: %d", GainedValue);
		Log.Add(0, "Total Traveled Distance: %.2f", totalTraveledDistance);
		Log.Add(0, "Total Traveled Distance (Per Task): %.2f", totalTraveledDistancePerTask);
		Log.Add(0, "Avg Traveled Distance: %.2f", avgTraveledDistance);
		Log.Add(0, "Avg Traveled Distance (Per Task): %.2f", avgTraveledDistancePerTask);
		Log.Add(0, "Max Traveled Distance: %.2f", maxTraveledDistance);
		Log.Add(0, "Max Traveled Distance (Per Task): %.2f", maxTraveledDistancePerTask);
		Log.Add(0, "%d, %d, %.2f, %.2f", AssignedTasks, GainedValue, totalTraveledDistance, avgTraveledDistancePerTask);
	}
}
