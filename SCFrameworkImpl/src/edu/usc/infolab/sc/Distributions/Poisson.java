package edu.usc.infolab.sc.Distributions;

import org.apache.commons.math3.distribution.PoissonDistribution;

public class Poisson {	
	PoissonDistribution dist;
	
	public Poisson(double mean) {
		dist = new PoissonDistribution(mean);
	}
	
	public int Next() {
		return dist.sample();
	}

}
