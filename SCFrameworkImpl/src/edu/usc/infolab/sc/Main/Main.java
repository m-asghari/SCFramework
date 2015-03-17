package edu.usc.infolab.sc.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import edu.usc.infolab.sc.CountDistribution;
import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Utils;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Algorithms.Online.BestDistribution;
import edu.usc.infolab.sc.Algorithms.Online.BestInsertion;
import edu.usc.infolab.sc.Algorithms.Online.NearestNeighbor;
import edu.usc.infolab.sc.Algorithms.Online.Ranking;
import edu.usc.infolab.sc.DataSetGenerators.DataGenerator;
import edu.usc.infolab.sc.Distributions.Exponential;
import edu.usc.infolab.sc.Distributions.ExponentialConfig;
import edu.usc.infolab.sc.Logging.FrameStats;
import edu.usc.infolab.sc.Logging.Log;
import edu.usc.infolab.sc.Logging.Result;

public class Main {
	
	public static Grid grid;
	private static HashMap<Integer, Task> _tasks;
	private static HashMap<Integer, Worker> _workers;

	public static void main(String[] args) {
		String input = "SkewedTasks_T1_W20";
		Initialize(-1, input);
		
		ChangeNumberOfTasks(input);
		
		Finalize();
	}
	
	protected static void RunMultipleTests(String input, int testSize) {
		for (int test = 0; test < testSize; test++) {
			String testInput = GenerateNewInput(test, input);
			String algoResults = RunAllAlgorithms(testInput);
			Result.Add(algoResults);			
		}
	}
	
	protected static void ChangeNumberOfTasks(String input) {
		int size = 50000;
		while (size <= 50000) {
			for (int test = 0; test < 20; test++) {
				String testInput = GenerateNewInput(test, input, size);
				System.out.println(String.format("Starting test %d for size %d", test, size));
				String algoResults = RunAllAlgorithms(testInput);
				Result.Add("%d,%s", size, algoResults);
			}
			
			int d = (int) Math.log10(size);
			size += Math.pow(10, d);
		}						
	}
	
	protected static void ChangeNumberOfAvailableWorkers(String input) {
		int availableWorkers = 1;
		while (availableWorkers <= 100) {
			for (int test = 0; test < 20; test++) {
				String testInput = GenerateNewInput(test, input, 1000, availableWorkers);
				System.out.println(String.format("Starting test %d for availableWorlers %d", test, availableWorkers));
				FrameStats.Add("%d,%d", availableWorkers, test);
				String algoResults = RunAllAlgorithms(testInput);
				Result.Add("%d,%s", availableWorkers, algoResults);
			}
			
			availableWorkers = (availableWorkers < 20) ? availableWorkers + 1 : availableWorkers + 10;
		}						
	}
	
	protected static void ChangeRateOfTasks(String input) {
		double rate = 0.5;
		while (rate < 5) {
			for (int test = 0; test < 20; test++) {
				String testInput = GenerateNewInput(test, input, 1000, 10, rate);
				System.out.println(String.format("Starting test %d for rate %d", test, rate));
				String algoResults = RunAllAlgorithms(testInput);
				Result.Add("%d,%s", rate, algoResults);
			}
			
			if (rate < 1) {
				rate += 0.05;
			}
			else if (rate < 2) {
				rate += 0.1;
			}
			else {
				rate += 1;
			}
		}
	}
	
