package edu.usc.infolab.sc.DataSetGenerators;

import java.util.ArrayList;
import java.util.Collections;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class Main {
	
	private static enum ReleaseMode {
		Independent, InterArrival, Available
	};
	
	public static void main(String[] args) {
		GenerateData("UniformInput.xml", "SampleOutput.xml");
	}
	
	public static TaskGenerator GetTaskGenerator(String inputFile) {
		Document input = IO.ReadXML(inputFile);
		Element dataSpec = input.getDocumentElement();
		Element iTasks = (Element) dataSpec.getElementsByTagName("Tasks").item(0);
		return new TaskGenerator(iTasks);
	}

	public static WorkerGenerator GetWorkerGenerator(String inputFile) {
		Document input = IO.ReadXML(inputFile);
		Element dataSpec = input.getDocumentElement();
		Element iWorkers = (Element) dataSpec.getElementsByTagName("Workers").item(0);
		return new WorkerGenerator(iWorkers);
	}
	
	public static ArrayList<Task> GenerateInterArrivalTasks(TaskGenerator tg, int size) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		Task initTask = new Task();
		initTask.location = tg.NextLocation();
		initTask.releaseFrame = 0;
		initTask.retractFrame = 0 + tg.NextDuration();
		initTask.value = tg.NextValue();
		tasks.add(initTask);
		
		int lastTime = 0;
		for (int i = 1; i < size; i++) {
			Task t = new Task();
			t.location = tg.NextLocation();
			lastTime += tg.NextRelease();
			t.releaseFrame = lastTime;
			t.retractFrame = lastTime + tg.NextDuration();
			t.value = tg.NextValue();
			tasks.add(t);
		}
		
		return tasks;
	}
	
	public static ArrayList<Task> GenerateTasks(TaskGenerator tg, int size) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		
		for (int i = 0; i < size; i++) {
			Task t = new Task();
			t.location = tg.NextLocation();
			t.releaseFrame = tg.NextRelease();
			t.retractFrame = t.releaseFrame + tg.NextDuration();
			t.value = tg.NextValue();
			tasks.add(t);
		}
		
		return tasks;
	}
	
	public static ArrayList<Worker> GenerateInterArrivalWorkers(WorkerGenerator wg, int cutOffTime) {
		ArrayList<Worker> workers = new ArrayList<Worker>();
		Worker initWorker = new Worker();
		initWorker.location = wg.NextLocation();
		initWorker.releaseFrame = 0;
		initWorker.retractFrame = 0 + wg.NextDuration();
		initWorker.maxNumberOfTasks = wg.NextNumOfTasks();
		workers.add(initWorker);
		
		int lastTime = 0;
		while (lastTime <= cutOffTime) {
			Worker w = new Worker();
			w.location = wg.NextLocation();
			lastTime += wg.NextRelease();
			w.releaseFrame = lastTime;
			w.retractFrame = lastTime + wg.NextDuration();
			w.maxNumberOfTasks = wg.NextNumOfTasks();
			workers.add(w);
		}
		
		return workers;
	}
	
	public static ArrayList<Worker> GenerateWorkers(WorkerGenerator wg, int cutOffTime) {
		ArrayList<Worker> workers = new ArrayList<Worker>();
		
		int lastTime = 0;
		while (lastTime <= cutOffTime) {
			Worker w = new Worker();
			w.location = wg.NextLocation();
			lastTime = wg.NextRelease();
			w.releaseFrame = lastTime;
			w.retractFrame = lastTime + wg.NextDuration();
			w.maxNumberOfTasks = wg.NextNumOfTasks();
			workers.add(w);
		}
		
		return workers;
	}
	
	public static ArrayList<Worker> GenerateWorkers(WorkerGenerator wg, int cutOffTime, int availableWorkers) {
		ArrayList<Worker> workers = new ArrayList<Worker>();
		
		int[] numOfWorkers = new int[cutOffTime];
		for (int t = 0; t < cutOffTime; t++) {
			numOfWorkers[t] = 0;
		}
		
		for (int i = 0; i < availableWorkers; i++) {
			Worker w = new Worker();
			w.location = wg.NextLocation();
			w.releaseFrame = 0;
			w.retractFrame = wg.NextDuration();
			w.maxNumberOfTasks = wg.NextNumOfTasks();
			for (int t = w.releaseFrame; t <= w.retractFrame; t++) {
				numOfWorkers[t]++;
			}
			workers.add(w);
		}
		
		int t = 0;
		while (t < cutOffTime) {
			if (numOfWorkers[t] < availableWorkers) {
				Worker w = new Worker();
				w.location = wg.NextLocation();
				w.releaseFrame = t + wg.NextRelease();
				w.retractFrame = w.releaseFrame + wg.NextDuration();
				w.maxNumberOfTasks = wg.NextNumOfTasks();
				for (int r = w.releaseFrame; r <= w.retractFrame; r++) {
					numOfWorkers[r]++;
				}
				workers.add(w);
			}
			else {
				t++;
			}
		}
		
		return workers;
	}
	
	public static void SaveData(Grid grid, ArrayList<Task> tasks, ArrayList<Worker> workers, String outputFile) {
		Document output = IO.GetEmptyDoc();
		Element data = output.createElement("Data");
		output.appendChild(data);
		
		Element oGrid = output.createElement("Grid");
		oGrid = grid.Fill(oGrid);
		data.appendChild(oGrid);
		
		Element oTasks = output.createElement("Tasks");
		data.appendChild(oTasks);
		
		Collections.sort(tasks);
		
		for (Task t : tasks) {
			Element task = output.createElement("Task");
			task = t.Fill(task);
			oTasks.appendChild(task);
		}
		
		Element oWorkers = output.createElement("Workers");
		data.appendChild(oWorkers);
		
		Collections.sort(workers);
		
		for (Worker w : workers) {
			Element worker = output.createElement("Worker");
			worker = w.Fill(worker);
			oWorkers.appendChild(worker);
		}
		
		IO.SaveXML(output, outputFile);		
	}

	private static ReleaseMode GetReleaseMode(Element e) {
		String str = e.getAttribute("releaseMode");
		if (str.equals("Available")) {
			return ReleaseMode.Available;
		}
		else if (str.equals("InterArrival")) {
			return ReleaseMode.InterArrival;
		}
		else return ReleaseMode.Independent;		
	}

	public static void GenerateData(String inputFile, String outputFile) {
		Document input = IO.ReadXML(inputFile);
		Element dataSpec = input.getDocumentElement();
		
		Document output = IO.GetEmptyDoc();
		Element data = output.createElement("Data");
		output.appendChild(data);
		
		Element gridElement = (Element) dataSpec.getElementsByTagName("Grid").item(0);
		Node oGrid = gridElement.cloneNode(true);
		output.adoptNode(oGrid);
		data.appendChild(oGrid);
		
		Element iTasks = (Element) dataSpec.getElementsByTagName("Tasks").item(0);
		int tasksSize = Integer.parseInt(iTasks.getAttribute("size"));
		ReleaseMode taskReleaseMode = GetReleaseMode(iTasks); 
		TaskGenerator tg = new TaskGenerator(iTasks);
		
		Element oTasks = output.createElement("Tasks");
		data.appendChild(oTasks);
		
		ArrayList<Task> tasks = new ArrayList<Task>();
		switch (taskReleaseMode) {
		case Independent:
			tasks = GenerateTasks(tg, tasksSize);
			break;
		case InterArrival:
			tasks = GenerateInterArrivalTasks(tg, tasksSize);
		default:
			break;
		}
		
		Collections.sort(tasks);
		
		int cutOffTime = 0;
		for (Task t : tasks) {
			Element task = output.createElement("Task");
			task = t.Fill(task);
			cutOffTime = t.releaseFrame;
			oTasks.appendChild(task);
		}
		
		Element iWorkers = (Element) dataSpec.getElementsByTagName("Workers").item(0);
		ReleaseMode workerRleaseMode = GetReleaseMode(iWorkers);
		WorkerGenerator wg = new WorkerGenerator(iWorkers);
		
		Element oWorkers = output.createElement("Workers");
		data.appendChild(oWorkers);
		
		ArrayList<Worker> workers = new ArrayList<Worker>();
		switch (workerRleaseMode) {
		case Independent:
			workers = GenerateWorkers(wg, cutOffTime);
			break;
		case InterArrival:
			workers = GenerateInterArrivalWorkers(wg, cutOffTime);
			break;
		case Available:
			int availableWorkers = Integer.parseInt(iWorkers.getAttribute("available"));
			workers = GenerateWorkers(wg, cutOffTime, availableWorkers);
			break;
		default:
			break;
		}
		
		Collections.sort(workers);
		
		for (Worker w : workers) {
			Element worker = output.createElement("Worker");
			worker = w.Fill(worker);
			oWorkers.appendChild(worker);
		}
		
		IO.SaveXML(output, outputFile);
	}
}
