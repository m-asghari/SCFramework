package edu.usc.infolab.sc.Main;

import java.util.HashMap;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Algorithms.Clairvoyant.BestPTSFirst;

public class Main {

	public static void main(String[] args) {
		String input = "W10T50_7";
		Log.Initialize(input);
		
		InputParser ip = new InputParser(String.format("%s.xml", input));
		//Grid grid = ip.GetGrid();
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer,Worker> workers = ip.GetWorkers();
		//Exact exactAlgo = new Exact(tasks, workers);
		//exactAlgo.Run();
		BestPTSFirst bpfAlgo = new BestPTSFirst(tasks, workers);
		bpfAlgo.Run();
		
		Result.GenerateReport();
		
		Log.Finalize();
	}

}
