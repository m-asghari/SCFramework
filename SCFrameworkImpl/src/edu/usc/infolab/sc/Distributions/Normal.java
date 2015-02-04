package edu.usc.infolab.sc.Distributions;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.w3c.dom.Element;

public class Normal extends Distribution<Double> {
	NormalDistribution dist;
	
	public static NormalConfig ParseConfig(Element e) {
		NormalConfig c = new NormalConfig();
		c.mean = Double.parseDouble(((Element)e.getElementsByTagName("mean").item(0)).getAttribute("value"));
		c.sd = Double.parseDouble(((Element)e.getElementsByTagName("sd").item(0)).getAttribute("value"));
		return c;
	}
	
	public Normal(NormalConfig config) {
		dist = new NormalDistribution(config.mean, config.sd);
	}
	
	@Override
	public Double Sample() {
		return dist.sample();
	}
	
	public static Class<?> GetConfigClass() {
		return NormalConfig.class;
	}

}
