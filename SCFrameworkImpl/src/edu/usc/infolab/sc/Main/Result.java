package edu.usc.infolab.sc.Main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public final class Result {
	private static FileWriter fw;
	private static BufferedWriter bw;
	
	public static void Initialize(String input) {
		try {
			fw = new FileWriter(String.format("%s.csv", input));
			bw = new BufferedWriter(fw);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void Finalize() {
		try {
			bw.close();
			fw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void Add(String format, Object... args) {
		Result.Add(String.format(format, args));
	}
		
	public static void Add(String result) {
		try {
			bw.write(result);
			bw.write("\n");
			bw.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String GenerateReport(ArrayList<Worker> workers) {
		int assignedTasks = 0;
		int gainedValue = 0;
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
				assignedTasks++;
				gainedValue += t.value;
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
		Log.Add(0, "Total Number of Assigned Tasks: %d", assignedTasks);
		Log.Add(0, "Total Gained Valued: %d", gainedValue);
		Log.Add(0, "Total Traveled Distance: %.2f", totalTraveledDistance);
		Log.Add(0, "Total Traveled Distance (Per Task): %.2f", totalTraveledDistancePerTask);
		Log.Add(0, "Avg Traveled Distance: %.2f", avgTraveledDistance);
		Log.Add(0, "Avg Traveled Distance (Per Task): %.2f", avgTraveledDistancePerTask);
		Log.Add(0, "Max Traveled Distance: %.2f", maxTraveledDistance);
		Log.Add(0, "Max Traveled Distance (Per Task): %.2f", maxTraveledDistancePerTask);
		String summary = String.format("%d,%d,%.2f,%.2f", assignedTasks, gainedValue, totalTraveledDistance, avgTraveledDistancePerTask); 
		Log.Add(0, summary);
		return summary;
	}
}
