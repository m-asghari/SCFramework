package edu.usc.infolab.sc.DataSetGenerators;

import org.w3c.dom.Element;

import edu.usc.infolab.sc.Worker;

public class WorkerGenerator extends SpatialEntityGenerator{
	
	private RandomGenerator<Double> maxNumberOfTasks;
	
	public WorkerGenerator(Element e) {
		super(e);
		maxNumberOfTasks = new RandomGenerator<Double>(
				(Element) e.getElementsByTagName("MaxTasks").item(0));
	}
	
	public Worker NextWorker() {
		Worker w = new Worker();
		w = (Worker) super.Fill(w);
		w.maxNumberOfTasks = maxNumberOfTasks.Sample().intValue();
		return w;
	}

}
