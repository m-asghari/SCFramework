package edu.usc.infolab.sc.Distributions;


public abstract class Distribution<T> {
	
	public abstract T Sample();
	
	public static Class<?> GetConfigClass() {
		return DistributionConfig.class;
	}
}
