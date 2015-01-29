package edu.usc.infolab.sc.DataSetGenerators;

import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

import org.w3c.dom.Element;

import edu.usc.infolab.sc.Distributions.DistProbPair;
import edu.usc.infolab.sc.Distributions.DistributionConfig;
import edu.usc.infolab.sc.Distributions.PointDistribution;

public class PointGenerator {
	
	private static String DistributionPackage = PointDistribution.class.getPackage().getName();
	
	ArrayList<DistProbPair<PointDistribution>> dists = new ArrayList<DistProbPair<PointDistribution>>();
	
	public class Config {
		String distName;
		Element settings;
		Double prob;
	}
	
	public PointGenerator(){};
	
	public PointGenerator(ArrayList<Config> pgConfig) {
		double unusedProb = 1.0;
		int unassignedProb = 0;
		for (Config c : pgConfig) {
			if (c.prob == -1) unassignedProb++;
			else unusedProb -= c.prob;
		}
		double defaultProb = unusedProb / unassignedProb;
		for (Config c : pgConfig) {
			String distClass = String.format("%s.%s", DistributionPackage, c.distName);
			try {
				Class<?> clazz = Class.forName(distClass);
				Method parseMethod = clazz.getMethod("Parse", new Class<?>[]{Element.class});
				DistributionConfig config = (DistributionConfig) parseMethod.invoke(clazz, new Object[]{c.settings});
				Constructor<?> cons = clazz.getConstructor(DistributionConfig.class);
				PointDistribution dist = (PointDistribution) cons.newInstance(config);
				double distProb = (c.prob == -1) ? defaultProb : c.prob; 
				dists.add(new DistProbPair<PointDistribution>(dist, distProb));				
			}
			catch (Exception exc) {
				exc.printStackTrace();
			}
		}		
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
