package edu.usc.infolab.sc.Main;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.CountDistribution;
import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Algorithms.Online.BestDistribution;

public class Main {
	
	public static Grid grid;

	public static void main(String[] args) {
		String input = "SampleOutput";
		Log.Initialize(1, input);
		
		InputParser ip = new InputParser(String.format("%s.xml", input));
		grid = ip.GetGrid();
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer,Worker> workers = ip.GetWorkers();
		
		double[] taskCount = new double[grid.size()];
		for (int i = 0; i < taskCount.length; ++i) {
			taskCount[i] = 0;
		}
		for (Task t : tasks.values()) {
			taskCount[grid.GetCell(t.location)]++;
		}
		CountDistribution distT = new CountDistribution(grid, taskCount);
		//Log.Add(distT.toString());
		
		//BestPTSFirst algo = new BestPTSFirst(tasks, workers);
		//GoodPTSFirst algo = new GoodPTSFirst(tasks, workers);
		
		//Greedy algo = new Greedy(tasks, workers, grid);
		//NearestNeighbor algo = new NearestNeighbor(tasks, workers, grid);
		//BestInsertion algo = new BestInsertion(tasks, workers, grid);
		BestDistribution algo = new BestDistribution(tasks, workers, grid, new Object[]{distT});
		algo.Run();
		
		Result.GenerateReport(new ArrayList<Worker>(workers.values()));
		
		Log.Finalize();
	}
}
