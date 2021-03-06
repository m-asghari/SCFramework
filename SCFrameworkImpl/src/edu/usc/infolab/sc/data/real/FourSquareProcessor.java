package edu.usc.infolab.sc.data.real;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;

import edu.usc.infolab.sc.Grid;
import edu.usc.infolab.sc.Task;
import edu.usc.infolab.sc.Worker;
import edu.usc.infolab.sc.data.DateSetGenerator;

public class FourSquareProcessor extends DateSetGenerator {
	private static final String LA = "LA";
	private static final String NY = "NY";
	private static final String London = "London";
	private static final String Paris = "Paris";
	private static final String Beijing = "Beijing";
	private static final HashMap<String, City> cities = new HashMap<String, City>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(LA, new City(new Object[]{LA, 33.537819, 34.314944, -118.957248, -117.487827}));
			put(NY, new City(new Object[]{NY, 40.587567, 40.912158, -74.223447, -73.606839}));
			put(London, new City(new Object[]{London, 51.341460, 51.628797, -0.504485, 0.317429}));
			put(Paris, new City(new Object[]{Paris, 48.679323, 48.976015, 2.107958, 2.871925}));
			put(Beijing, new City(new Object[]{Beijing, 39.753881, 40.247413, 115.768465, 116.689805}));
		}
	};
	
	private static HashMap<String, Double> venueLats = new HashMap<String, Double>();
	private static HashMap<String, Double> venueLngs = new HashMap<String, Double>();
	
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");

	
	public static void main(String[] args) {
		String[] cityNames = new String[]{LA, NY, London, Paris, Beijing};
		try {
			populateVenueLatLngs();
			for (String cityName : cityNames) {
				City city = cities.get(cityName);
				ArrayList<String> cityData = filterCheckIns(city);
				ProcessCityData(city, cityData);				
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		catch (ParseException pe) {
			pe.printStackTrace();
		}
	}
	
	private static void populateVenueLatLngs() throws IOException{
		FileReader fr = new FileReader("res//Foursquare//POIs.txt");
		BufferedReader br = new BufferedReader(fr);
		int counter = 0;
		String line = "";
		while ((line = br.readLine()) != null) {
			counter++;
			String[] fields = line.split("\t");
			if (fields.length < 4 || fields[0] == "" || fields[1] == "" || fields[2] == "") continue;
			venueLats.put(fields[0], Double.parseDouble(fields[1]));
			venueLngs.put(fields[0], Double.parseDouble(fields[2]));
			if (counter % 200 == 0) {
				System.out.println(String.format("Processed %d venues", counter));
			}
		}
		br.close();
		fr.close();
	}
	
	private static ArrayList<String> filterCheckIns(City city) throws IOException {
		ArrayList<String> filteredCheckIns = new ArrayList<String>();
		FileReader fr = new FileReader("res//Foursquare//Checkins.txt");
		BufferedReader br = new BufferedReader(fr);
		int counter = 0;
		String line = "";
		while ((line = br.readLine()) != null) {
			String[] fields = line.split("\t");
			if (fields.length < 4 || fields[0] == "" || fields[1] == "" || fields[2] == "") continue;
			String venueID = fields[1];
			double lat = venueLats.get(venueID);
			double lng = venueLngs.get(venueID);
			if (lat <= city.maxLat && lat >= city.minLat && lng <= city.maxLng && lng >= city.minLng) {
				StringBuilder sb = new StringBuilder();
				sb.append(fields[0]);
				sb.append("\t");
				sb.append(Double.toString(lat));
				sb.append("\t");
				sb.append(Double.toString(lng));
				sb.append("\t");
				sb.append(fields[2]);
				sb.append("\t");
				sb.append(fields[3]);
				filteredCheckIns.add(sb.toString());
				counter++;
			}
		}
		System.out.println(String.format("%s -> %d locations", city.name, counter));
		br.close();
		fr.close();
				
		return filteredCheckIns;
	}
	
	static class WorkerData{
		int id;
		ArrayList<Point2D.Double> locations = new ArrayList<Point2D.Double>();
		int startFrame;
		int endFrame;
		int maxNumberOfTasks;
	}
	
	private static void ProcessCityData(City city, ArrayList<String> cityData) throws ParseException{
		HashMap<Integer, WorkerData> workersData = new HashMap<Integer, WorkerData>();
		HashMap<Integer, Task> tasks = new HashMap<Integer, Task>();
		int taskCounter = 0;
		Random rand = new Random();
		for (String data : cityData) {
			String[] fields = data.split("\t");
			int userId = Integer.parseInt(fields[0]);
			double lat = Double.parseDouble(fields[1]);
			double lng = Double.parseDouble(fields[2]);			
			Point2D.Double gridPoint = FitToGrid(city, lat, lng);
			Calendar time = Calendar.getInstance();
			time.setTime(sdf.parse(fields[3]));
			int offset = Integer.parseInt(fields[4]);
			time.add(Calendar.MINUTE, offset);
			
			// Add Task
			Task newTask = new Task();
			newTask.id = taskCounter++;
			newTask.location = gridPoint;
			int retractFrame = CalendarToFrame(time);
			int releaseFrame = 0;
			newTask.retractFrame = retractFrame;
			/*if (rand.nextDouble() < 0.25) {
				releaseFrame = retractFrame - (rand.nextInt(101) + 200);
			} else {
				releaseFrame = retractFrame - (rand.nextInt(91) + 50);
			}*/
			releaseFrame = retractFrame - (rand.nextInt(45) + 45);
			newTask.releaseFrame = releaseFrame;
			newTask.value = 1;
			tasks.put(newTask.id, newTask);
			
			if (workersData.containsKey(userId)) {
				WorkerData workerData = workersData.get(userId);
				workerData.locations.add(gridPoint);
				workerData.startFrame = (releaseFrame < workerData.startFrame) ? releaseFrame : workerData.startFrame;
				workerData.endFrame = (retractFrame > workerData.endFrame) ? retractFrame : workerData.endFrame;
				if (workerData.maxNumberOfTasks < 7) workerData.maxNumberOfTasks++;
				workersData.put(userId, workerData);
			} else {
				WorkerData workerData = new WorkerData();
				workerData.id = userId;
				workerData.locations.add(gridPoint);
				workerData.startFrame = (releaseFrame < workerData.startFrame) ? releaseFrame : workerData.startFrame;
				workerData.endFrame = (retractFrame > workerData.endFrame) ? retractFrame : workerData.endFrame;
				if (workerData.maxNumberOfTasks < 7) workerData.maxNumberOfTasks++;
				workersData.put(userId, workerData);
			}
		}
		
		HashMap<Integer, Worker> workers = new HashMap<Integer, Worker>();
		int workerCounter = 0;
		for (Entry<Integer, WorkerData> e : workersData.entrySet()) {
			WorkerData workerData = e.getValue();
			Worker newWorker = new Worker();
			newWorker.id = workerCounter++;
			newWorker.releaseFrame = workerData.startFrame;
			newWorker.retractFrame = workerData.endFrame;
			newWorker.maxNumberOfTasks = workerData.maxNumberOfTasks;
			newWorker.location = findMeanPoint(workerData.locations);
			workers.put(newWorker.id, newWorker);
		}
		
		ArrayList<Task> sampled_tasks = new ArrayList<Task>();
		for (int i = 0; i < 15000; i++) {
			int idx = rand.nextInt(tasks.size());
			sampled_tasks.add(tasks.get(idx));
		}
		
		ArrayList<Worker> sampled_workers = new ArrayList<Worker>();
		for (int i = 0; i < 3000; i++) {
			int idx = rand.nextInt(workers.size());
			sampled_workers.add(workers.get(idx));
		}
		
		Collections.sort(sampled_tasks);
		Collections.sort(sampled_workers);
		
		SaveData(grid, sampled_tasks, sampled_workers, String.format("res//Foursquare//foursquare_realData_%s.xml", city.name));
	}
	
	private static Point2D.Double findMeanPoint(ArrayList<Point2D.Double> locations) {
		double xSum = 0;
		double ySum = 0;
		for (Point2D.Double p : locations) {
			xSum += p.x;
			ySum += p.y;
		}
		return new Point2D.Double(xSum/locations.size(), ySum/locations.size());
	}

	private static int CalendarToFrame(Calendar cal) {
		int origFrame = 
				cal.get(Calendar.HOUR_OF_DAY) * 60 * 60 +
				cal.get(Calendar.MINUTE) * 60 +
				cal.get(Calendar.SECOND);
		
		return origFrame;
	}
	
	private static final double gridMinLat = 0;
	private static final double gridMaxLat = 360;
	private static final double gridMinLng = 0;
	private static final double gridMaxLng = 360;
	private static final Grid grid = new Grid(
			new Point2D.Double(gridMinLat,gridMinLng), 
			new Point2D.Double(gridMaxLat, gridMaxLng),
			(int)((gridMaxLat-gridMinLat)/3), (int)((gridMaxLng-gridMinLng)/3));	
	private static Point2D.Double FitToGrid(City city, double lat, double lng) {
		double newLat = (((lat - city.minLat)/(city.maxLat-city.minLat))*(gridMaxLat-gridMinLat))+(gridMinLat);
		double newLng = (((lng - city.minLng)/(city.maxLng-city.minLng))*(gridMaxLng-gridMinLng))+(gridMinLng);
		return new Point2D.Double(newLat, newLng);
	}
}
