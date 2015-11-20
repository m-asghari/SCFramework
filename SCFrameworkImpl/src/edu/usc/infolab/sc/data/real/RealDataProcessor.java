package edu.usc.infolab.sc.data.real;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import edu.usc.infolab.sc.data.DateSetGenerator;

public abstract class RealDataProcessor extends DateSetGenerator {
	protected City city;
	
	private String rawData;
	private FileReader fr;
	private BufferedReader br;
	
	protected String parsedData;
	protected FileWriter fw;
	protected BufferedWriter bw;
	
	public RealDataProcessor(String source, String dest, City city) {
		this.rawData = source;
		this.parsedData = dest;
		this.city = city;
	}
	
	public void ParseRawData() {
		try {
			fr = new FileReader(rawData);
			br = new BufferedReader(fr);
			fw = new FileWriter(parsedData);
			bw = new BufferedWriter(fw);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		int row = 0;
		try {
			String line = "";
			while ((line = br.readLine()) != null) {
				ParseRow(line);
				row++;
				if (row % 100000 == 0) {
					System.out.println(String.format("Processed %d rows.", row));
				}
			}
			System.out.println(row);
			bw.close();
			fw.close();
			br.close();
			fr.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public abstract void GenerateSCInput(int num);
	
	protected abstract void ParseRow(String line);
	
	protected abstract void FinalizeParse();
}
