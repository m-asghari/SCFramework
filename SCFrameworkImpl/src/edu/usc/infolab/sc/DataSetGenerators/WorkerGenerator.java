package edu.usc.infolab.sc.DataSetGenerators;

import org.w3c.dom.Element;

public class WorkerGenerator extends SpatialEntityGenerator{
	
	private RandomGenerator<Double> maxNumberOfTasks;
	
	public WorkerGenerator(Element e) {
		super(e);
		maxNumberOfTasks = new RandomGenerator<Double>(
				(Element) e.getElementsByTagName("MaxTasks").item(0));
	}
	
	public Integer NextNumOfTasks() {
		return maxNumberOfTasks.Sample().intValue();
	}
}
