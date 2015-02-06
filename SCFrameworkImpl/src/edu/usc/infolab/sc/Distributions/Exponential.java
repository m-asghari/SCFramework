package edu.usc.infolab.sc.Distributions;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.w3c.dom.Element;

public class Exponential extends Distribution<Double> {
	ExponentialDistribution dist;
	
	public static ExponentialConfig ParseConfig(Element e) {
		ExponentialConfig c = new ExponentialConfig();
		c.mean = Double.parseDouble(((Element)e.getElementsByTagName("mean").item(0)).getAttribute("value"));
		return c;
	}
	
	public Exponential(ExponentialConfig config) {
		dist = new ExponentialDistribution(config.mean);
	}
	
	@Override
	public Double Sample() {
		return dist.sample();
	}
	
	public static Class<?> GetConfigClass() {
		return ExponentialConfig.class;
	}
}