	private static String RunAllAlgorithms(String input) {
		InputParser ip = new InputParser(input);
		grid = ip.GetGrid();
		_tasks = ip.GetTasks();
		_workers = ip.GetWorkers();
		
		double[] taskCount = new double[grid.size()];
		for (int i = 0; i < taskCount.length; ++i) {
			taskCount[i] = 0;
		}
		for (Task t : _tasks.values()) {
			taskCount[grid.GetCell(t.location)]++;
		}
		CountDistribution distT = new CountDistribution(grid, taskCount);
		
		HashMap<Integer, Task> tasks;
		HashMap<Integer, Worker> workers;
		int endTime = 0;
		
		tasks = GetTasksCopy();
		workers = GetWorkersCopy();
		Ranking rnkAlgo = new Ranking(tasks, workers, grid.clone());
		endTime = rnkAlgo.Run();
		String rnkResutls = Result.GenerateReport(new ArrayList<Worker>(workers.values()), endTime);
		
		tasks = GetTasksCopy();
		workers = GetWorkersCopy();
		NearestNeighbor nnAlgo = new NearestNeighbor(tasks, workers, grid.clone());
		endTime = nnAlgo.Run();
		String nnResutls = Result.GenerateReport(new ArrayList<Worker>(workers.values()), endTime);
		
		tasks = GetTasksCopy();
		workers = GetWorkersCopy();
		BestInsertion biAlgo = new BestInsertion(tasks, workers, grid.clone());
		endTime = biAlgo.Run();
		String biResutls = Result.GenerateReport(new ArrayList<Worker>(workers.values()), endTime);
		
		tasks = GetTasksCopy();
		workers = GetWorkersCopy();
		BestDistribution bdAlgo = new BestDistribution(tasks, workers, grid.clone(), new Object[]{distT});
		endTime = bdAlgo.Run();
		String bdResutls = Result.GenerateReport(new ArrayList<Worker>(workers.values()), endTime);
		
		return String.format("%s,%s,%s,%s", rnkResutls, nnResutls, biResutls, bdResutls);
	}
	
	private static HashMap<Integer, Task> GetTasksCopy() {
		HashMap<Integer, Task> tasks = new HashMap<Integer, Task>();
		for (Entry<Integer, Task> e : _tasks.entrySet()) {
			tasks.put(e.getKey(), e.getValue().clone());
		}
		return tasks;
	}
	
	private static HashMap<Integer, Worker> GetWorkersCopy() {
		HashMap<Integer, Worker> workers = new HashMap<Integer, Worker>();
		for (Entry<Integer, Worker> e : _workers.entrySet()) {
			workers.put(e.getKey(), e.getValue().clone());
		}
		return workers;
	}

	private static String GenerateNewInput(int test, String input) {
		File inputFile = new File(input, String.format("input%03d.xml", test));
		DataGenerator.GenerateData(String.format("%s.xml", input), inputFile.getPath());
		return inputFile.getPath();
	}
	
	private static String GenerateNewInput(int test, String input, int tasksSize) {
		File inputFile = new File(input, String.format("input%03d_%05d.xml", test, tasksSize));
		DataGenerator.GenerateData(String.format("%s.xml", input), inputFile.getPath(), tasksSize);
		return inputFile.getPath();
	}
	
	private static String GenerateNewInput(int test, String input, int tasksSize, int availableWorkers) {
		File inputFile = new File(input, String.format("input%03d_%05d_w%03d.xml", test, tasksSize, availableWorkers));
		DataGenerator.GenerateData(String.format("%s.xml", input), inputFile.getPath(), tasksSize, availableWorkers);
		return inputFile.getPath();
	}
	
	private static String GenerateNewInput(int test, String input, int tasksSize, int availableWorkers, double tasksRate) {
		File inputFile = new File(input, String.format("input%03d_%05d_w%03d_t%s.xml", test, tasksSize, availableWorkers, String.format("%.1f", tasksRate).replace(".", "")));
		Exponential tasksReleaseDist = new Exponential(new ExponentialConfig(tasksRate));
		DataGenerator.GenerateData(String.format("%s.xml", input), inputFile.getPath(), tasksSize, availableWorkers, tasksReleaseDist);
		return inputFile.getPath();
	}
	
	private static void Initialize(int level, String input) {
		try {
			String srcFileName = String.format("%s.xml", input);
			File dir = Utils.CreateEmptyDirectory(input);
			FileUtils.copyFile(new File(srcFileName), new File(dir, srcFileName));
			
			File f = new File(dir, input);
			Log.Initialize(level, f.getPath());
			Result.Initialize(f.getPath());
			FrameStats.Initialize(String.format("%s_frameStats", f.getPath()));
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private static void Finalize() {
		Log.Finalize();
		Result.Finalize();
		FrameStats.Finalize();
	}
}
