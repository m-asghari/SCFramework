package edu.usc.infolab.sc.Distributions;

public class DistProbPair<T> {
	public final T dist;
	public final Double prob;
	
	public DistProbPair(T d, Double p) {
		this.dist = d;
		this.prob = p;
	}

}
