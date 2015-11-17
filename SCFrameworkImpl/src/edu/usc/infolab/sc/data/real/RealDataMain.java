package edu.usc.infolab.sc.data.real;

public class RealDataMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FlickrProcessor flickrProcessor = new FlickrProcessor(
				"res\\Flickr\\photo_metadata.csv", "res\\Flickr\\LA_images.csv");
		//flickrProcessor.ParseRawData();
		flickrProcessor.GenerateSCInput();
	}

}
