package edu.usc.infolab.sc.data.real;

import java.util.HashMap;

public class RealDataMain {
	
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] cityNames = new String[]{LA, NY, London, Paris, Beijing};
		for (String cityName : cityNames) {
			City city = cities.get(cityName);
			FlickrProcessor flickrProcessor = new FlickrProcessor(
					"res\\Flickr\\photo_metadata.csv", 
					String.format("res\\Flickr\\%s_images.csv", cityName),
					city);
			//flickrProcessor.ParseRawData();
			System.out.println(flickrProcessor.GetStats());
			//flickrProcessor.GenerateSCInput(20);			
		}
	}

}
