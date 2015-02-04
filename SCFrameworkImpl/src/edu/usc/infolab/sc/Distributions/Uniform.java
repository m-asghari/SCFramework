package edu.usc.infolab.sc.Distributions;

import java.util.Random;

import org.w3c.dom.Element;

public class Uniform extends Distribution<Double>{
	Random rand;
	double min;
	double max;
	double dx;
	
	public static UniformConfig ParseConfig(Element e) {
		UniformConfig c = new UniformConfig();
		c.min = Double.parseDouble(((Element) e.getElementsByTagName("min").item(0)).getAttribute("value"));
		c.max = Double.parseDouble(((Element) e.getElementsByTagName("max").item(0)).getAttribute("value"));
		return c;
	}
	
	public Uniform(UniformConfig config) {
		min = config.min;
		max = config.max;
		dx = max - min;
		
		rand = new Random();
	}
	
	@Override
	public Double Sample() {
		double newX = (rand.nextDouble() * dx) + min;
		return newX;
	}
	
	public static Class<?> GetConfigClass() {
		return UniformConfig.class;
	}

}
