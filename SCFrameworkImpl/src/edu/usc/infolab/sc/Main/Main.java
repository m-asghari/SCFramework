package edu.usc.infolab.sc.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import edu.usc.infolab.sc.CountDistribution;
import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Utils;
import edu.usc.infolab.sc.Worker;
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
import edu.usc.infolab.sc.Distributions.Exponential;
import edu.usc.infolab.sc.Distributions.ExponentialConfig;
import edu.usc.infolab.sc.Distributions.Poisson;
import edu.usc.infolab.sc.Distributions.PoissonConfig;
import edu.usc.infolab.sc.Logging.FrameStats;
import edu.usc.infolab.sc.Logging.Log;
import edu.usc.infolab.sc.Logging.Result;

public class Main {
	private static final String GENERAL = "GENERAL";
	private static final String ASSIGNMENT_STAT = "ASSIGNMENT_STAT";
	
	public static void main(String[] args) {
		String input = "SkewedTasks_2";
		Initialize(5, input);
		
		//RunMultipleTests(input, 100);
		ChangeRateOfTasks(input);
		
		Finalize();
	}
	
	protected static void RunSingleTests(String config) {
		String input = GenerateNewInput(config);
		System.out.println("Starting test");
		String algoResults = RunOnlineAlgorithms(input);
		Result.Add(GENERAL, algoResults);
	}
	
	protected static void RunMultipleTests(String config, int testSize) {
		for (int test = 0; test < testSize; test++) {
			String input = GenerateNewInput(test, config);
			System.out.println(String.format("Starting test %d", test));
			String algoResults = RunOnlineAlgorithms(input);
			Result.Add(GENERAL, algoResults);			
		}
	}
	
	protected static void ChangeNumberOfTasks(String config) {
		int size = 100;
		while (size <= 2000) {
			for (int test = 0; test < 20; test++) {
				String input = GenerateNewInput(test, config, size);
				System.out.println(String.format("Starting test %d for size %d", test, size));
				String algoResults = RunOnlineAlgorithms(input);
				Result.Add(GENERAL, "%d,%s", size, algoResults);
			}
			
			//int d = (int) Math.log10(size);
			//size += Math.pow(10, d);
			size += 100;
		}						
	}
	
	protected static void ChangeNumberOfAvailableWorkers(String config) {
		int availableWorkers = 1;
		while (availableWorkers <= 40) {
			for (int test = 0; test < 20; test++) {
				String input = GenerateNewInput(test, config, 1000, availableWorkers);
				System.out.println(String.format("Starting test %d for availableWorlers %d", test, availableWorkers));
				FrameStats.Add("%d,%d", availableWorkers, test);
				String algoResults = RunOnlineAlgorithms(input);
				Result.Add(GENERAL, "%d,%s", availableWorkers, algoResults);
			}
			
			availableWorkers = (availableWorkers < 40) ? availableWorkers + 1 : availableWorkers + 10;
		}						
	}
	
	protected static void ChangeRateOfTasks(String config) {
		double rate = 0.5;
		while (rate <= 1024 ) {
			for (int test = 0; test < 10; test++) {
				String input = GenerateNewInput(test, config, 1000, 10, rate);
				System.out.println(String.format("Starting test %d for rate %.2f", test, rate));
				String algoResults = RunOnlineAlgorithms(input);
				Result.Add(GENERAL, "%.2f,%s", rate, algoResults);
			}
			
			rate *= 2;
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
	
	protected static String RunRandom(String input) {
		InputParser ip = new InputParser(input);
		Grid grid = ip.GetGrid();
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer, Worker> workers = ip.GetWorkers();
		
		Random rndAlgo = new Random(tasks, workers, grid.clone());
		int endTime = rndAlgo.Run();
		Result.Add(ASSIGNMENT_STAT, Result.GetAssignmentStats("Rnd", new ArrayList<Task>(tasks.values())));
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
		String rnkResults = RunRanking(input);
		String nnResults = RunNearestNeighbor(input);
		String biResults = RunBestInsertion(input);
		String bdResults = RunBestDistribution(input);
		String mftResults = RunMostFreeTime(input);
		return String.format("%s,%s,%s,%s,%s", rnkResults, nnResults, biResults, mftResults, bdResults);
		//return "";
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
	
	private static String GenerateNewInput(int test, String config, int tasksSize, int availableWorkers) {
		File inputFile = new File(config, String.format("input%03d_%05d_w%03d.xml", test, tasksSize, availableWorkers));
		DataGenerator.GenerateData(String.format("%s.xml", config), inputFile.getPath(), tasksSize, availableWorkers);
		return inputFile.getPath();
	}
	
	private static String GenerateNewInput(int test, String config, int tasksSize, int availableWorkers, double tasksRate) {
		File inputFile = new File(config, String.format("input%03d_%05d_w%03d_t%s.xml", test, tasksSize, availableWorkers, String.format("%.1f", tasksRate).replace(".", "")));
		//Exponential tasksReleaseDist = new Exponential(new ExponentialConfig(tasksRate));
		Poisson tasksReleaseDist = new Poisson(new PoissonConfig(tasksRate));
		DataGenerator.GenerateData(String.format("%s.xml", config), inputFile.getPath(), tasksSize, availableWorkers, tasksReleaseDist);
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
	
	private static void Finalize() {
		Log.Finalize();
		Result.FinalizeAll();
	}
}
