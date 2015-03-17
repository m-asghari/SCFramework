package edu.usc.infolab.sc.Logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public final class Result{
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
		Add(String.format(format, args));
	}
	
	public static void Add(String msg) {
		try {
			bw.write(msg);
			bw.write("\n");
			bw.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String GenerateReport(ArrayList<Worker> workers, ArrayList<Task> tasks, int endTime) {
		int assignedTasks = 0;
		int gainedValue = 0;
		double totalTraveledDistance = 0;
		double totalTraveledDistancePerTask = 0;
		double avgTraveledDistance = 0;
		double avgTraveledDistancePerTask = 0;
		double maxTraveledDistance = 0;
		double maxTraveledDistancePerTask = 0;
		double avgWorkerUtility = 0;
		double avgEligibleWorker = 0;
		ArrayList<Double> traveledDistances = new ArrayList<Double>();
		int workerCount = 0;
		
		for (Worker w : workers) {
			ArrayList<Task> wTasks = w.GetAssignedTasks();
			for (Task t : wTasks) {
				assignedTasks++;
				gainedValue += t.value;
			}
			if (wTasks.size() > 0) {
				totalTraveledDistance += w.travledDistance;
				totalTraveledDistancePerTask += w.travledDistance / wTasks.size();
				traveledDistances.add(w.travledDistance);
				if (maxTraveledDistance < w.travledDistance) {
					maxTraveledDistance = w.travledDistance;
				}
				if (w.travledDistance / wTasks.size() > maxTraveledDistancePerTask) {
					maxTraveledDistancePerTask = w.travledDistance / wTasks.size();
				}
				avgWorkerUtility += w.GetUtility(endTime);
				workerCount++;
			}
		}
		
		if (workerCount != 0) {
			avgTraveledDistance = totalTraveledDistance / workerCount;
			avgTraveledDistancePerTask = totalTraveledDistancePerTask / workerCount;
		}
		avgWorkerUtility = avgWorkerUtility / workers.size();

		int sum = 0;
		int count = 0;
		for (Task t : tasks) {
			if (t.eligibleWorkers > 0) {
				count++;
				sum += t.eligibleWorkers;
			}
		}
		avgEligibleWorker = (count > 0) ? (double)sum/count : 0;
		
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
		Log.Add(0, "Avg Worker Utility: %.2f", avgWorkerUtility);
		Log.Add(0, "Avg Eligible Workers: %.2f", avgEligibleWorker);
		String summary = String.format("%d,%d,%.2f,%.2f, %.2f, %.2f", 
				assignedTasks, gainedValue, totalTraveledDistance, avgTraveledDistancePerTask, avgEligibleWorker, avgWorkerUtility); 
		Log.Add(0, summary);
		return summary;
	}
}
