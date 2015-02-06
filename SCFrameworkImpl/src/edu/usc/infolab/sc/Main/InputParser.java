package edu.usc.infolab.sc.Main;

import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;

public class InputParser {
	private Document doc;
	private Element root;
	private Transformer transformer;
	
	public InputParser(String filepath) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(filepath);
			root = doc.getDocumentElement();
			
			TransformerFactory tf = TransformerFactory.newInstance();
			transformer = tf.newTransformer();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Grid GetGrid(){
		return new Grid((Element) root.getElementsByTagName("Grid").item(0));
	}
	
	public HashMap<Integer, Worker> GetWorkers() {
		HashMap<Integer, Worker> workers = new HashMap<Integer, Worker>();
		Element workersElement = (Element) root.getElementsByTagName("Workers").item(0);
		NodeList workerElements = workersElement.getElementsByTagName("Worker");
		for (int i = 0; i < workerElements.getLength(); ++i) {
			Element worker = (Element) workerElements.item(0);
			Worker w = new Worker(worker);
			workers.put(w.id, w);
		}
		return workers;
	}
	
	public HashMap<Integer, Task> GetTasks() {
		HashMap<Integer, Task> tasks = new HashMap<Integer, Task>();
		Element tasksElement = (Element) root.getElementsByTagName("Tasks").item(0);
		NodeList taskElements = tasksElement.getElementsByTagName("Task");
		for (int i = 0; i < taskElements.getLength(); ++i) {
			Element task = (Element) taskElements.item(i);
			this.Print(task);
			Task t = new Task(task);
			tasks.put(t.id, t);
		}
		return tasks;
	}
	
	public void Print(Element e) {
		try {
			StringWriter sw = new StringWriter();
			transformer.transform(new DOMSource(e), new StreamResult(sw));
			System.out.println(sw.toString());
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
		
	}
}
