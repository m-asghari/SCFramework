package edu.usc.infolab.sc.DataSetGenerators;

import java.io.File;

import edu.usc.infolab.sc.Utils;


public class BatchGenerator {

	public static void main(String[] args) {
		String inputFile = "SampleInput.xml";
		String outputPattern = "T50000_1";
		int numberOfOutputs = 100;
		
		
		File dir = Utils.CreateEmptyDirectory(outputPattern);
		
		for (int i = 0; i < numberOfOutputs; ++i) {
			File file = new File(dir, String.format("%s_%02d.xml", outputPattern, i));
			String outputFile = file.getPath();
			Main.GenerateData(inputFile, outputFile);
		}

	}

}
