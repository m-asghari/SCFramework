package edu.usc.infolab.sc.Distributions;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.usc.infolab.sc.Grid;

public class Poisson2D {
	Grid grid;
	Double intensity;
	Random rand;
	
	public Poisson2D(Grid grid, Double intensity) {
		this.grid = grid;
		this.intensity = intensity;
		rand = new Random();		
	}
	
	public void Initialize() {
		Poisson p = new Poisson(intensity);
		Double sum = 0.0;
		for (int cell = 0; cell < grid.cellProb.size(); ++cell) {
			double prob = p.Next()*1.0;
			grid.cellProb.add(cell, prob);
			sum += prob;			
		}
		for (int cell = 0; cell < grid.cellProb.size(); ++cell) {
			double old = grid.cellProb.get(cell);
			grid.cellProb.set(cell, old/sum);
		}
	}
	
	public Point2D.Double Next() {
		Double p = rand.nextDouble();
		int cell = 0;
		double cdf = 0.0;
		while ((cdf += grid.cellProb.get(cell++)) < p );
		return grid.RandomPoint(cell);
	}
}
