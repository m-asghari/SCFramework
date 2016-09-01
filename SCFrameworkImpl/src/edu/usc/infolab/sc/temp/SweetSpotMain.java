package edu.usc.infolab.sc.temp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.infolab.sc.Pair;

public class SweetSpotMain {

	
	public static void main(String[] args) {
		double[] tRates = new double[] {
				1, 2, 3, 4, 5, 6, 7, 8, 9,
				10, 20, 30, 40, 50, 60, 70, 80, 90,
				100, 200, 300, 400, 500
		};
		double[] wRates = new double[]{
				0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9,
				1, 2, 3, 4, 5, 6, 7, 8, 9, 
				10, 20, 30, 40, 50
		};
		HashMap<Pair<Double, Double>, ArrayList<Double>> values = 
				new HashMap<Pair<Double, Double>, ArrayList<Double>>();
		
		try {
			FileReader fr = new FileReader("Results\\VLDB16\\BatchedVsOnline\\RawData.csv");
			BufferedReader br = new BufferedReader(fr);
			
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] params = line.split(",");
				Double tRate = Double.parseDouble(params[0]);
				Double wRate = Double.parseDouble(params[1]);
				ArrayList<Double> results = new ArrayList<Double>();
				for (int i = 2; i < params.length; i++) {
					results.add(Double.parseDouble(params[i]));
				}
				values.put(new Pair<Double, Double>(tRate, wRate), results);
			}
			br.close();
			fr.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		StringBuilder sb = new StringBuilder();
		/*sb.append(",");
		for (double wRate : wRates) {
			sb.append(wRate);
			sb.append(",");
		}
		sb.append("\n");*/
		for (double tRate : tRates) {
			/*sb.append(tRate);
			sb.append(",");*/
			for (double wRate : wRates) {
				ArrayList<Double> res = values.get(new Pair<Double, Double>(tRate, wRate));
				sb.append((((res.get(3) - res.get(4))/res.get(0))) *100);
				sb.append(",");
			}
			sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append("\n");
		}
		
		try {
			FileWriter fw = new FileWriter("C:\\Users\\Mohammad\\Documents\\MATLAB\\"
					+ "bi_lals.csv");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(sb.toString());
			bw.close();
			fw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
