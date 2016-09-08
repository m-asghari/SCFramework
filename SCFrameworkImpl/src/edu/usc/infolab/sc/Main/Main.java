package edu.usc.infolab.sc.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import edu.usc.infolab.sc.CountDistribution;
import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Utils;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Algorithms.Batched.LALS;
import edu.usc.infolab.sc.Algorithms.Clairvoyant.Exact;
import edu.usc.infolab.sc.Algorithms.Online.BestDistributionAdhoc;
import edu.usc.infolab.sc.Algorithms.Online.BestDistributionEMD;
import edu.usc.infolab.sc.Algorithms.Online.BestDistributionJSD;
import edu.usc.infolab.sc.Algorithms.Online.BestInsertion;
import edu.usc.infolab.sc.Algorithms.Online.MostFreeTime;
import edu.usc.infolab.sc.Algorithms.Online.NearestNeighbor;
import edu.usc.infolab.sc.Algorithms.Online.Random;
import edu.usc.infolab.sc.Algorithms.Online.Ranking;
import edu.usc.infolab.sc.DataSetGenerators.DataGenerator;
import edu.usc.infolab.sc.Distributions.Poisson;
import edu.usc.infolab.sc.Distributions.PoissonConfig;
import edu.usc.infolab.sc.Logging.Log;
import edu.usc.infolab.sc.Logging.Result;

public class Main {
	private static final String GENERAL = "GENERAL";
	private static final String ASSIGNMENT_STAT = "ASSIGNMENT_STAT";
	
	public static void main(String[] args) {
		String input = "SkewedTasks_4";
		Initialize(0, input);
		
		//RunMultipleTests(input, 20);
		//ChangeRateOfTasks(input);
		//ChangeNumberOfTasks(input);
		/*String[] cities = new String[]{"LA", "NY", "London", "Paris", "Beijing"};
		for (String city : cities) {
			for (int i = 0; i < 20; i++) {
				RunOnlineAlgorithms(String.format("res\\Flickr\\flickr_realData_%s_15000_%d.xml", city, i));
			}
		}*/
		//ChangeSkewnessLevel(input);
		//RunMultipleBatchedVsOnlineTests(input, 20);
		//ChangeRateOfTasksAndWorkers(input);
		String result = RunBatchedVsOnline("foursquare_realData_LA.xml");
		Result.Add(GENERAL, result);
		
		Finalize();
	}
	
	protected static void RunSingleTests(String config) {
		String input = GenerateNewInput(config);
		System.out.println("Starting test");
		String algoResults = RunOnlineAlgorithms(input);
		Result.Add(GENERAL, algoResults);
	}
	
	protected static void RunSingleBatched(String config) {
		String input = GenerateNewInput(config);
		System.out.println("Starting test");
		String algoResults = RunLALS(input);
		Result.Add(GENERAL, algoResults);
	}
	
	protected static void RunMultipleBatchedVsOnlineTests(String config, int testSize) {
		for (int test = 0; test < testSize; test++) {
			String input = GenerateNewInput(test, config);
			System.out.println(String.format("Starting test %d", test));
			String algoResults = RunBatchedVsOnline(input);
			Result.Add(GENERAL, algoResults);			
		}
	}
	
	protected static void RunMultipleOnlineTests(String config, int testSize) {
		for (int test = 0; test < testSize; test++) {
			String input = GenerateNewInput(test, config);
			System.out.println(String.format("Starting test %d", test));
			String algoResults = RunOnlineAlgorithms(input);
			Result.Add(GENERAL, algoResults);			
		}
	}
	
	protected static void ChangeNumberOfTasks(String config) {
		//int size = 100;
		int[] sizes = {100, 500, 1000, 2500, 5000};
		//while (size <= 2000) {
		for (int size : sizes) {
			for (int test = 0; test < 5; test++) {
				String input = GenerateNewInput(test, config, size);
				System.out.println(String.format("Starting test %d for size %d", test, size));
				String algoResults = RunOnlineAlgorithms(input);
				Result.Add(GENERAL, "%d,%s", size, algoResults);
			}
			
			//int d = (int) Math.log10(size);
			//size += Math.pow(10, d);
			//size += 100;
		}						
	}
	
