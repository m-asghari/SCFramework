package edu.usc.infolab.sc.DataSetGenerators;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class WorkerGenerator {
	
	PointGenerator pointGenerator;
	
	public WorkerGenerator() {
		pointGenerator = new PointGenerator();
	}
	
	public void Parse(Element e) {
		Element location = (Element) e.getElementsByTagName("Location").item(0);
		ArrayList<PointGenerator.Config> locationConfigs = new ArrayList<PointGenerator.Config>();
		NodeList distributions = location.getElementsByTagName("Distribution");
		if (distributions != null && distributions.getLength() > 0) {
			for (int i = 0; i < distributions.getLength(); ++i) {
				Element distElemetn = (Element) distributions.item(i);
				PointGenerator.Config locationConfig = pointGenerator.new Config();
				locationConfig.distName = distElemetn.getAttribute("name");
				locationConfig.settings = (Element) distElemetn.getElementsByTagName("Settings").item(0);
				locationConfig.prob = Double.parseDouble(distElemetn.getAttribute("prob"));
				locationConfigs.add(locationConfig);
			}
		}
		pointGenerator = new PointGenerator(locationConfigs);
	}

}
