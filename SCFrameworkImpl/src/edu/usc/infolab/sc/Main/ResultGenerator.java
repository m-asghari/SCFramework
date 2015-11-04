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
	private static final String RNK = "Ranking";
	private static final String NN = "NearestNeighbor";
	private static final String BI = "BestInsersion";
	private static final String MFT = "MostFreeTime";
	private static final String ADHOC = "AdHoc";
	private static final String JSD = "JSD";
	private static final String EMD = "EMD";

	private static HashMap<String, ArrayList<Result>> results = new HashMap<String, ArrayList<Result>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(RNK, new ArrayList<Result>());
			put(NN, new ArrayList<Result>());
			put(BI, new ArrayList<Result>());
			put(MFT, new ArrayList<Result>());
			put(ADHOC, new ArrayList<Result>());
			put(JSD, new ArrayList<Result>());
			put(EMD, new ArrayList<Result>());
		}
	};

	public static void main(String[] args) {
		String inputDir = "SkewedTasks_4";
		File dir = new File(inputDir);
		File[] files = dir.listFiles();
		for (File file : files) {
			String method = GetMethod(file.getName());
			if (method != null) {
				//System.out.println(file.getName());
				String id = file.getName().substring(9, file.getName().indexOf("."));
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
			int lines = res.get("Ranking").size();
			for (int i = 0; i < lines; i++) {
				StringBuilder line = new StringBuilder();
				Result rnk = res.get(RNK).get(i);
				Result nn = res.get(NN).get(i);
				Result bi = res.get(BI).get(i);
				Result mft = res.get(MFT).get(i);
				Result adhoc = res.get(ADHOC).get(i);
				Result jsd = res.get(JSD).get(i);
				Result emd = res.get(EMD).get(i);
				line.append(rnk.ID + ",");
				line.append(rnk.NumOfAssignedTasks + ",");
				line.append(nn.NumOfAssignedTasks + ",");
				line.append(bi.NumOfAssignedTasks + ",");
				line.append(mft.NumOfAssignedTasks + ",");
				line.append(adhoc.NumOfAssignedTasks + ",");
				line.append(jsd.NumOfAssignedTasks + ",");
				line.append(emd.NumOfAssignedTasks + ",");
				line.append(rnk.DecideEligibilityRunTime + ",");
				line.append(nn.DecideEligibilityRunTime + ",");
				line.append(bi.DecideEligibilityRunTime + ",");
				line.append(mft.DecideEligibilityRunTime + ",");
				line.append(adhoc.DecideEligibilityRunTime + ",");
				line.append(jsd.DecideEligibilityRunTime + ",");
				line.append(emd.DecideEligibilityRunTime + ",");
				line.append(rnk.SelectWorkerRunTime + ",");
				line.append(nn.SelectWorkerRunTime + ",");
				line.append(bi.SelectWorkerRunTime + ",");
				line.append(mft.SelectWorkerRunTime + ",");
				line.append(adhoc.SelectWorkerRunTime + ",");
				line.append(jsd.SelectWorkerRunTime + ",");
				line.append(emd.SelectWorkerRunTime + ",");
				line.append(rnk.AvgTraveledDistancePerTask + ",");
				line.append(nn.AvgTraveledDistancePerTask + ",");
				line.append(bi.AvgTraveledDistancePerTask + ",");
				line.append(mft.AvgTraveledDistancePerTask + ",");
				line.append(adhoc.AvgTraveledDistancePerTask + ",");
				line.append(jsd.AvgTraveledDistancePerTask + ",");
				line.append(emd.AvgTraveledDistancePerTask + "\n");
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
			result.DecideEligibilityRunTime += t.assignmentStat.decideEligibilityTime;
			result.SelectWorkerRunTime += t.assignmentStat.selectWorkerTime;
		}
		for (Worker w : workers) {
			result.AvgTraveledDistancePerTask += w.travledDistance;
		}
		result.DecideEligibilityRunTime /= tasks.size();
		result.SelectWorkerRunTime /= tasks.size();
		result.AvgTraveledDistancePerTask /= result.NumOfAssignedTasks;
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
				t.releaseFrame = Integer.parseInt(params[1]);
				t.retractFrame = Integer.parseInt(params[2]);
				t.value = Integer.parseInt(params[3]);
				t.assignmentStat.decideEligibilityTime = Integer.parseInt(params[4]);
				t.assignmentStat.selectWorkerTime = Integer.parseInt(params[5]);
				t.assignmentStat.availableWorkers = Integer.parseInt(params[6]);
				t.assignmentStat.eligibleWorkers = Integer.parseInt(params[7]);
				if (params.length > 8) {
					String[] times = params[8].split(";");
					for (int j = 0; j < times.length; j++) {
						t.assignmentStat.workerFreeTimes.add(Integer.parseInt(times[j]));
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