	protected static void ChangeRateOfTasks(String config) {
		double rate = 60;
		while (rate <= 300000 ) {
			for (int test = 0; test < 5; test++) {
				String input = GenerateNewInput(test, config, 1000, rate);
				System.out.println(String.format("Starting test %d for rate %.2f", test, rate));
				String algoResults = RunOnlineAlgorithms(input);
				Result.Add(GENERAL, "%.2f,%s", rate, algoResults);
			}
			
			rate*=10;
		}
	}
	
	protected static void ChangeRateOfWorkers(String config) {
		double rate = 1;
		while (rate <= 64) {
			for (int test = 0; test < 5; test++) {
				String input = GenerateNewInput(test, config, 1000, 4, rate);
				System.out.println(String.format("Starting test %d for rate %.2f", test, rate));
				String algoResults = RunOnlineAlgorithms(input);
				Result.Add(GENERAL, "%.2f,%s", rate, algoResults);
			}
			
			rate *= 2;
		}
	}
	
	protected static void ChangeRateOfTasksAndWorkers(String config) {
		for (double tRate = 1; tRate < 1000; tRate += Math.pow(10, Math.floor(Math.log10(tRate)))) {
			for (double wRate = 0.1; wRate < 100; wRate += Math.pow(10, Math.floor(Math.log10(wRate)))) {
				for (int test = 0; test < 5; test++) {
					String input = GenerateNewInput(test, config, Math.max(10000, (int)tRate*10), tRate, wRate);
					System.out.println(String.format("Starting test %d for tRate %.2f and wRate %.2f", test, tRate, wRate));
					//String algoResults = RunOnlineAlgorithms(input);
					String algoResults = RunBatchedVsOnline(input);
					Result.Add(GENERAL, "%.2f,%s", tRate, algoResults);
				}
			}
		}
	}
	
