package edu.usc.infolab.sc.Main;

import java.util.HashMap;

import edu.usc.infolab.sc.CountDistribution;
import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Algorithms.Online.BestDistribution;

public class Main {
	
	public static Grid grid;

	public static void main(String[] args) {
		String input = "T50000_3";
		Log.Initialize(input);
		
		InputParser ip = new InputParser(String.format("%s.xml", input));
		grid = ip.GetGrid();
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer,Worker> workers = ip.GetWorkers();
		
		
		//BestPTSFirst algo = new BestPTSFirst(tasks, workers);
		//GoodPTSFirst algo = new GoodPTSFirst(tasks, workers);
		
		//Greedy algo = new Greedy(tasks, workers);
		//NearestNeighbor algo = new NearestNeighbor(tasks, workers);
		BestDistribution algo = new BestDistribution(tasks, workers, new Object[]{grid, new CountDistribution(grid.size(), false)});
		algo.Run();
		
		
		Result.GenerateReport();
		
		Log.Finalize();
	}
}
