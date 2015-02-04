package edu.usc.infolab.sc.DataSetGenerators;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class Main {

	public static void main(String[] args) {
		
		Document input = IO.ReadXML("SampleInput1.xml");
		Element dataSpec = input.getDocumentElement();
		
		Document output = IO.GetEmptyDoc();
		Element data = output.createElement("Data");
		output.appendChild(data);
		
		
		Element iWorkers = (Element) dataSpec.getElementsByTagName("Workers").item(0);
		int workersSize = Integer.parseInt(iWorkers.getAttribute("size"));
		WorkerGenerator wg = new WorkerGenerator(iWorkers);
		
		Element oWorkers = output.createElement("Workers");
		data.appendChild(oWorkers);
		
		for (int i = 0; i < workersSize; i++) {
			Worker w = wg.NextWorker();
			Element worker = output.createElement("Worker");
			worker = w.Fill(worker);
			oWorkers.appendChild(worker);
		}
		
		/*Element iTasks = (Element) dataSpec.getElementsByTagName("Tasks").item(0);
		int tasksSize = Integer.parseInt(iTasks.getAttribute("size"));
		TaskGenerator tg = new TaskGenerator(iTasks);
		
		Element oTasks = output.createElement("Tasks");
		data.appendChild(oTasks);
		
		for (int i = 0; i < tasksSize; i++) {
			Task t = tg.NextTask();
			Element task = output.createElement("Task");
			task = t.Fill(task);
			oTasks.appendChild(task);
		}*/
		
		IO.SaveXML(output, "SampleOutput1.xml");
	}

}
