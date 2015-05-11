package edu.usc.infolab.sc.DataSetGenerators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.SpatialEntity;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Distributions.Distribution;

public class DataGenerator {
	
	public static enum ReleaseMode {
		Independent, InterArrival, Available, PerMinute
	};
	
	public static void main(String[] args) {
		GenerateData("UniformTasks.xml", "SampleOutput.xml");
	}
	
	public static ArrayList<Task> GenerateInterArrivalTasks(TaskGenerator tg, int size) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		Task initTask = new Task();
		initTask.location = tg.NextLocation();
		initTask.releaseFrame = 0;
		initTask.retractFrame = 0 + tg.NextDuration();
		initTask.value = tg.NextValue();
		tasks.add(initTask);
		
		Double lastTime = 0.0;
		for (int i = 1; i < size; i++) {
			Task t = new Task();
			t.location = tg.NextLocation();
			lastTime += tg.NextRelease();
			t.releaseFrame = lastTime.intValue();
			t.retractFrame = lastTime.intValue() + tg.NextDuration();
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
			t.releaseFrame = tg.NextRelease().intValue();
			t.retractFrame = t.releaseFrame + tg.NextDuration();
			t.value = tg.NextValue();
			tasks.add(t);
		}
		
		return tasks;
	}
	
	public static ArrayList<Task> GeneratePerMinuteTasks(TaskGenerator tg, int size) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		
		int totalCount = 0;
		int minute = 0;
		Random rand = new Random();
		
		while (totalCount < size) {
			int perMinuteCount = tg.NextRelease().intValue();
			for (int i = 0; i < perMinuteCount; i++) {
				Task t = new Task();
				t.location = tg.NextLocation();
				t.releaseFrame = 60 * minute + rand.nextInt(60);
				t.retractFrame = t.releaseFrame + tg.NextDuration();
				t.value = tg.NextValue();
				tasks.add(t);
				totalCount++;
			}
			minute++;
		}
		
		return tasks;
	}
	
	public static ArrayList<Worker> GenerateInterArrivalWorkers(WorkerGenerator wg, int cutOffTime, int availableWorkers) {
		ArrayList<Worker> workers = new ArrayList<Worker>();
		for (int i = 0; i < availableWorkers; i++) {
			Worker w = new Worker();
			w.location = wg.NextLocation();
			w.releaseFrame = 0;
			Random rand = new Random();
			Double rnd = rand.nextDouble();
			Double retractTime = rnd * wg.NextDuration();
			w.retractFrame = retractTime.intValue();
			Double mnot = rnd * wg.NextNumOfTasks();
			w.maxNumberOfTasks = mnot.intValue();
			workers.add(w);
		}
		
		Double lastTime = 0.0;
		while (lastTime <= cutOffTime) {
			Worker w = new Worker();
			w.location = wg.NextLocation();
			lastTime += wg.NextRelease();
			w.releaseFrame = lastTime.intValue();
			w.retractFrame = lastTime.intValue() + wg.NextDuration();
			w.maxNumberOfTasks = wg.NextNumOfTasks();
			workers.add(w);
		}
		
		return workers;
	}
	
	private static ArrayList<Worker> GenerateInitialWorkers(WorkerGenerator wg, int availableWorkers) {
		ArrayList<Worker> workers = new ArrayList<Worker>();
		
		Random rand = new Random();
		for (int i = 0; i < availableWorkers; i++) {
			Worker w = new Worker();
			w.location = wg.NextLocation();
			w.releaseFrame = 0;
			Double portion = rand.nextDouble();
			Double duration = portion * wg.NextDuration();
			w.retractFrame = duration.intValue();
			Double numOfTasks = portion * wg.NextNumOfTasks();
			w.maxNumberOfTasks = numOfTasks.intValue();
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
			for (int t = w.releaseFrame; t <= w.retractFrame && t < cutOffTime; t++) {
				numOfWorkers[t]++;
			}
			workers.add(w);
		}
		
		int t = 0;
		while (t < cutOffTime) {
			if (numOfWorkers[t] < availableWorkers) {
				Worker w = new Worker();
				w.location = wg.NextLocation();
				w.releaseFrame = t + wg.NextRelease().intValue();
				w.retractFrame = w.releaseFrame + wg.NextDuration();
				w.maxNumberOfTasks = wg.NextNumOfTasks();
				for (int r = w.releaseFrame; r <= w.retractFrame && r < cutOffTime; r++) {
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
	
	public static ArrayList<Worker> GeneratePerMinuteWorkers(WorkerGenerator wg, int cutOffTime, int availableWorkers) {
		ArrayList<Worker> workers = new ArrayList<Worker>();
		
		workers.addAll(GenerateInitialWorkers(wg, availableWorkers));
		
		int time = 0;
		int minute = 0;
		Random rand = new Random();
		
		while (time < cutOffTime) {
			int perMinuteCount = wg.NextRelease().intValue();
			for (int i = 0; i < perMinuteCount; i++) {
				Worker w = new Worker();
				w.location = wg.NextLocation();
				w.releaseFrame = 60 * minute + rand.nextInt(60);
				w.retractFrame = w.releaseFrame + wg.NextDuration();
				w.maxNumberOfTasks = wg.NextNumOfTasks();
				workers.add(w);
			}
			time += 60;
			minute++;
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
		else if (str.equals("PerMinute")) {
			return ReleaseMode.PerMinute;
		}
		else return ReleaseMode.Independent;		
	}
	
	private static Integer GetCutOffTime(ArrayList<? extends SpatialEntity> list) {
		Integer cutOffTime = 0;
		for (SpatialEntity se : list) {
			if (se.releaseFrame > cutOffTime)
				cutOffTime = se.releaseFrame;
		}
		return  cutOffTime;
	}

	public static void GenerateData(String inputFile, String outputFile) {
		Document input = IO.ReadXML(inputFile);
		Element dataSpec = input.getDocumentElement();
		
		Element gridElement = (Element) dataSpec.getElementsByTagName("Grid").item(0);
		Grid grid = new Grid(gridElement);
		
		Element iTasks = (Element) dataSpec.getElementsByTagName("Tasks").item(0);
		int tasksSize = Integer.parseInt(iTasks.getAttribute("size"));
		ReleaseMode tasksReleaseMode = GetReleaseMode(iTasks); 
		TaskGenerator tg = new TaskGenerator(iTasks, grid);
		
		Element iWorkers = (Element) dataSpec.getElementsByTagName("Workers").item(0);
		ReleaseMode workersReleaseMode = GetReleaseMode(iWorkers);
		WorkerGenerator wg = new WorkerGenerator(iWorkers, grid);
		int availableWorkers = 0;
		availableWorkers = Integer.parseInt(iWorkers.getAttribute("available"));
		
		GenerateData(grid, tg, tasksReleaseMode, tasksSize, wg, workersReleaseMode, availableWorkers, outputFile);
	}
	
	public static void GenerateData(String inputFile, String outputFile, int tasksSize) {
		Document input = IO.ReadXML(inputFile);
		Element dataSpec = input.getDocumentElement();
		
		Element gridElement = (Element) dataSpec.getElementsByTagName("Grid").item(0);
		Grid grid = new Grid(gridElement);
		
		Element iTasks = (Element) dataSpec.getElementsByTagName("Tasks").item(0);
		ReleaseMode tasksReleaseMode = GetReleaseMode(iTasks); 
		TaskGenerator tg = new TaskGenerator(iTasks, grid);
		
		Element iWorkers = (Element) dataSpec.getElementsByTagName("Workers").item(0);
		ReleaseMode workersReleaseMode = GetReleaseMode(iWorkers);
		WorkerGenerator wg = new WorkerGenerator(iWorkers, grid);
		int availableWorkers = 0;
		if (workersReleaseMode == ReleaseMode.Available) {
			availableWorkers = Integer.parseInt(iWorkers.getAttribute("available"));
		}
		
		GenerateData(grid, tg, tasksReleaseMode, tasksSize, wg, workersReleaseMode, availableWorkers, outputFile);
	}
	
	public static void GenerateData(String inputFile, String outputFile, int tasksSize, int availableWorkers) {
		Document input = IO.ReadXML(inputFile);
		Element dataSpec = input.getDocumentElement();
		
		Element gridElement = (Element) dataSpec.getElementsByTagName("Grid").item(0);
		Grid grid = new Grid(gridElement);
		
		Element iTasks = (Element) dataSpec.getElementsByTagName("Tasks").item(0);
		ReleaseMode tasksReleaseMode = GetReleaseMode(iTasks); 
		TaskGenerator tg = new TaskGenerator(iTasks, grid);
		
		Element iWorkers = (Element) dataSpec.getElementsByTagName("Workers").item(0);
		ReleaseMode workersReleaseMode = ReleaseMode.Available;
		WorkerGenerator wg = new WorkerGenerator(iWorkers, grid);
		
		GenerateData(grid, tg, tasksReleaseMode, tasksSize, wg, workersReleaseMode, availableWorkers, outputFile);
	}
	
	public static void GenerateData(String inputFile, String outputFile, int tasksSize, int availableWorkers, Distribution<Double> tasksReleaseDist) {
		Document input = IO.ReadXML(inputFile);
		Element dataSpec = input.getDocumentElement();
		
		Element gridElement = (Element) dataSpec.getElementsByTagName("Grid").item(0);
		Grid grid = new Grid(gridElement);
		
		Element iTasks = (Element) dataSpec.getElementsByTagName("Tasks").item(0);
		ReleaseMode tasksReleaseMode = GetReleaseMode(iTasks); 
		TaskGenerator tg = new TaskGenerator(iTasks, grid);
		tg.SetReleaseTimeDist(tasksReleaseDist);
		
		Element iWorkers = (Element) dataSpec.getElementsByTagName("Workers").item(0);
		ReleaseMode workersReleaseMode = ReleaseMode.Available;
		WorkerGenerator wg = new WorkerGenerator(iWorkers, grid);
		
		GenerateData(grid, tg, tasksReleaseMode, tasksSize, wg, workersReleaseMode, availableWorkers, outputFile);
	}
	
	private static void GenerateData(
			Grid grid,
			TaskGenerator tg, ReleaseMode tasksReleaseMode, int tasksSize,
			WorkerGenerator wg, ReleaseMode workersReleaseMode, int availableWorkers,
			String outputFile) {
		
		ArrayList<Task> tasks = new ArrayList<Task>();
		switch (tasksReleaseMode) {
		case Independent:
			tasks = GenerateTasks(tg, tasksSize);
			break;
		case InterArrival:
			tasks = GenerateInterArrivalTasks(tg, tasksSize);
			break;
		case PerMinute:
			tasks = GeneratePerMinuteTasks(tg, tasksSize);
			break;
		default:
			break;
		}
		
		int cutOffTime = GetCutOffTime(tasks);
		
		ArrayList<Worker> workers = new ArrayList<Worker>();
		switch (workersReleaseMode) {
		case InterArrival:
			workers = GenerateInterArrivalWorkers(wg, cutOffTime, availableWorkers);
			break;
		case Available:
			workers = GenerateWorkers(wg, cutOffTime, availableWorkers);
			break;
		case PerMinute:
			workers = GeneratePerMinuteWorkers(wg, cutOffTime, availableWorkers);
			break;
		default:
			break;
		}
		
		SaveData(grid, tasks, workers, outputFile);
	}
}
