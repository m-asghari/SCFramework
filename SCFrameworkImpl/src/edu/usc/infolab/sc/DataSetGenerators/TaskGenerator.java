package edu.usc.infolab.sc.DataSetGenerators;

import org.w3c.dom.Element;

import edu.usc.infolab.sc.Task;

public class TaskGenerator extends SpatialEntityGenerator {
	RandomGenerator<Double> value;
	
	public TaskGenerator(Element e) {
		super(e);
		value = new RandomGenerator<Double>(
				(Element) e.getElementsByTagName("Value").item(0));
	}
	
	public Task NextTask() {
		Task t = new Task();
		t = (Task) super.Fill(t);
		t.value = value.Sample().intValue();
		return t;
	}
}
