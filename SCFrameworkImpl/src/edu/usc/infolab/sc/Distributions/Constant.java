package edu.usc.infolab.sc.Distributions;

import org.w3c.dom.Element;

public class Constant extends Distribution<Double> {
	Double value;
	
	public static ConstantConfig ParseConfig(Element e) {
		ConstantConfig c = new ConstantConfig();
		c.mean = Double.parseDouble(((Element)e.getElementsByTagName("mean").item(0)).getAttribute("value"));
		return c;
	}
	
	public Constant(ConstantConfig config) {
		this.value = config.mean;
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
