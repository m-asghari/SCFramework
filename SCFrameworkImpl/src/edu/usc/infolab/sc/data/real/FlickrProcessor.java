package edu.usc.infolab.sc.data.real;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;

import javax.imageio.ImageIO;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Pair;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;


public class FlickrProcessor extends RealDataProcessor {
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private final int idIdx = 0;
	private final int userIdx = 1;
	private final int latIdx = 4;
	private final int lngIdx = 5;
	private final int dateTakenIdx = 7;
	private final int dateUploadedIdx = 8;
	
	private class Image {
		public Long id;
		public String user;
		public double lat;
		public double lng;
		public Calendar dateTaken;
		public Calendar dateUploaded;
		
		public Image(String... params) throws Exception {
			if (params.length < 9) {
				throw new Exception("Not enough parameters for Image.");
			}
			
			try {
				this.id = Long.parseLong(params[idIdx]);
				this.user = params[userIdx];
				this.lat = Double.parseDouble(params[latIdx]);
				this.lng = Double.parseDouble(params[lngIdx]);
				this.dateTaken = Calendar.getInstance();
				this.dateTaken.setTime(sdf.parse(params[dateTakenIdx].replace("\"", "")));
				this.dateUploaded = Calendar.getInstance();
				this.dateUploaded.setTime(sdf.parse(params[dateUploadedIdx].replace("\"", "")));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public String toString() {
			return String.format("%d,%s,,,%f,%f,,%s,%s\n",
					this.id, this.user, this.lat, this.lng,
					sdf.format(this.dateTaken.getTime()), sdf.format(this.dateUploaded.getTime()));
		}
	}

	
	private boolean firstLine;
	
	private final Pair<String, String> titleField = 
			new Pair<String, String>("\",\".*\",\"\\{" , "\", ,\"\\{");
	private final Pair<String, String> tagsField = 
			new Pair<String, String>(",\"\\{.*\\}\",", ", ,");
		
	public FlickrProcessor(String source, String dest, City city) {
		super(source, dest, city);
		firstLine = true;
	}

	@Override
	public void ParseRow(String line) {
		if (firstLine) {
			firstLine = false;
			return;
		}
		
		line = line
				.replaceFirst(titleField.First, titleField.Second) //remove title field
				.replaceFirst(tagsField.First, tagsField.Second); //remove tags field
		
		try {
			Image img = new Image(line.split(","));
			if (InRange(city, img)) {
				bw.write(img.toString());
				bw.flush();
			}			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void GenerateSCInput(int num) {
		try {
			FileReader fr = new FileReader(parsedData);
			BufferedReader br = new BufferedReader(fr);
			
			ArrayList<Task> tasks = new ArrayList<Task>();
			ArrayList<Worker> workers = new ArrayList<Worker>();
			
			String line = "";
			Random rand = new Random();
			ArrayList<Task> plotting_tasks = new ArrayList<Task>();
			while ((line = br.readLine()) != null) {
				Image img = new Image(line.split(","));
				
				Task pt = new Task();
				pt.location = new Point2D.Double(img.lat - city.minLat, img.lng - city.minLng);
				plotting_tasks.add(pt);
				
				Point2D.Double loc = FitToGrid(city, img.lat, img.lng);
				//int frame1 = CalendarToFrame(img.dateTaken);
				//int frame2 = CalendarToFrame(img.dateUploaded);
				//int releaseFrame = (frame1 > frame2) ? frame2 : frame1;
				//int retrarcFrame = (frame1 > frame2) ? frame1 : frame2;
				//int duration = retrarcFrame - releaseFrame;
				//if (durationThreshold > duration) {
				int releaseFrame = CalendarToFrame(img.dateTaken);
				if (rand.nextDouble() < 0.85) {
					//Task
					Task t = new Task();
					t.location = loc;
					t.releaseFrame = releaseFrame;
					//t.retractFrame = retrarcFrame;
					if (rand.nextDouble() < 0.25) {
						t.retractFrame = releaseFrame + (rand.nextInt(101) + 200);
					}
					else {
						t.retractFrame = releaseFrame + (rand.nextInt(91) + 50);
					}
					t.value = 1;
					tasks.add(t);
				}
				else {
					//Worker
					Worker w = new Worker();
					//w.location = loc;
					w.location = new Point2D.Double(
							rand.nextDouble() * (gridMaxLat - gridMinLat),
							rand.nextDouble() * (gridMaxLng - gridMinLng));
					w.releaseFrame = releaseFrame;
					//w.retractFrame = retrarcFrame;
					w.retractFrame = releaseFrame + (int)(rand.nextGaussian() * 180 + 720);
					w.maxNumberOfTasks = 4 + rand.nextInt(3);
					workers.add(w);
				}
			}
			br.close();
			fr.close();
			
			
			
			for (int test = 0; test < num; test++) {
				ArrayList<Task> sampled_tasks = new ArrayList<Task>();
				for (int i = 0; i < 15000; i++) {
					int idx = rand.nextInt(tasks.size());
					sampled_tasks.add(tasks.get(idx));
				}
				PlotTasks(plotting_tasks, city, 600);
				
				ArrayList<Worker> sampled_workers = new ArrayList<Worker>();
				for (int i = 0; i < 3000; i++) {
					int idx = rand.nextInt(workers.size());
					sampled_workers.add(workers.get(idx));
				}
				
				Collections.sort(sampled_tasks);
				Collections.sort(sampled_workers);
				System.out.println(String.format("#Tasks: %d, #Workers: %d", sampled_tasks.size(), sampled_workers.size()));
				SaveData(grid, sampled_tasks, sampled_workers, String.format("res//Flickr//flickr_realData_%s_%d_%d.xml", city.name, sampled_tasks.size(), test));
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String GetStats(){
		try {
			FileReader fr = new FileReader(parsedData);
			BufferedReader br = new BufferedReader(fr);
			
			int taskCount = 0;
			ArrayList<String> workerIDs = new ArrayList<String>();
			
			String line = "";
			while ((line = br.readLine()) != null) {
				taskCount++;
				Image img = new Image(line.split(","));
				if (!workerIDs.contains(img.user)) {
					workerIDs.add(img.user);
				}
			}
			
			br.close();
			fr.close();
			return String.format("City: %s, #tasks: %d, #workers: %d", city.name, taskCount, workerIDs.size());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private int CalendarToFrame(Calendar cal) {
		int origFrame = 
				cal.get(Calendar.HOUR_OF_DAY) * 60 * 60 +
				cal.get(Calendar.MINUTE) * 60 +
				cal.get(Calendar.SECOND);
		
		return origFrame;
	}
	
	private final double gridMinLat = 0;
	private final double gridMaxLat = 360;
	private final double gridMinLng = 0;
	private final double gridMaxLng = 360;
	private final Grid grid = new Grid(
			new Point2D.Double(gridMinLat,gridMinLng), 
			new Point2D.Double(gridMaxLat, gridMaxLng),
			(int)((gridMaxLat-gridMinLat)/3), (int)((gridMaxLng-gridMinLng)/3));	
	private Point2D.Double FitToGrid(City city, double lat, double lng) {
		double newLat = (((lat - city.minLat)/(city.maxLat-city.minLat))*(gridMaxLat-gridMinLat))+(gridMinLat);
		double newLng = (((lng - city.minLng)/(city.maxLng-city.minLng))*(gridMaxLng-gridMinLng))+(gridMinLng);
		return new Point2D.Double(newLat, newLng);
	}
	
	private boolean InRange(City city, Image img) { 
		if (img.lat > city.maxLat)
			return false;
		if (img.lat < city.minLat)
			return false;
		if (img.lng > city.maxLng)
			return false;
		if (img.lng < city.minLng)
			return false;
		return true;
	}
	
	private void PlotTasks(ArrayList<Task> tasks, City city, int scale) {
		BufferedImage bufferedImage = new BufferedImage(
				(int)((city.maxLat - city.minLat)*scale),
				(int)((city.maxLng - city.minLng)*scale),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
		
		g.setColor(Color.RED);
		for (int i = 0; i < 5000 && i < tasks.size(); i++) {
			tasks.get(i).Draw(g, scale);
		}
		try {
			ImageIO.write(bufferedImage, "JPG", new File(String.format("%s_tasks_dist.jpg", city.name)));
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}		
	}

	@Override
	protected void FinalizeParse() {
		try {
			bw.close();
			fw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
