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
import edu.usc.infolab.sc.Algorithms.Online.Greedy;
import edu.usc.infolab.sc.Algorithms.Online.NearestNeighbor;

public class Main {
	
	public static Grid grid;
	private static HashMap<Integer, Task> _tasks;
	private static HashMap<Integer, Worker> _workers;

	public static void main(String[] args) {
		String input = "UniformTasks_500";
		
		Main.Initialize(1, input);
		
		for (int test = 0; test < 50; test++) {
			String testInput = GenerateNewInput(test, input);
			InputParser ip = new InputParser(testInput);
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
			
			
			tasks = GetTasksCopy();
			workers = GetWorkersCopy();
			Greedy grAlgo = new Greedy(tasks, workers, grid.clone());
			grAlgo.Run();
			String grResutls = Result.GenerateReport(new ArrayList<Worker>(workers.values()));
			
			tasks = GetTasksCopy();
			workers = GetWorkersCopy();
			NearestNeighbor nnAlgo = new NearestNeighbor(tasks, workers, grid.clone());
			nnAlgo.Run();
			String nnResutls = Result.GenerateReport(new ArrayList<Worker>(workers.values()));
			
			tasks = GetTasksCopy();
			workers = GetWorkersCopy();
			BestInsertion biAlgo = new BestInsertion(tasks, workers, grid.clone());
			biAlgo.Run();
			String biResutls = Result.GenerateReport(new ArrayList<Worker>(workers.values()));
			
			tasks = GetTasksCopy();
			workers = GetWorkersCopy();
			BestDistribution bdAlgo = new BestDistribution(tasks, workers, grid.clone(), new Object[]{distT});
			bdAlgo.Run();
			String bdResutls = Result.GenerateReport(new ArrayList<Worker>(workers.values()));
			
			Result.Add("%s,%s,%s,%s", grResutls, nnResutls, biResutls, bdResutls);			
		}
		
		Main.Finalize();
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
		edu.usc.infolab.sc.DataSetGenerators.Main.GenerateData(String.format("%s.xml", input), inputFile.getPath());
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
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private static void Finalize() {
		Log.Finalize();
		Result.Finalize();
	}
}
