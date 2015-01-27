package edu.usc.infolab.sc.RandomGenerator;

import java.awt.geom.Point2D;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class BivariateNormal {
	private static int sampleSize = 100;
	NormalDistribution normal1;
	NormalDistribution normal2;
	MultivariateNormalDistribution dist;
	
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
	
	public Point2D.Double Sample() {
		double[] p = dist.sample();
		return new Point2D.Double(p[0], p[1]);
	}

}
