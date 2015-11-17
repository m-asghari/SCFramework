package edu.usc.infolab.sc.data.real;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import edu.usc.infolab.sc.Grid;
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
	
	//private final String listRegex = "\"\\{[^\\{]*\\}\"";
	//private final String emptyList = "";
	
	private double maxLat = 34.314944;
	private double minLat = 33.537819;
	private double maxLng = -117.487827;
	private double minLng = -118.957248;
	
	private final long durationThreshold 
			= 3L	/* hours*/ 
			* 60	/* minutes/hour */
			* 60	/* seconds/minute */;
			//* 1000	/* milliseconds/second*/; 
	
	public FlickrProcessor(String source, String dest) {
		super(source, dest);
		firstLine = true;
	}

	@Override
	public void ParseRow(String line) {
		if (firstLine) {
			firstLine = false;
			return;
		}
		
		line = line
				.replaceFirst("\",\".*\",\"\\{" , "\", ,\"\\{") //remove title field
				.replaceFirst(",\"\\{.*\\}\",", ", ,"); //remove tags field
		
		try {
			Image img = new Image(line.split(","));
			if (InRange(img)) {
				bw.write(img.toString());
				bw.flush();
			}			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void GenerateSCInput() {
		try {
			FileReader fr = new FileReader(parsedData);
			BufferedReader br = new BufferedReader(fr);
			
			ArrayList<Task> tasks = new ArrayList<Task>();
			ArrayList<Worker> workers = new ArrayList<Worker>();
			
			String line = "";
			while ((line = br.readLine()) != null) {
				Image img = new Image(line.split(","));
				Point2D.Double loc = FitToGrid(img.lat, img.lng);
				int frame1 = CalendarToFrame(img.dateTaken);
				int frame2 = CalendarToFrame(img.dateUploaded);
				int releaseFrame = (frame1 > frame2) ? frame2 : frame1;
				int retrarcFrame = (frame1 > frame2) ? frame1 : frame2;
				int duration = retrarcFrame - releaseFrame;
				if (duration < durationThreshold) {
					//Task
					Task t = new Task();
					t.location = loc;
					t.releaseFrame = releaseFrame;
					t.retractFrame = retrarcFrame;
					t.value = 1;
					tasks.add(t);
				}
				else {
					//Worker
				}
			}
			br.close();
			fr.close();
			
			Collections.sort(tasks);
			SaveData(grid, tasks, workers, "res//Flickr//Output.xml");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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
	private Point2D.Double FitToGrid(double lat, double lng) {
		double newLat = (((gridMaxLat-gridMinLat)*(lat-minLat))/(maxLat-minLat)) + gridMinLat;
		double newLng = (((gridMaxLng-gridMinLng)*(lng-minLng))/(maxLng-minLng)) + gridMinLng;
		return new Point2D.Double(newLat, newLng);
	}
	
	private boolean InRange(Image img) { 
		if (img.lat > maxLat)
			return false;
		if (img.lat < minLat)
			return false;
		if (img.lng > maxLng)
			return false;
		if (img.lng < minLng)
			return false;
		return true;
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
