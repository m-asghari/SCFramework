package edu.usc.infolab.sc.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Pair;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class ResultGenerator {
	private static final String RND = "Random";
	private static final String RNK = "Ranking";
	private static final String NN = "NearestNeighbor";
	private static final String BI = "BestInsersion";
	private static final String MFT = "MostFreeTime";
	private static final String ADHOC = "AdHoc";
	private static final String JSD = "JSD";
	private static final String EMD = "EMD";
	private static final String LALS = "LALS";

	private static HashMap<String, ArrayList<Result>> results = new HashMap<String, ArrayList<Result>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			//put(RND, new ArrayList<Result>());
			//put(RNK, new ArrayList<Result>());
			//put(NN, new ArrayList<Result>());
			put(BI, new ArrayList<Result>());
			//put(MFT, new ArrayList<Result>());
			//put(ADHOC, new ArrayList<Result>());
			//put(JSD, new ArrayList<Result>());
			//put(EMD, new ArrayList<Result>());
			put(LALS, new ArrayList<Result>());
		}
	};

	public static void main(String[] args) {
		String inputDir = "BatchedVsOnline";
		File dir = new File(inputDir);
		File[] files = dir.listFiles();
		for (File file : files) {
			String method = GetMethod(file.getName());
			if (method != null) {
				//System.out.println(file.getName());
				String id = "";
				try {
					id = file.getName().substring(9, file.getName().indexOf("."));
				}
				catch (Exception e) {
				}
				Pair<ArrayList<Task>, ArrayList<Worker>> entities = ReadFileContents(file);
				ArrayList<Task> tasks = new ArrayList<Task>(entities.First);
				ArrayList<Worker> workers = new ArrayList<Worker>(entities.Second);
				Result result = GetResults(tasks, workers);
				result.ID = id;
				results.get(method).add(result);
			}
		}
		SaveToFile(inputDir, results);
	}

	private static void SaveToFile(String dir, HashMap<String, ArrayList<Result>> res) {
		FileWriter fw;
		BufferedWriter bw;
		try {
			fw = new FileWriter(new File(dir, "ResultGenerator_Results.csv"));
			bw = new BufferedWriter(fw);
			int lines = res.get(BI).size();
			for (int i = 0; i < lines; i++) {
				StringBuilder line = new StringBuilder();
				//Result rnd = res.get(RND).get(i);
				//Result rnk = res.get(RNK).get(i);
				//Result nn = res.get(NN).get(i);
				Result bi = res.get(BI).get(i);
				//Result mft = res.get(MFT).get(i);
				//Result adhoc = res.get(ADHOC).get(i);
				//Result jsd = res.get(JSD).get(i);
				//Result emd = res.get(EMD).get(i);
				Result lals = res.get(LALS).get(i);
				line.append(bi.ID + ",");
				line.append(bi.TaskCount + ",");
				//line.append(rnd.NumOfAssignedTasks + ",");
				//line.append(rnk.NumOfAssignedTasks + ",");
				//line.append(nn.NumOfAssignedTasks + ",");
				//line.append(mft.NumOfAssignedTasks + ",");
				line.append(bi.NumOfAssignedTasks + ",");
				//line.append(adhoc.NumOfAssignedTasks + ",");
				//line.append(jsd.NumOfAssignedTasks + ",");
				//line.append(emd.NumOfAssignedTasks + ",");
				line.append(lals.NumOfAssignedTasks + ",");
				//line.append(rnd.NumOfCompletedTasks + ",");
				//line.append(rnk.NumOfCompletedTasks + ",");
				//line.append(nn.NumOfCompletedTasks + ",");
				//line.append(mft.NumOfCompletedTasks + ",");
				line.append(bi.NumOfCompletedTasks + ",");
				//line.append(adhoc.NumOfCompletedTasks + ",");
				//line.append(jsd.NumOfCompletedTasks + ",");
				//line.append(emd.NumOfCompletedTasks + ",");
				line.append(lals.NumOfCompletedTasks + ",");
				//line.append(rnd.DecideEligibilityRunTime + ",");
				//line.append(rnk.DecideEligibilityRunTime + ",");
				//line.append(nn.DecideEligibilityRunTime + ",");
				//line.append(mft.DecideEligibilityRunTime + ",");
				line.append(bi.DecideEligibilityRunTime + ",");
				//line.append(adhoc.DecideEligibilityRunTime + ",");
				//line.append(jsd.DecideEligibilityRunTime + ",");
				//line.append(emd.DecideEligibilityRunTime + ",");
				line.append(lals.DecideEligibilityRunTime + ",");
				//line.append(rnd.SelectWorkerRunTime + ",");
				//line.append(rnk.SelectWorkerRunTime + ",");
				//line.append(nn.SelectWorkerRunTime + ",");
				//line.append(mft.SelectWorkerRunTime + ",");
				line.append(bi.SelectWorkerRunTime + ",");
				//line.append(adhoc.SelectWorkerRunTime + ",");
				//line.append(jsd.SelectWorkerRunTime + ",");
				//line.append(emd.SelectWorkerRunTime + ",");
				line.append(lals.SelectWorkerRunTime + ",");
				//line.append(rnd.TotalTime + ",");
				//line.append(rnk.TotalTime + ",");
				//line.append(nn.TotalTime + ",");
				//line.append(mft.TotalTime + ",");
				line.append(bi.TotalTime + ",");
				//line.append(adhoc.TotalTime + ",");
				//line.append(jsd.TotalTime + ",");
				//line.append(emd.TotalTime + ",");
				line.append(lals.TotalTime + ",");
				//line.append(rnd.EligibleWorkers + ",");
				//line.append(rnk.EligibleWorkers + ",");
				//line.append(nn.EligibleWorkers + ",");
				//line.append(mft.EligibleWorkers + ",");
				line.append(bi.EligibleWorkers + ",");
				//line.append(adhoc.EligibleWorkers + ",");
				//line.append(jsd.EligibleWorkers + ",");
				//line.append(emd.EligibleWorkers + ",");
				line.append(lals.EligibleWorkers + ",");
				//line.append(rnd.AvgTraveledDistancePerTask + ",");
				//line.append(rnk.AvgTraveledDistancePerTask + ",");
				//line.append(nn.AvgTraveledDistancePerTask + ",");
				//line.append(mft.AvgTraveledDistancePerTask + ",");
				line.append(bi.AvgTraveledDistancePerTask + ",");
				//line.append(adhoc.AvgTraveledDistancePerTask + ",");
				//line.append(jsd.AvgTraveledDistancePerTask + ",");
				//line.append(emd.AvgTraveledDistancePerTask + "\n");
				line.append(lals.AvgTraveledDistancePerTask + "\n");
				bw.write(line.toString());
			}
			bw.close();
			fw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	private static Result GetResults(ArrayList<Task> tasks,
			ArrayList<Worker> workers) {
		Result result = new Result();
		for (Task t : tasks) {
			if (t.assignmentStat.assigned == 1)
				result.NumOfAssignedTasks++;
			if (t.assignmentStat.completed == 1) {
				result.NumOfCompletedTasks++;
				result.EligibleWorkers += t.assignmentStat.eligibleWorkers;
				result.TotalTime += t.assignmentStat.totalTime;	
			}
			result.DecideEligibilityRunTime += t.assignmentStat.decideEligibilityTime;
			result.SelectWorkerRunTime += t.assignmentStat.selectWorkerTime;
		}
		for (Worker w : workers) {
			result.AvgTraveledDistancePerTask += w.travledDistance;
		}
		result.DecideEligibilityRunTime /= tasks.size();
		result.SelectWorkerRunTime /= tasks.size();
		result.TotalTime /= result.NumOfCompletedTasks;
		result.EligibleWorkers /= result.NumOfCompletedTasks;
		result.AvgTraveledDistancePerTask /= result.NumOfCompletedTasks;
		result.TaskCount = tasks.size();
		return result;
	}

	private static Pair<ArrayList<Task>, ArrayList<Worker>> ReadFileContents(
			File file) {
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			int tasksSize = Integer.parseInt(br.readLine());
			ArrayList<Task> tasks = new ArrayList<Task>();
			for (int i = 0; i < tasksSize; i++) {
				String newLine = br.readLine();
				String[] params = newLine.split(",");
				Task t = new Task();
				t.assignmentStat.assigned = Integer.parseInt(params[0]);
				t.assignmentStat.completed = Integer.parseInt(params[1]);
				t.releaseFrame = Integer.parseInt(params[2]);
				t.retractFrame = Integer.parseInt(params[3]);
				t.value = Integer.parseInt(params[4]);
				t.assignmentStat.decideEligibilityTime = Integer.parseInt(params[5]);
				t.assignmentStat.selectWorkerTime = Integer.parseInt(params[6]);
				t.assignmentStat.totalTime = Integer.parseInt(params[7]);
				t.assignmentStat.availableWorkers = Integer.parseInt(params[8]);
				t.assignmentStat.eligibleWorkers = Integer.parseInt(params[9]);
				if (params.length > 10) {
					String[] times = params[10].split(";");
					for (int j = 0; j < times.length; j++) {
						t.assignmentStat.workerFreeTimes.add(Long.parseLong(times[j]));
					}
				}
				tasks.add(t);
			}
			
			int workerSize = Integer.parseInt(br.readLine());
			ArrayList<Worker> workers = new ArrayList<Worker>();
			for (int i = 0 ; i < workerSize; i++) {
				String newLine = br.readLine();
				String[] params = newLine.split(",");
				Worker w = new Worker();
				w.releaseFrame = Integer.parseInt(params[0]);
				w.retractFrame = Integer.parseInt(params[1]);
				w.travledDistance = Double.parseDouble(params[2]);
				w.maxNumberOfTasks = Integer.parseInt(params[3]);
				workers.add(w);
			}
			
			br.close();
			fr.close();
			
			return new Pair<ArrayList<Task>, ArrayList<Worker>>(tasks, workers);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private static String GetMethod(String name) {
		if (!name.contains(".txt"))
			return null;
		return name.substring(name.indexOf(".") + 5, name.length() - 4);
	}

}