	protected static void ChangeSkewnessLevel(String config) {
		double[] levels = {0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
		for (double level : levels) {
			for (int test = 0; test < 10; test++) {
				String input = GenerateNewInput(test, config, 1000, 3, 20, level);
				System.out.println(String.format("Starting test %d for skewness %.2f", test, level));
				String algoResults = RunOnlineAlgorithms(input);
				Result.Add(GENERAL, "%.2f,%s", level, algoResults);
			}
		}
	}
	
	protected static String RunExactAlgorithm(String config) {
		String input = GenerateNewInput(config);
		InputParser ip = new InputParser(input);
		//Grid grid = ip.GetGrid();
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer, Worker> workers = ip.GetWorkers();
		
		Exact exactAlgo = new Exact(tasks, workers);
		int endTime = exactAlgo.Run();
		return Result.GenerateReport(new ArrayList<Worker>(workers.values()), new ArrayList<Task>(tasks.values()), endTime);
	}
	
	protected static String RunLALS(String input) {
		InputParser ip = new InputParser(input);
		
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer, Worker> workers = ip.GetWorkers();
		
		LALS lalsAlgo = new LALS(tasks, workers);
		int endTime = lalsAlgo.Run();
		SaveToFile(input+".LALS.txt", new ArrayList<Task>(tasks.values()), new ArrayList<Worker>(workers.values()));
		return Result.GenerateReport(new ArrayList<Worker>(workers.values()), new ArrayList<Task>(tasks.values()), endTime);
	}
	
	protected static String RunRandom(String input) {
		InputParser ip = new InputParser(input);
		Grid grid = ip.GetGrid();
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer, Worker> workers = ip.GetWorkers();
		
		Random rndAlgo = new Random(tasks, workers, grid.clone());
		int endTime = rndAlgo.Run();
		Result.Add(ASSIGNMENT_STAT, Result.GetAssignmentStats("Rnd", new ArrayList<Task>(tasks.values())));
		SaveToFile(input+".Random.txt", new ArrayList<Task>(tasks.values()), new ArrayList<Worker>(workers.values()));
		return Result.GenerateReport(new ArrayList<Worker>(workers.values()), new ArrayList<Task>(tasks.values()), endTime);
	}
	
	protected static String RunRanking(String input) {
		InputParser ip = new InputParser(input);
		Grid grid = ip.GetGrid();
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer, Worker> workers = ip.GetWorkers();
		
		Ranking rnkAlgo = new Ranking(tasks, workers, grid.clone());
		int endTime = rnkAlgo.Run();
		Result.Add(ASSIGNMENT_STAT, Result.GetAssignmentStats("Rnk", new ArrayList<Task>(tasks.values())));
		SaveToFile(input+".Ranking.txt", new ArrayList<Task>(tasks.values()), new ArrayList<Worker>(workers.values()));
		return Result.GenerateReport(new ArrayList<Worker>(workers.values()), new ArrayList<Task>(tasks.values()), endTime);
	}
	
	protected static String RunNearestNeighbor(String input) {
		InputParser ip = new InputParser(input);
		Grid grid = ip.GetGrid();
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer, Worker> workers = ip.GetWorkers();
		
		NearestNeighbor nnAlgo = new NearestNeighbor(tasks, workers, grid.clone());
		int endTime = nnAlgo.Run();
		Result.Add(ASSIGNMENT_STAT, Result.GetAssignmentStats("NN", new ArrayList<Task>(tasks.values())));
		SaveToFile(input+".NearestNeighbor.txt", new ArrayList<Task>(tasks.values()), new ArrayList<Worker>(workers.values()));
		return Result.GenerateReport(new ArrayList<Worker>(workers.values()), new ArrayList<Task>(tasks.values()), endTime);
	}
	
	protected static String RunBestInsertion(String input) {
		InputParser ip = new InputParser(input);
		Grid grid = ip.GetGrid();
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer, Worker> workers = ip.GetWorkers();
		
		BestInsertion biAlgo = new BestInsertion(tasks, workers, grid.clone());
		int endTime = biAlgo.Run();
		Result.Add(ASSIGNMENT_STAT, Result.GetAssignmentStats("BI", new ArrayList<Task>(tasks.values())));
		SaveToFile(input+".BestInsersion.txt", new ArrayList<Task>(tasks.values()), new ArrayList<Worker>(workers.values()));
		return Result.GenerateReport(new ArrayList<Worker>(workers.values()), new ArrayList<Task>(tasks.values()), endTime);
	}
	
	protected static String RunMostFreeTime(String input) {
		InputParser ip = new InputParser(input);
		Grid grid = ip.GetGrid();
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer, Worker> workers = ip.GetWorkers();
		
		MostFreeTime mftAlgo = new MostFreeTime(tasks, workers, grid.clone());
		int endTime = mftAlgo.Run();
		Result.Add(ASSIGNMENT_STAT, Result.GetAssignmentStats("MFT", new ArrayList<Task>(tasks.values())));
		SaveToFile(input+".MostFreeTime.txt", new ArrayList<Task>(tasks.values()), new ArrayList<Worker>(workers.values()));
		return Result.GenerateReport(new ArrayList<Worker>(workers.values()), new ArrayList<Task>(tasks.values()), endTime);
	}
	
	protected static String RunBestDistributionAdhoc(String input) {
		InputParser ip = new InputParser(input);
		Grid grid = ip.GetGrid();
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer, Worker> workers = ip.GetWorkers();
		
		double[] taskCount = new double[grid.size()];
		for (int i = 0; i < taskCount.length; ++i) {
			taskCount[i] = 0;
		}
		for (Task t : tasks.values()) {
			taskCount[grid.GetCell(t.location)]++;
		}
		CountDistribution distT = new CountDistribution(grid, taskCount);
		
		BestDistributionAdhoc bdAlgo = new BestDistributionAdhoc(tasks, workers, grid.clone(), new Object[]{distT});
		//BestDistribution bdAlgo = new BestDistribution(tasks, workers, grid.clone());
		int endTime = bdAlgo.Run();
		Result.Add(ASSIGNMENT_STAT, Result.GetAssignmentStats("BD", new ArrayList<Task>(tasks.values())));
		SaveToFile(input+".AdHoc.txt", new ArrayList<Task>(tasks.values()), new ArrayList<Worker>(workers.values()));
		return Result.GenerateReport(new ArrayList<Worker>(workers.values()), new ArrayList<Task>(tasks.values()), endTime);
	}
	
	protected static String RunBestDistributionEMD(String input) {
		InputParser ip = new InputParser(input);
		Grid grid = ip.GetGrid();
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer, Worker> workers = ip.GetWorkers();
		
		double[] taskCount = new double[grid.size()];
		for (int i = 0; i < taskCount.length; ++i) {
			taskCount[i] = 0;
		}
		for (Task t : tasks.values()) {
			taskCount[grid.GetCell(t.location)]++;
		}
		CountDistribution distT = new CountDistribution(grid, taskCount);
		
		BestDistributionEMD bdAlgo = new BestDistributionEMD(tasks, workers, grid.clone(), new Object[]{distT});
		//BestDistribution bdAlgo = new BestDistribution(tasks, workers, grid.clone());
		int endTime = bdAlgo.Run();
		Result.Add(ASSIGNMENT_STAT, Result.GetAssignmentStats("BD", new ArrayList<Task>(tasks.values())));
		SaveToFile(input+".EMD.txt", new ArrayList<Task>(tasks.values()), new ArrayList<Worker>(workers.values()));
		return Result.GenerateReport(new ArrayList<Worker>(workers.values()), new ArrayList<Task>(tasks.values()), endTime);
	}
	
	protected static String RunBestDistributionJSD(String input) {
		InputParser ip = new InputParser(input);
		Grid grid = ip.GetGrid();
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer, Worker> workers = ip.GetWorkers();
		
		double[] taskCount = new double[grid.size()];
		for (int i = 0; i < taskCount.length; ++i) {
			taskCount[i] = 0;
		}
		for (Task t : tasks.values()) {
			taskCount[grid.GetCell(t.location)]++;
		}
		CountDistribution distT = new CountDistribution(grid, taskCount);
		
		BestDistributionJSD bdAlgo = new BestDistributionJSD(tasks, workers, grid.clone(), new Object[]{distT});
		//BestDistribution bdAlgo = new BestDistribution(tasks, workers, grid.clone());
		int endTime = bdAlgo.Run();
		Result.Add(ASSIGNMENT_STAT, Result.GetAssignmentStats("BD", new ArrayList<Task>(tasks.values())));
		SaveToFile(input+".JSD.txt", new ArrayList<Task>(tasks.values()), new ArrayList<Worker>(workers.values()));
		return Result.GenerateReport(new ArrayList<Worker>(workers.values()), new ArrayList<Task>(tasks.values()), endTime);
	}
	
	protected static String RunBestDistribution(String input) {
		String adhocResult = RunBestDistributionAdhoc(input);
		String emdResult = RunBestDistributionEMD(input);
		String jsdResult = RunBestDistributionJSD(input);
		return String.format("%s,%s,%s", adhocResult, emdResult, jsdResult);
		//return "";
	}
	
	private static String RunOnlineAlgorithms(String input) {
		String rndResults = RunRandom(input);
		String rnkResults = RunRanking(input);
		String nnResults = RunNearestNeighbor(input);
		String mftResults = RunMostFreeTime(input);
		String biResults = RunBestInsertion(input);
		String bdResults = RunBestDistributionEMD(input);
		return String.format("%s,%s,%s,%s,%s,%s", rndResults, rnkResults, nnResults, biResults, mftResults, bdResults);
		//return String.format("%s,%s,%s", nnResults, biResults, bdResults);
		//return "";
	}
	
	private static String RunBatchedVsOnline(String input) {
		//String biResults = RunBestInsertion(input);
		String biResults = "";
		String lalsResults = RunLALS(input);
		return String.format("%s,%s", biResults, lalsResults);
	}
	
	private static String GenerateNewInput(String config) {
		File inputFile = new File(config, "input.xml");
		DataGenerator.GenerateData(String.format("%s.xml", config), inputFile.getPath());

		return inputFile.getPath();
	}

	private static String GenerateNewInput(int test, String config) {
		File inputFile = new File(config, String.format("input%03d.xml", test));
		DataGenerator.GenerateData(String.format("%s.xml", config), inputFile.getPath());
		return inputFile.getPath();
	}
	
	private static String GenerateNewInput(int test, String config, int tasksSize) {
		File inputFile = new File(config, String.format("input%03d_%05d.xml", test, tasksSize));
		DataGenerator.GenerateData(String.format("%s.xml", config), inputFile.getPath(), tasksSize);
		return inputFile.getPath();
	}
	
	private static String GenerateNewInput(int test, String config, int tasksSize, double tasksRate) {
		File inputFile = new File(config, String.format("input%03d_%05d_t%s.xml", test, tasksSize, String.format("%.1f", tasksRate).replace(".", "")));
		//Exponential tasksReleaseDist = new Exponential(new ExponentialConfig(tasksRate));
		Poisson tasksReleaseDist = new Poisson(new PoissonConfig(tasksRate));
		DataGenerator.GenerateData(String.format("%s.xml", config), inputFile.getPath(), tasksSize, tasksReleaseDist);
		return inputFile.getPath();
	}
	
	private static String GenerateNewInput(int test, String config, int tasksSize, double tasksRate, double workerRate) {
		File inputFile = new File(config, String.format("input%03d_%05d_t%s_w%s.xml", test, tasksSize, String.format("%.1f", tasksRate).replace(".", ""), String.format("%.1f", workerRate).replace(".", "")));
		//Exponential tasksReleaseDist = new Exponential(new ExponentialConfig(tasksRate));
		Poisson tasksReleaseDist = new Poisson(new PoissonConfig(tasksRate));
		Poisson workerReleaseDist = new Poisson(new PoissonConfig(workerRate));
		DataGenerator.GenerateData(String.format("%s.xml", config), inputFile.getPath(), tasksSize, tasksReleaseDist, workerReleaseDist);
		return inputFile.getPath();
	}
	
	private static String GenerateNewInput(int test, String config, int tasksSize, double taskRate, double workerRate, double skewness) {
		java.util.Random rand = new java.util.Random();
		int numOfGausClusters = 4;
		double[] probs = new double[numOfGausClusters];
		double sum = 0;
		for (int i = 0; i < numOfGausClusters; i++) {
			probs[i] = rand.nextDouble();
			sum += probs[i];
		}
		for (int i = 0; i < numOfGausClusters; i++) {
			probs[i] = (probs[i] * skewness) / sum;
		}
		File inputFile = new File(config, String.format("input%03d_%05d_s%s.xml", test, tasksSize, String.format("%.1f", skewness).replace(".", "")));
		DataGenerator.GenerateData(String.format("%s.xml", config), inputFile.getPath(), tasksSize, probs);
		return inputFile.getPath();
	}
	
	private static void Initialize(int level, String config) {
		try {
			String srcFileName = String.format("%s.xml", config);
			File dir = Utils.CreateEmptyDirectory(config);
			FileUtils.copyFile(new File(srcFileName), new File(dir, srcFileName));
			
			File f = new File(dir, config);
			Log.Initialize(level, f.getPath());
			Result.InitNewWriter(GENERAL, String.format("%s_%s", f.getPath(), GENERAL));
			Result.InitNewWriter(ASSIGNMENT_STAT, String.format("%s_%s", f.getPath(), ASSIGNMENT_STAT));
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private static void SaveToFile(String name, ArrayList<Task> tasks, ArrayList<Worker> workers) {
		try {
			FileWriter fw = new FileWriter(name);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(String.format("%d", tasks.size()));
			bw.write("\n");
			for (Task t : tasks) {
				String p1 = String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,", 
						t.assignmentStat.assigned, t.assignmentStat.completed, 
						t.releaseFrame, t.retractFrame, t.value, 
						t.assignmentStat.decideEligibilityTime, t.assignmentStat.selectWorkerTime, t.assignmentStat.totalTime, 
						t.assignmentStat.availableWorkers, t.assignmentStat.eligibleWorkers);
				StringBuilder sb = new StringBuilder();
				for (Long i : t.assignmentStat.workerFreeTimes) {
					if (!i.toString().equals("0")) {
						sb.append(i.toString());
						sb.append(";");
					}
				}
				String p2 = sb.toString();
				if (p2.length() > 0)
					p2 = p2.substring(0, p2.length()-1);
				bw.write(p1);
				bw.write(p2);
				bw.write("\n");
			}
			bw.write(String.format("%d",workers.size()));
			bw.write("\n");
			for (Worker w : workers) {
				String p1 = String.format("%d,%d,%.2f,%d,%d,%d",
						w.releaseFrame, w.retractFrame, w.travledDistance, w.maxNumberOfTasks, 
						w.GetAssignedTasks().size(), w.GetCompletedTasks().size());
				bw.write(p1);
				bw.write("\n");
			}
			bw.close();
			fw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static void Finalize() {
		Log.Finalize();
		Result.FinalizeAll();
	}
}
