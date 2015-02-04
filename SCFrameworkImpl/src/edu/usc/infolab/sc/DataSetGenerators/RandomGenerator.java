package edu.usc.infolab.sc.DataSetGenerators;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.usc.infolab.sc.Distributions.DistProbPair;
import edu.usc.infolab.sc.Distributions.Distribution;

public class RandomGenerator<T> {
	ArrayList<DistProbPair<Distribution<T>>> dists; 
	public RandomGenerator(){};
	
	public RandomGenerator(Element e) {
		dists = this.Parse(e);
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
	
	private ArrayList<DistProbPair<Distribution<T>>> Parse (Element e) {
		ArrayList<DistProbPair<Distribution<T>>> dists = new ArrayList<DistProbPair<Distribution<T>>>();
		NodeList distributions = e.getElementsByTagName("Distribution");
		String DistributionPackage = Distribution.class.getPackage().getName();
		double unusedProb = 1.0;
		int unassignedProb = 0;
		if (distributions != null && distributions.getLength() > 0) {
			for (int i = 0; i < distributions.getLength(); ++i) {
				Element distElemetn = (Element) distributions.item(i);
				String distClass = String.format("%s.%s", DistributionPackage, distElemetn.getAttribute("name"));
				try {	
					@SuppressWarnings("unchecked")
					Class<Distribution<T>> distClazz = (Class<Distribution<T>>) Class.forName(distClass);
					Method getConfigClassMethod = distClazz.getMethod("GetConfigClass", new Class<?>[]{});
					Class<?> configClazz = Class.class.cast(getConfigClassMethod.invoke(distClazz, new Object[]{}));
					Method parseMethod = distClazz.getMethod("ParseConfig", new Class<?>[]{Element.class});
					Element settings = (Element) distElemetn.getElementsByTagName("Settings").item(0);		
					Object config = configClazz.cast(parseMethod.invoke(distClazz, new Object[]{settings}));
					Constructor<Distribution<T>> cons = distClazz.getConstructor(configClazz);
					Distribution<T> dist = cons.newInstance(config);
					double prob = Double.parseDouble(distElemetn.getAttribute("prob"));
					if (prob == -1) unassignedProb++;
					else unusedProb -= prob;
					dists.add(new DistProbPair<Distribution<T>>(dist, prob));
					}
				catch (Exception exc) {
					exc.printStackTrace();
				}
			}
			double defaultPrb = unusedProb / unassignedProb;
			for (DistProbPair<Distribution<T>> p : dists) {
				if (p.prob == -1) p.prob = defaultPrb;
			}
		}
		return dists;

	}
}
