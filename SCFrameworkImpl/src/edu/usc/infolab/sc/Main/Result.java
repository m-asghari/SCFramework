package edu.usc.infolab.sc.Main;

public final class Result {
	public static int AssignedTasks = 0;
	public static int GainedValue = 0;
	
	public static void GenerateReport() {
		Log.Add("\n\nFinal Report:\n");
		Log.Add("Total Number of Assigned Tasks: %d\n", AssignedTasks);
		Log.Add("Total Gained Valued: %d\n", GainedValue);
	}
}
