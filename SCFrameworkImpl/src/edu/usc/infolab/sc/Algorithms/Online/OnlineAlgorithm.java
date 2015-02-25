package edu.usc.infolab.sc.Algorithms.Online;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.Algorithms.Algorithm;
import edu.usc.infolab.sc.Main.Log;

public abstract class OnlineAlgorithm extends Algorithm{
	protected class FrameStat {
		public int frameNum;
		public int availableWorkers;
		
		public FrameStat(Object...args) {
			this.frameNum = (int)args[0];
			this.availableWorkers = (int)args[1];
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("Frame %d:\n", this.frameNum));
			sb.append(String.format("\tAvailable Workers: %d\n", this.availableWorkers));
			return sb.toString();
		}
	}
	
	private ArrayList<FrameStat> _frameStat;
	
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
		_frameStat = new ArrayList<FrameStat>();
	}
	
	@Override
	public void Run() {
		while (!upcomingTasks.isEmpty() || !presentTasks.isEmpty()) {
			AdvanceTime();
			currentFrame++;
			Log.Add(1, "Current Time Frame: %d", currentFrame);
		}
		PrintStat();
	}
	
	public void AdvanceTime() {
		GetFrameStat();
		HashMap<Task, Worker> assignments = new HashMap<Task, Worker>();
		// Check to see if any worker becomes available in current frame
		while (!upcomingWorkers.isEmpty() && 
				upcomingWorkers.get(0).releaseFrame <= currentFrame) {
			availableWorkers.add(upcomingWorkers.get(0));
			upcomingWorkers.remove(0);
		}
		
		UpdateWorkerDistribution();
		
		// Check to see if any task arrives in current frame
		while (!upcomingTasks.isEmpty() &&
				upcomingTasks.get(0).releaseFrame <= currentFrame) {
			Worker w = null;
			if ((w = AssignTask(upcomingTasks.get(0))) != null) {
				assignments.put(upcomingTasks.get(0), w);
				presentTasks.add(upcomingTasks.get(0));
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
		SaveFrameToImage(assignments, 10);
	}
	
	protected void UpdateWorkerDistribution() {}
	
	private void SaveFrameToImage(HashMap<Task, Worker> assignments, int scale) {
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
			ImageIO.write(bufferedImage, "JPG", new File(String.format("FrameImages/frame%05d.jpg", currentFrame)));
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}		
	}

	protected void GetFrameStat() {
		Object[] frameStatParams = new Object[] {
				currentFrame,
				availableWorkers.size()
		};
		_frameStat.add(new FrameStat(frameStatParams));
	}
	
	protected void PrintStat() {
		for (FrameStat fs : _frameStat) {
			Log.Add(3, fs.toString());
		}
	}

	protected abstract Worker AssignTask(Task task);
	
}
