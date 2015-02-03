package edu.usc.infolab.sc.Distributions;

import org.apache.commons.math3.distribution.NormalDistribution;

public class Normal extends Distribution<Double> {
	NormalDistribution dist;
	
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
