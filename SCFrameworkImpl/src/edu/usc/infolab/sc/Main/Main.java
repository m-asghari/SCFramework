package edu.usc.infolab.sc.Main;

import java.util.HashMap;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Clairvoyant.ExactAlgorithm;

public class Main {

	public static void main(String[] args) {
		Log.Initialize();
		
		InputParser ip = new InputParser("W50T250_1.xml");
		//Grid grid = ip.GetGrid();
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer,Worker> workers = ip.GetWorkers();
		ExactAlgorithm exactAlgo = new ExactAlgorithm(tasks, workers);
		exactAlgo.Run();
		
		Log.Finalize();
	}

}
