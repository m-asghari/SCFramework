package edu.usc.infolab.sc.Distributions;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.w3c.dom.Element;

public class Poisson extends Distribution<Double>{	
	PoissonDistribution dist;
	
	public static PoissonConfig ParseConfig(Element e) {
		PoissonConfig c = new PoissonConfig();
		c.mean = Double.parseDouble(((Element)e.getElementsByTagName("mean").item(0)).getAttribute("value"));
		return c;
	}
	
	public Poisson(PoissonConfig config) {
		dist = new PoissonDistribution(config.mean);
	}
	
	public int Next() {
		return dist.sample();
	}
	
	@Override
	public Double Sample() {
		return (double) dist.sample();
	}
	
	public static Class<?> GetConfigClass() {
		return PoissonConfig.class;
	}

}
