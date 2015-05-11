package edu.usc.infolab.sc;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import edu.usc.infolab.FlowNetwork.Edge;
import edu.usc.infolab.FlowNetwork.Graph;
import edu.usc.infolab.FlowNetwork.Node;


public class CountDistribution {
	public double[] cellCount;
	int totalCount;
	Grid grid;
	
	public CountDistribution(Grid g, boolean empty) {
		grid = g;
		cellCount = new double[grid.size()];
		for (int i = 0; i < grid.size(); ++i) {
			cellCount[i] = (empty) ? 0.0 : 1.0/grid.size();
		}
		totalCount = (empty) ? 0 : 1;
	}
	
	public CountDistribution(Grid g, double[] counts) {
		grid = g;
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
	
	public void Inc(int cell, int num) {
		for (int i = 0; i < num; i++)
			this.Inc(cell);
	}
	
	public void Inc(int cell) {
		cellCount[cell]++;
		totalCount++;
	}
	
	public void Dec(int cell, int num) {
		for (int i = 0; i < num; i++)
			this.Dec(cell);
	}
	
	public void Dec(int cell) {
		if (cellCount[cell] >= 1) {
			cellCount[cell]--;
			totalCount--;
		}
	}
	
	public double GetMaxInfluence() {
		return 2 * grid.maxDistance;
	}
	
	public double GetPointInfluence(Point2D.Double p) {
		double sum = 0;
		for (int i = 0; i < cellCount.length; ++i) {
			double dist = p.distance(grid.GetCellMidPoint(i));
			//double distInf = (dist >= 1) ? grid.maxDistance / dist : 2 * grid.maxDistance;
			double distInf = (dist >= 1) ? grid.maxDistance / dist : grid.maxDistance;
			sum += distInf * Prob(i);
		}
		return sum;
	}
	
	public CountDistribution Normalize() {
		CountDistribution normal = new CountDistribution(grid, true);
		for (int i = 0; i < this.cellCount.length; ++i) {
			normal.cellCount[i] = this.cellCount[i] / this.totalCount;
		}
		normal.totalCount = 1;
		return normal;
	}
	
	public static CountDistribution Mean(CountDistribution A, CountDistribution B) {
		CountDistribution retDist = new CountDistribution(A.grid, true);
		CountDistribution A_n = A.Normalize();
		CountDistribution B_n = B.Normalize();
		for (int i = 0; i < retDist.cellCount.length; ++i) {
			retDist.cellCount[i] = A_n.cellCount[i] + B_n.cellCount[i];
		}
		retDist.totalCount = A_n.totalCount + B_n.totalCount;
		return retDist.Normalize();
	}
	
	public static Double EMD(CountDistribution P, CountDistribution Q) {
		Node.Reset();
		HashMap<Integer, Node> pNodes = new HashMap<Integer, Node>();
		HashMap<Integer, Node> nNodes	= new HashMap<Integer, Node>();
		for (int cell = 0; cell < Q.grid.size(); cell++) {
			Double diff = (P.Prob(cell) - Q.Prob(cell)) * 100;
			if (diff > 0) {
				pNodes.put(cell, new Node(diff.intValue()));
			} else if (diff < 0) {
				nNodes.put(cell, new Node(diff.intValue()));
			}
		}
		ArrayList<Node> nodes = new ArrayList<Node>();
		nodes.addAll(pNodes.values());
		nodes.addAll(nNodes.values());
		
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for (Entry<Integer, Node> pNode : pNodes.entrySet()) {
			for (Entry<Integer, Node> nNode : nNodes.entrySet()) {
				Double cost = P.grid.GetCellMidPoint(pNode.getKey()).distance(P.grid.GetCellMidPoint(nNode.getKey()));
				edges.add(new Edge(pNode.getValue(), nNode.getValue(), cost.intValue()));
				
			}
		}
		
		Graph emdGraph = new Graph(nodes, edges);
		emdGraph.FindMinFlow();
		
		Double emd = 0.;
		for (Edge e : emdGraph.edges.values()) {
			emd += e.flow * e.cost;
		}
		return emd;
	}
	
	public static double JSD(CountDistribution P, CountDistribution Q) {
		CountDistribution avg = CountDistribution.Mean(P, Q);
		return (0.5 * KLD(P, avg)) + (0.5 * KLD(Q, avg));
	}
	
	private static double KLD(CountDistribution P, CountDistribution Q) {
		double sum = 0.0;
		for (int i = 0; i < P.cellCount.length; ++i) {
			double p = P.Prob(i);
			if (p == 0)
				continue;
			double q = Q.Prob(i);
			double log = Math.log(p/q);
			sum += p * log;
		}
		return sum;
	}
	
	public static double SKLD(CountDistribution P, CountDistribution Q) {
		double sum = 0;
		for (int i = 0; i < P.cellCount.length; ++i) {
			double p = P.Prob(i);
			if (p == 0)
				continue;
			for (int j = 0; j < Q.cellCount.length; ++j) {
				double q = Q.Prob(j);
				double dist = P.grid.GetCellMidPoint(i).distance(Q.grid.GetCellMidPoint(j));
				double cellDist = (dist == 0) ? 2 * P.grid.maxDistance : P.grid.maxDistance / dist;
				sum += cellDist * Math.abs(p - q);
			}			
		}
		return sum;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Cell Probs: ");
		for (int i = 0; i < cellCount.length; ++i) {
			if (cellCount[i] > 1 / cellCount.length) {
				sb.append(String.format("%d: %.2f, ", i, cellCount[i]/totalCount));
			}
		}
		sb.append("\n");
		return sb.toString();
	}
}
