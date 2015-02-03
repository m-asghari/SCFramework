package edu.usc.infolab.sc.Distributions;

import org.apache.commons.math3.distribution.PoissonDistribution;

public class Poisson extends Distribution<Double>{	
	PoissonDistribution dist;
	
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
