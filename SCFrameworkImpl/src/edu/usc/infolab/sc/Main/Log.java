package edu.usc.infolab.sc.Main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class Log {
	private static final boolean print = true;
	private static final DateFormat sdf = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
	private static final String logName = String.format("logs_%s.log", new SimpleDateFormat("dd-MMM-yy_HH-mm-ss").format(Calendar.getInstance().getTime())); 
	private static FileWriter fw;
	private static BufferedWriter bw;
	
	private Log() {}
	
	public static void Initialize() {
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
	
	public static void Add(String format, Object... args) {
		Log.Add(String.format(format, args));
	}
		
	public static void Add(String log) {
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
