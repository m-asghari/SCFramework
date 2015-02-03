package edu.usc.infolab.sc.Distributions;

import java.awt.geom.Point2D;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.w3c.dom.Element;

public class BivariateNormal extends Distribution<Point2D.Double>{
	
	public static BivariateNormalConfig ParseConfig(Element e) {
		BivariateNormalConfig c = new BivariateNormalConfig();
		c.mean1 = Double.parseDouble(((Element) e.getElementsByTagName("mean1").item(0)).getAttribute("value"));
		c.mean2 = Double.parseDouble(((Element) e.getElementsByTagName("mean2").item(0)).getAttribute("value"));
		c.sd1 = Double.parseDouble(((Element) e.getElementsByTagName("sd1").item(0)).getAttribute("value"));
		c.sd2 = Double.parseDouble(((Element) e.getElementsByTagName("sd2").item(0)).getAttribute("value"));
		String covarString = ((Element) e.getElementsByTagName("covar").item(0)).getAttribute("value");
		if (covarString.equals("")) {
			c.covar = null;
		}
		else {
			c.covar = new double[2][2];
			String[] vals = covarString.split(";");
			c.covar[0][0] = Double.parseDouble(vals[0]);
			c.covar[0][1] = Double.parseDouble(vals[1]);
			c.covar[1][0] = Double.parseDouble(vals[2]);
			c.covar[1][1] = Double.parseDouble(vals[3]);
		}
		return c;
	}
	
	private static int sampleSize = 100;
	NormalDistribution normal1;
	NormalDistribution normal2;
	MultivariateNormalDistribution dist;
	
	public BivariateNormal(BivariateNormalConfig config) {
		//BivariateNormalConfig config = (BivariateNormalConfig) c;
		double[] means = {config.mean1, config.mean2};
		if (config.covar == null) {
			normal1 = new NormalDistribution(config.mean1, config.sd1);
			normal2 = new NormalDistribution(config.mean2, config.sd2);
			double[] sample1 = new double[sampleSize];
			double[] sample2 = new double[sampleSize];
			for (int i = 0; i < sampleSize; i++) {
				sample1[i] = normal1.sample();
				sample2[i] = normal2.sample();
			}
			PearsonsCorrelation pearson = new PearsonsCorrelation();
			double corr = pearson.correlation(sample1, sample2);			
			config.covar = new double[][]{{config.sd1*config.sd1, corr*config.sd1*config.sd2}, {corr*config.sd1*config.sd2, config.sd2*config.sd2}};
		}
		dist = new MultivariateNormalDistribution(means, config.covar);
	}
	
	public BivariateNormal(double mean1, double sd1, double mean2, double sd2) {
		normal1 = new NormalDistribution(mean1, sd1);
		normal2 = new NormalDistribution(mean2, sd2);
		double[] sample1 = new double[sampleSize];
		double[] sample2 = new double[sampleSize];
		for (int i = 0; i < sampleSize; i++) {
			sample1[i] = normal1.sample();
			sample2[i] = normal2.sample();
		}
		PearsonsCorrelation pearson = new PearsonsCorrelation();
		double corr = pearson.correlation(sample1, sample2);
		double[] means = {mean1, mean2};
		double[][] covariances = {{sd1*sd1, corr*sd1*sd2}, {corr*sd1*sd2, sd2*sd2}};
		dist = new MultivariateNormalDistribution(means, covariances);		
	}
	
	@Override
	public Point2D.Double Sample() {
		double[] p = dist.sample();
		return new Point2D.Double(p[0], p[1]);
	}
	
	public static Class<?> GetConfigClass() {
		return BivariateNormalConfig.class;
	}

}
