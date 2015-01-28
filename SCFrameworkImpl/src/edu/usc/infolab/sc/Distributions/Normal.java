package edu.usc.infolab.sc.Distributions;

import org.apache.commons.math3.distribution.NormalDistribution;

public class Normal {
	NormalDistribution dist;
	
	public Normal(double mean, double sd) {
		dist = new NormalDistribution(mean, sd);
	}
	
	public void Sample() {
		dist.sample();
	}

}
