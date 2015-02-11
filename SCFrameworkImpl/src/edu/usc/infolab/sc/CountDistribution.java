package edu.usc.infolab.sc;


public class CountDistribution {
	double[] cellCount;
	int totalCount;
	
	public CountDistribution(int size, boolean empty) {
		cellCount = new double[size];
		for (int i = 0; i < size; ++i) {
			cellCount[i] = (empty) ? 0.0 : 1.0/size;
		}
		totalCount = (empty) ? 0 : 1;
	}
	
	public CountDistribution(double[] counts) {
		cellCount = new double[counts.length];
		totalCount = 0;
		for(int i = 0; i < counts.length; ++i) {
			cellCount[i] = counts[i];
			totalCount += counts[i];
		}
	}
	
	public double Prob(int cell) {
		return cellCount[cell]/totalCount;
	}
	
	public void Inc(int cell) {
		cellCount[cell]++;
		totalCount++;
	}
	
	public void Dec(int cell) {
		if (cellCount[cell] >= 1) {
			cellCount[cell]--;
			totalCount--;
		}
	}
	
	public CountDistribution Normalize() {
		CountDistribution normal = new CountDistribution(cellCount.length, true);
		for (int i = 0; i < this.cellCount.length; ++i) {
			normal.cellCount[i] = this.cellCount[i] / this.totalCount;
		}
		normal.totalCount = 1;
		return normal;
	}
	
	public static CountDistribution Mean(CountDistribution A, CountDistribution B) {
		CountDistribution retDist = new CountDistribution(A.cellCount.length, true);
		CountDistribution A_n = A.Normalize();
		CountDistribution B_n = B.Normalize();
		for (int i = 0; i < retDist.cellCount.length; ++i) {
			retDist.cellCount[i] = A_n.cellCount[i] + B_n.cellCount[i];
		}
		retDist.totalCount = A_n.totalCount + B_n.totalCount;
		return retDist.Normalize();
	}
	
	public static double JSD(CountDistribution P, CountDistribution Q) {
		CountDistribution avg = CountDistribution.Mean(P, Q);
		return (0.5 * KLD(P, avg)) + (0.5 * KLD(Q, avg));
	}
	
	private static double KLD(CountDistribution P, CountDistribution Q) {
		double sum = 0.0;
		for (int i = 0; i < P.cellCount.length; ++i) {
			double p = P.Prob(i);
			double q = Q.Prob(i);
			double log = Math.log(p/q);
			sum += p * log;
		}
		return sum;
	}
}
