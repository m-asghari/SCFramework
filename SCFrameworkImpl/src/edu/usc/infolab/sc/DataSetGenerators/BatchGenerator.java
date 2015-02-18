package edu.usc.infolab.sc.DataSetGenerators;

import java.io.File;


public class BatchGenerator {

	public static void main(String[] args) {
		String inputFile = "SampleInput.xml";
		String outputPattern = "T50000_1";
		int numberOfOutputs = 100;
		
		File currentDir = new File(".");
		File dir = new File(currentDir, outputPattern);
		if (dir.exists())
			Delete(dir);
		dir.mkdir();
		
		for (int i = 0; i < numberOfOutputs; ++i) {
			File file = new File(dir, String.format("%s_%02d.xml", outputPattern, i));
			String outputFile = file.getPath();
			Main.GenerateData(inputFile, outputFile);
		}

	}
	
	private static void Delete(File f) {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				Delete(c);
		}
		f.delete();
	}

}
