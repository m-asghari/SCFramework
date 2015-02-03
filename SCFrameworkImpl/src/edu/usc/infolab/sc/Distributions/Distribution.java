package edu.usc.infolab.sc.Distributions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Distribution<T> {
	public T Sample() {
		return null;
	};
	
	public static Class<?> GetConfigClass() {
		return DistributionConfig.class;
	}
	
	public static <R> ArrayList<DistProbPair<Distribution<R>>> Parse(Element e, R obj) {
		ArrayList<DistProbPair<Distribution<R>>> dists = new ArrayList<DistProbPair<Distribution<R>>>();
		NodeList distributions = e.getElementsByTagName("Distribution");
		String DistributionPackage = PointDistribution.class.getPackage().getName();
		double unusedProb = 1.0;
		int unassignedProb = 0;
		if (distributions != null && distributions.getLength() > 0) {
			for (int i = 0; i < distributions.getLength(); ++i) {
				Element distElemetn = (Element) distributions.item(i);
				String distClass = String.format("%s.%s", DistributionPackage, distElemetn.getAttribute("name"));
				try {
					@SuppressWarnings("unchecked")
					Class<Distribution<R>> distClazz = (Class<Distribution<R>>) Class.forName(distClass);
					Method getConfigClassMethod = distClazz.getMethod("GetConfigClass", new Class<?>[]{});
					Class<?> configClazz = Class.class.cast(getConfigClassMethod.invoke(distClazz, new Object[]{}));
					Method parseMethod = distClazz.getMethod("ParseConfig", new Class<?>[]{Element.class});
					Element settings = (Element) distElemetn.getElementsByTagName("Settings").item(0);		
					Object config = configClazz.cast(parseMethod.invoke(distClazz, new Object[]{settings}));
					Constructor<Distribution<R>> cons = distClazz.getConstructor(configClazz);
					Distribution<R> dist = cons.newInstance(config);
					double prob = Double.parseDouble(distElemetn.getAttribute("prob"));
					if (prob == -1) unassignedProb++;
					else unusedProb -= prob;
					dists.add(new DistProbPair<Distribution<R>>(dist, prob));
					}
				catch (Exception exc) {
					exc.printStackTrace();
				}
			}
			double defaultPrb = unusedProb / unassignedProb;
			for (DistProbPair<Distribution<R>> p : dists) {
				if (p.prob == -1) p.prob = defaultPrb;
			}
		}
		return dists;
	}
}
