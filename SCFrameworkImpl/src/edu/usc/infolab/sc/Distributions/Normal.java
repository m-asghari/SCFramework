package edu.usc.infolab.sc.Distributions;

import org.apache.commons.math3.distribution.NormalDistribution;

public class Normal extends Distribution {
	NormalDistribution dist;
	
	public Normal(double mean, double sd) {
		dist = new NormalDistribution(mean, sd);
	}
	
	@Override
	public double Sample() {
		return dist.sample();
	}

}
