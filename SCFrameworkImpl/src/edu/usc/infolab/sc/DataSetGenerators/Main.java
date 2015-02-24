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
	
	public static Grid grid;

	public static void main(String[] args) {
		GenerateData("SampleInput.xml", "SampleOutput.xml");
	}
	
	public static void GenerateData(String inputFile, String outputFile) {
		Document input = IO.ReadXML(inputFile);
		Element dataSpec = input.getDocumentElement();
		
		Document output = IO.GetEmptyDoc();
		Element data = output.createElement("Data");
		output.appendChild(data);
		
		Element gridElement = (Element) dataSpec.getElementsByTagName("Grid").item(0);
		grid = new Grid(gridElement);
		Node oGrid = gridElement.cloneNode(true);
		output.adoptNode(oGrid);
		data.appendChild(oGrid);
		
		Element iTasks = (Element) dataSpec.getElementsByTagName("Tasks").item(0);
		int tasksSize = Integer.parseInt(iTasks.getAttribute("size"));
		TaskGenerator tg = new TaskGenerator(iTasks);
		
		Element oTasks = output.createElement("Tasks");
		data.appendChild(oTasks);
		
		ArrayList<Task> tasks = new ArrayList<Task>();
		for (int i = 0; i < tasksSize; i++) {
			tasks.add(tg.NextTask());	
		}
		
		Collections.sort(tasks);
		
		int endTime = 0;
		for (Task t : tasks) {
			Element task = output.createElement("Task");
			task = t.Fill(task);
			endTime = t.releaseFrame;
			oTasks.appendChild(task);
		}
		
		Element iWorkers = (Element) dataSpec.getElementsByTagName("Workers").item(0);
		WorkerGenerator wg = new WorkerGenerator(iWorkers);
		
		Element oWorkers = output.createElement("Workers");
		data.appendChild(oWorkers);
		
		ArrayList<Worker> workers = new ArrayList<Worker>();
		while (true) {
			Worker w = wg.NextWorker();
			if (w.releaseFrame <= endTime)
				workers.add(w);
			else
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
