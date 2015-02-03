package edu.usc.infolab.sc.DataSetGenerators;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import org.w3c.dom.Element;

import edu.usc.infolab.sc.Distributions.DistProbPair;
import edu.usc.infolab.sc.Distributions.PointDistribution;

public class RandomPointGenerator {
	
	ArrayList<DistProbPair<PointDistribution>> dists = new ArrayList<DistProbPair<PointDistribution>>();
	
	public RandomPointGenerator(){};
	
	public RandomPointGenerator(Element e) {
		dists = Utils.ParseDistributions(e, PointDistribution.class);
	}
	
	private PointDistribution GetDist(Double rand) {
		PointDistribution dist = null;
		double cdf = 0.0;
		for (DistProbPair<PointDistribution> p : dists) {
			cdf += p.prob;
			if (rand <= cdf) { 
				dist = p.dist;
				break;
			}			
		}
		return dist;
	}
	
	public Point2D.Double Sample() {
		Random rand = new Random();
		return GetDist(rand.nextDouble()).Sample();
	}
	

}
