package edu.usc.infolab.sc.Logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Log {
	private static final boolean print = true;
	private static final DateFormat sdf = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
	private static final String logName = String.format("logs_%s.log", new SimpleDateFormat("dd-MMM-yy_HH-mm-ss").format(Calendar.getInstance().getTime())); 
	private static FileWriter fw;
	private static BufferedWriter bw;
	
	private static int logLevel;
	
	// Log levels:
	//		0 - Final Report
	//		1 - High Level Completions (Frame, ...)
	//		2 - Low Level Completions (Assignments, ...)
	//		3 - Frame Stats
	//		4 - Selection Results
	//		5 - Selection Details
	
	protected Log() {}
	
	public static void Initialize(int level, String input) {
		logLevel = level;
		try {
			fw = new FileWriter(String.format("%s.log", input));
			bw = new BufferedWriter(fw);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void Initialize(int level) {
		logLevel = level;
		try {
			fw = new FileWriter(logName);
			bw = new BufferedWriter(fw);
		}
		catch (IOException e) {
			e.printStackTrace();
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
	
	public static void Add(int level, String format, Object... args) {
		Add(level, String.format(format, args));
	}
		
	public static void Add(int level, String log) {
		if (level > logLevel) return;
		try {
			String time = sdf.format(Calendar.getInstance().getTime());
			String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
			String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
			String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
			int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
			bw.write(String.format("%s %s.%s(%d): ", time, className, methodName, lineNumber));
			bw.write(log);
			bw.write("\n");
			bw.flush();
			if (print) System.out.println(log);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
