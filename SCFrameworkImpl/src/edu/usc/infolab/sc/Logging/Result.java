package edu.usc.infolab.sc.Logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public final class Result{
	private static HashMap<String, FileWriter> fws = new HashMap<String, FileWriter>();
	private static HashMap<String, BufferedWriter> bws = new HashMap<String, BufferedWriter>();
	
	public static void InitNewWriter(String name, String input) {
		try {
			fws.put(name, new FileWriter(String.format("%s.csv", input)));
			bws.put(name, new BufferedWriter(fws.get(name)));
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void FinalizeAll() {
		try {
			for (BufferedWriter bw : bws.values()) {
				bw.close();
			}
			for (FileWriter fw : fws.values()) {
				fw.close();
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void Add(String name, String format, Object... args) {
		Add(name, String.format(format, args));
	}
	
	public static void Add(String name, String msg) {
		try {
			BufferedWriter bw = bws.get(name);
			bw.write(msg);
			bw.write("\n");
			bw.flush();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static String GenerateReport(ArrayList<Worker> workers, ArrayList<Task> tasks, int endTime) {
		int assignedTasks = 0;
		int completedTasks = 0;
		double totalTraveledDistance = 0;
		double totalTraveledDistancePerTask = 0;
		double avgTraveledDistance = 0;
		double avgTraveledDistancePerTask = 0;
		double maxTraveledDistance = 0;
		double maxTraveledDistancePerTask = 0;
		double avgWorkerUtility = 0;
		ArrayList<Double> traveledDistances = new ArrayList<Double>();
		int workerCount = 0;
		
		for (Worker w : workers) {
			int numOfAssignedTask = w.GetAssignedTasks().size();
			int numOfCompletedTask = w.GetCompletedTasks().size();
			if (numOfCompletedTask > 0) {
				totalTraveledDistance += w.travledDistance;
				totalTraveledDistancePerTask += w.travledDistance / numOfCompletedTask;
				traveledDistances.add(w.travledDistance);
				if (maxTraveledDistance < w.travledDistance) {
					maxTraveledDistance = w.travledDistance;
				}
				if (w.travledDistance / numOfCompletedTask > maxTraveledDistancePerTask) {
					maxTraveledDistancePerTask = w.travledDistance / numOfAssignedTask;
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

		
		int eligibleWorkersSum = 0;
		long assignmentRuntimeDecideSum = 0;
		long assignmentRuntimeSelectSum = 0;
		for (Task t : tasks) {
			if (t.assignmentStat.assigned == 1) {
				assignedTasks++;
				eligibleWorkersSum += t.assignmentStat.eligibleWorkers;
			}
			if (t.assignmentStat.completed == 1) {
				completedTasks++;
			}
			assignmentRuntimeDecideSum += t.assignmentStat.decideEligibilityTime;
			assignmentRuntimeSelectSum += t.assignmentStat.selectWorkerTime;
		}
		double eligibleWorkerAvg = (double)eligibleWorkersSum / assignedTasks;
		double runTimeDecideAvg = (double)assignmentRuntimeDecideSum / tasks.size();
		double runTimeSelectAvg = (double)assignmentRuntimeSelectSum / tasks.size();
		
		Log.Add(0, "\n\nFinal Report:\n");
		Log.Add(0, "Total Number of Workers: %d", workers.size());
		Log.Add(0, "Total Number of Used Workers: %d", workerCount);
		Log.Add(0, "Total Number of Assigned Tasks: %d", assignedTasks);
		Log.Add(0, "Total Number of Completed Tasks: %d", completedTasks);
		Log.Add(0, "Avg Eligible Workers (Per Assigned Tasks): %.2f", eligibleWorkerAvg);
		Log.Add(0, "Total Traveled Distance: %.2f", totalTraveledDistance);
		Log.Add(0, "Total Traveled Distance (Per Task): %.2f", totalTraveledDistancePerTask);
		Log.Add(0, "Avg Traveled Distance: %.2f", avgTraveledDistance);
		Log.Add(0, "Avg Traveled Distance (Per Task): %.2f", avgTraveledDistancePerTask);
		Log.Add(0, "Max Traveled Distance: %.2f", maxTraveledDistance);
		Log.Add(0, "Max Traveled Distance (Per Task): %.2f", maxTraveledDistancePerTask);
		Log.Add(0, "Avg Worker Utility: %.2f", avgWorkerUtility);
		String summary = String.format("%d,%d,%.2f,%.2f,%.2f,%.2f", 
				assignedTasks, completedTasks, eligibleWorkerAvg, avgTraveledDistancePerTask,
				runTimeDecideAvg, runTimeSelectAvg); 
		Log.Add(0, summary);
		return summary;
	}
	
	public static String GetAssignmentStats(String algorithm, ArrayList<Task> tasks) {
		Collections.sort(tasks);
		StringBuilder sb = new StringBuilder();
		sb.append(algorithm);
		for (Task t : tasks) {
			sb.append(String.format(",%d", t.assignmentStat.availableWorkers));
		}
		sb.append("\n");
		sb.append(algorithm);
		for (Task t : tasks) {
			sb.append(String.format(",%d", t.assignmentStat.eligibleWorkers));
		}
		sb.append("\n");
		sb.append(algorithm);
		for (Task t : tasks) {
			Double sum = 0.0;
			for (long ft : t.assignmentStat.workerFreeTimes) {
				sum += ft;
			}
			sb.append(String.format(",%.2f", sum / t.assignmentStat.workerFreeTimes.size()));
		}
		sb.append("\n");
		sb.append(algorithm);
		for (Task t : tasks) {
			sb.append(String.format(",%d", t.assignmentStat.assigned));
		}
		sb.append("\n");
		return sb.toString();
	}
}
