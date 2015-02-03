package edu.usc.infolab.sc.DataSetGenerators;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.usc.infolab.sc.Distributions.DistProbPair;
import edu.usc.infolab.sc.Distributions.PointDistribution;

public class Utils {
	
	public static <T> ArrayList<DistProbPair<T>> ParseDistributions(Element e, Class<T> clazz) {
		ArrayList<DistProbPair<T>> dists = new ArrayList<DistProbPair<T>>();
		NodeList distributions = e.getElementsByTagName("Distribution");
		String DistributionPackage = PointDistribution.class.getPackage().getName();
		double unusedProb = 1.0;
		int unassignedProb = 0;
		if (distributions != null && distributions.getLength() > 0) {
			for (int i = 0; i < distributions.getLength(); ++i) {
				Element distElemetn = (Element) distributions.item(i);
				String distClass = String.format("%s.%s", DistributionPackage, distElemetn.getAttribute("name"));
				try {
					Class<?> distClazz = Class.forName(distClass);
					Method getConfigClassMethod = distClazz.getMethod("GetConfigClass", new Class<?>[]{});
					Class<?> configClazz = Class.class.cast(getConfigClassMethod.invoke(distClazz, new Object[]{}));
					Method parseMethod = distClazz.getMethod("Parse", new Class<?>[]{Element.class});
					Element settings = (Element) distElemetn.getElementsByTagName("Settings").item(0);		
					Object config = configClazz.cast(parseMethod.invoke(distClazz, new Object[]{settings}));
					Constructor<?> cons = distClazz.getConstructor(configClazz);
					T dist = clazz.cast(cons.newInstance(config));
					double prob = Double.parseDouble(distElemetn.getAttribute("prob"));
					if (prob == -1) unassignedProb++;
					else unusedProb -= prob;
					dists.add(new DistProbPair<T>(dist, prob));
					}
				catch (Exception exc) {
					exc.printStackTrace();
				}
			}
			double defaultPrb = unusedProb / unassignedProb;
			for (DistProbPair<T> p : dists) {
				if (p.prob == -1) p.prob = defaultPrb;
			}
		}
		return dists;
		
	}

}
