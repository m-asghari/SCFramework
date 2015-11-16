package edu.usc.infolab.sc.Main;

public class Result {
	public Result() {
		ID = "";
		TaskCount = 0;
		NumOfAssignedTasks = 0;
		NumOfCompletedTasks = 0;
		DecideEligibilityRunTime = 0;
		SelectWorkerRunTime = 0;
		TotalTime = 0;
		EligibleWorkers = 0;
		AvgTraveledDistancePerTask = 0;
	}
	String ID;
	int TaskCount;
	int NumOfAssignedTasks;
	int NumOfCompletedTasks;
	double DecideEligibilityRunTime;
	double SelectWorkerRunTime;
	double TotalTime;
	double EligibleWorkers;
	double AvgTraveledDistancePerTask;
}
