package edu.usc.infolab.sc.Distributions;

public class Constant extends Distribution {
	double value;
	
	public Constant(ConstantConfig config) {
		this.value = config.value;
	}
	
	@Override
	public double Sample() {
		// TODO Auto-generated method stub
		return 0;
	}

}
