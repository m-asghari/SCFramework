package edu.usc.infolab.sc.data;

import java.util.ArrayList;
import java.util.Collections;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.DataSetGenerators.IO;

public abstract class DateSetGenerator {
		
	public void SaveData(Grid grid, ArrayList<Task> tasks, ArrayList<Worker> workers, String outputFile) {
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
}
