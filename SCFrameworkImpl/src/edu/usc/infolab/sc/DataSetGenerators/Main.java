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
		
		Document input = IO.ReadXML("SampleInput1.xml");
		Element dataSpec = input.getDocumentElement();
		
		Document output = IO.GetEmptyDoc();
		Element data = output.createElement("Data");
		output.appendChild(data);
		
		Element gridElement = (Element) dataSpec.getElementsByTagName("Grid").item(0);
		grid = new Grid(gridElement);
		Node oGrid = gridElement.cloneNode(true);
		output.adoptNode(oGrid);
		data.appendChild(oGrid);
		
		Element iWorkers = (Element) dataSpec.getElementsByTagName("Workers").item(0);
		int workersSize = Integer.parseInt(iWorkers.getAttribute("size"));
		WorkerGenerator wg = new WorkerGenerator(iWorkers);
		
		Element oWorkers = output.createElement("Workers");
		data.appendChild(oWorkers);
		
		ArrayList<Worker> workers = new ArrayList<Worker>();
		for (int i = 0; i < workersSize; i++) {
			workers.add(wg.NextWorker());
		}
		
		Collections.sort(workers);
		
		for (Worker w : workers) {
			Element worker = output.createElement("Worker");
			worker = w.Fill(worker);
			oWorkers.appendChild(worker);
		}
		
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
		
		for (Task t : tasks) {
			Element task = output.createElement("Task");
			task = t.Fill(task);
			oTasks.appendChild(task);
		}
		
		IO.SaveXML(output, "W10T50_4.xml");
	}

}
