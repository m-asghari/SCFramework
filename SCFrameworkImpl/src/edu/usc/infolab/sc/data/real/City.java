package edu.usc.infolab.sc.data.real;

public class City {
	public String name;
	public double minLat;
	public double maxLat;
	public double minLng;
	public double maxLng;
	
	public City(Object... args) {
		if (args.length < 5) {
			System.out.println("Not enough arguments for City");
		}
		this.name = (String)args[0];
		this.minLat = (Double)args[1];
		this.maxLat = (Double)args[2];
		this.minLng = (Double)args[3];
		this.maxLng = (Double)args[4];
	}
}
