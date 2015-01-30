package edu.usc.infolab.sc.Distributions;

import org.apache.commons.math3.distribution.NormalDistribution;

public class Normal extends Distribution {
	NormalDistribution dist;
	
	public Normal(NormalConfig config) {
		dist = new NormalDistribution(config.mean, config.sd);
	}
	
	@Override
	public double Sample() {
		return dist.sample();
	}

}
