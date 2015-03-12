package edu.usc.infolab.sc.Logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public abstract class CSVWriter{
	private static FileWriter fw;
	private static BufferedWriter bw;
	
	public static void Initialize(String input) {
		try {
			fw = new FileWriter(String.format("%s.csv", input));
			bw = new BufferedWriter(fw);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void Finalize() {
		try {
			bw.close();
			fw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void Add(String format, Object... args) {
		Add(String.format(format, args));
	}
	
	public static void Add(String msg) {
		try {
			bw.write(msg);
			bw.write("\n");
			bw.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
