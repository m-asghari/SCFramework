package edu.usc.infolab.sc.Algorithms.Online;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Utils;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Algorithms.Algorithm;
import edu.usc.infolab.sc.Logging.Log;

public abstract class OnlineAlgorithm extends Algorithm{
	protected class FrameStats {
		public int frameNum;
		public int presentWorkers;
		public int availableWorkers;
		public int workerAvailabilities;
		public int releasedTasks;
		public int assignedTasks;
		
		public FrameStats(int frameNumber) {
			this.frameNum = frameNumber;
			this.presentWorkers = 0;
			this.availableWorkers = 0;
			this.workerAvailabilities = 0;
			this.releasedTasks = 0;
			this.assignedTasks = 0;
		}
		
		public FrameStats(Object...args) {
			this.frameNum = (int)args[0];
			this.presentWorkers = (int)args[1];
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("Frame %d:\n", this.frameNum));
			sb.append(String.format("\tAvailable Workers: %d\n", this.presentWorkers));
			return sb.toString();
		}
		
		public String ShortString() {
			return String.format("%d,%d,%d,%d,%d,%d",
					frameNum, presentWorkers, availableWorkers, workerAvailabilities, releasedTasks, assignedTasks);
			
		}
	}
	
	protected ArrayList<FrameStats> _framesStats;
	protected FrameStats _frameStats;
	private File _frameImgDir;
	
	Integer currentFrame;
	ArrayList<Worker> availableWorkers;
	ArrayList<Task> presentTasks;
	ArrayList<Task> unassignedTasks;
	ArrayList<Task> upcomingTasks;
	ArrayList<Worker> upcomingWorkers;
	protected Grid grid;
	
	public OnlineAlgorithm(HashMap<Integer, Task> tasks, HashMap<Integer, Worker> workers, Grid grid) {
		super(tasks, workers);
		currentFrame = 0;
		this.upcomingTasks = new ArrayList<Task>(tasks.values());
		Collections.sort(upcomingTasks);
		this.upcomingWorkers = new ArrayList<Worker>(workers.values());
		Collections.sort(upcomingWorkers);
		availableWorkers = new ArrayList<Worker>();
		presentTasks = new ArrayList<Task>();
		unassignedTasks = new ArrayList<Task>();
		this.grid = grid.clone();
		_framesStats = new ArrayList<FrameStats>();
		_frameImgDir = Utils.CreateEmptyDirectory("FrameImages");
	}
	
	@Override
	public int Run() {
		if (upcomingTasks.size() == 0) {
			System.out.print("");
		}
		while (!upcomingTasks.isEmpty() || !presentTasks.isEmpty()) {
			Log.Add(1, "Current Time Frame: %d", currentFrame);
			AdvanceTime();
			currentFrame++;
		}
		//PrintStat();
		return currentFrame - 1;
	}
	
	private void AdvanceTime() {
		_frameStats = new FrameStats(currentFrame);
		HashMap<Task, Worker> assignments = new HashMap<Task, Worker>();
		// Check to see if any worker becomes available in current frame
		while (!upcomingWorkers.isEmpty() && 
				upcomingWorkers.get(0).releaseFrame <= currentFrame) {
			availableWorkers.add(upcomingWorkers.get(0));
			upcomingWorkers.remove(0);
		}
		
		for (Worker w : availableWorkers) {
			_frameStats.presentWorkers++;
			int availability = w.maxNumberOfTasks - w.GetAssignedTasks().size();
			if (availability > 0) {
				_frameStats.availableWorkers++;
				_frameStats.workerAvailabilities += availability;
			}			
		}
		
		//GetFrameStat();
		
		UpdateWorkerDistribution();
		
		// Check to see if any task arrives in current frame
		while (!upcomingTasks.isEmpty() &&
				upcomingTasks.get(0).releaseFrame <= currentFrame) {
			_frameStats.releasedTasks++;
			Worker w = null;
			if ((w = AssignTask(upcomingTasks.get(0))) != null) {
				assignments.put(upcomingTasks.get(0), w);
				//presentTasks.add(upcomingTasks.get(0));
				_frameStats.assignedTasks++;
			}
			else {
				unassignedTasks.add(upcomingTasks.get(0));
			}
			upcomingTasks.remove(0);
		}
		
		for (Iterator<Worker> it = availableWorkers.iterator(); it.hasNext();) {
			Worker worker = it.next();
			
			// What the worker has to do in current frame
			ArrayList<Task> finished = worker.UpdateLocation(1);
			for (Task ft : finished) {
				presentTasks.remove(ft);
			}
			
			
			if (worker.retractFrame.equals(currentFrame)) {
				it.remove();
			}
		}
		_framesStats.add(_frameStats);
		//if (currentFrame < 1000)
		//	SaveFrameToImage(assignments, 10);
	}
	
	protected Worker AssignTask(Task task) {
		Log.Add(5, "Task %d:", task.id);
		HashMap<Worker, ArrayList<Task>> eligibleWorkers = new HashMap<Worker, ArrayList<Task>>();
		
		for (Worker w : availableWorkers) {
			Calendar start1, end1;
			task.assignmentStat.workerFreeTimes.add(w.retractFrame - w.GetCompleteTime(currentFrame).intValue());
			task.assignmentStat.availableWorkers++;
			Log.Add(5, "Worker %d has %d tasks scheduled.", w.id, w.GetSchedule().size());
			start1 = Calendar.getInstance();
			ArrayList<Task> taskOrder = w.CanPerform();
			end1 = Calendar.getInstance();
			long time = end1.getTimeInMillis() - start1.getTimeInMillis();
			if (time > task.assignmentStat.decideEligibilityTime)
				task.assignmentStat.decideEligibilityTime = time;
			if (taskOrder != null )  {
				task.assignmentStat.eligibleWorkers++;
				eligibleWorkers.put(w, new ArrayList<Task>(taskOrder));
				Log.Add(5, "\tWorker %d can perform the task", w.id);
			}
			Log.Add(5, "\tWorker %d cannot perform the task", w.id);
		}
		Worker selectedWorker = SelectWorker(eligibleWorkers, task);
		if (selectedWorker != null) {
			//Log.Add(1, "Task %d assigned to Worker %d", task.id, selectedWorker.id);
			task.assignmentStat.assigned = 1;
			task.AssignTo(selectedWorker);
			selectedWorker.AddTask(task);
			boolean scheduled = selectedWorker.ComputeSchedule(task, currentFrame);
			if (scheduled) {
				presentTasks.add(task);
			}
		}
		else {
			Log.Add(1, "Task %d not assigned", task.id);
		}
		return selectedWorker;
	}
	
	protected abstract Worker SelectWorker(HashMap<Worker, ArrayList<Task>> eligibleWorkers, Task task);
	
	protected void UpdateWorkerDistribution() {}
	
	protected void SaveFrameToImage(HashMap<Task, Worker> assignments, int scale) {
		BufferedImage bufferedImage = new BufferedImage(grid.GetLength()*scale,grid.GetWidth()*scale,BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
		
		g.setColor(Color.WHITE);
		grid.Draw(g, scale);
		g.setColor(Color.GREEN);
		for (Worker w : availableWorkers) {
			w.Draw(g, scale);
		}
		g.setColor(Color.RED);
		for (Task t : presentTasks) {
			t.Draw(g, scale);
		}
		for (Entry<Task, Worker> e : assignments.entrySet()) {
			g.drawLine((int)(e.getKey().location.x * scale), (int)(e.getKey().location.y * scale), (int)(e.getValue().location.x * scale), (int)(e.getValue().location.y * scale));
		}
		try {
			ImageIO.write(bufferedImage, "JPG", new File(_frameImgDir, String.format("frame%05d.jpg", currentFrame)));
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}		
	}

	/*protected void GetFrameStat() {
		int presentWorkers = 0;
		for (Worker w : availableWorkers) {
			if (w.GetAssignedTasks().size() < w.maxNumberOfTasks) presentWorkers++;
		}
		//int availability = 0;
		
		Object[] frameStatParams = new Object[] {
				currentFrame,
				presentWorkers
		};
		_framesStats.add(new FrameStats(frameStatParams));
	}*/
	
	protected void PrintStat() {
		edu.usc.infolab.sc.Logging.FrameStats.Add("%d", currentFrame);
		for (FrameStats fs : _framesStats) {
			edu.usc.infolab.sc.Logging.FrameStats.Add(fs.ShortString());
		}
		for (FrameStats fs : _framesStats) {
			Log.Add(3, fs.toString());
		}
	}	
}
