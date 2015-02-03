package edu.usc.infolab.sc.DataSetGenerators;

import java.util.ArrayList;
import java.util.Random;

import org.w3c.dom.Element;

import edu.usc.infolab.sc.Distributions.DistProbPair;
import edu.usc.infolab.sc.Distributions.Distribution;

public class RandomGenerator<T> {
	ArrayList<DistProbPair<Distribution<T>>> dists; 
	public RandomGenerator(){};
	
	public RandomGenerator(Element e) {
		dists = Distribution.Parse(e, );
	}

	private Distribution<T> GetDist(Double rand) {
		Distribution<T> dist = null;
		double cdf = 0.0;
		for (DistProbPair<Distribution<T>> p : dists) {
			cdf += p.prob;
			if (rand <= cdf) {
				dist = p.dist;
				break;
			}
		}
		return dist;
	}
	
	public T Sample() {
		Random rand = new Random();
		return GetDist(rand.nextDouble()).Sample();
	}
}
