package edu.usc.infolab.sc.DataSetGenerators;

import org.w3c.dom.Element;

public class TaskGenerator extends SpatialEntityGenerator {
	RandomGenerator<Double> value;
	
	public TaskGenerator(Element e) {
		super(e);
		value = new RandomGenerator<Double>(
				(Element) e.getElementsByTagName("Value").item(0));
	}
	
	public Integer NextValue() {
		return value.Sample().intValue();
	}
}
