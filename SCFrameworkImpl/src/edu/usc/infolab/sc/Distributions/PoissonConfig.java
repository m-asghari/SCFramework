package edu.usc.infolab.sc.Distributions;

public class PoissonConfig extends DistributionConfig {
	double mean;
	
	public PoissonConfig() {}
	
	public PoissonConfig(double mean) {
		this.mean = mean;
	}
}
