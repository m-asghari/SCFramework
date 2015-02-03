package edu.usc.infolab.sc.Distributions;

public class Constant extends Distribution<Double> {
	Double value;
	
	public Constant(ConstantConfig config) {
		this.value = config.value;
	}
	
	@Override
	public Double Sample() {
		// TODO Auto-generated method stub
		return value;
	}
	
	public static Class<?> GetConfigClass() {
		return ConstantConfig.class;
	}

}
