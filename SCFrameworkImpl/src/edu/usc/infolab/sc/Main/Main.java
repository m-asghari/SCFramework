package edu.usc.infolab.sc.Main;

import java.util.HashMap;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Clairvoyant.BestPTSFirst;
import edu.usc.infolab.sc.Clairvoyant.Exact;

public class Main {

	public static void main(String[] args) {
		Log.Initialize();
		
		InputParser ip = new InputParser("W10T50_4.xml");
		//Grid grid = ip.GetGrid();
		HashMap<Integer, Task> tasks = ip.GetTasks();
		HashMap<Integer,Worker> workers = ip.GetWorkers();
		//Exact exactAlgo = new Exact(tasks, workers);
		//exactAlgo.Run();
		BestPTSFirst bpfAlgo = new BestPTSFirst(tasks, workers);
		bpfAlgo.Run();
		
		Log.Finalize();
	}

}
