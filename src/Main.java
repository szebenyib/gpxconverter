import java.io.*;
import java.util.List;


/**
 * Created by Balint Szebenyi on 2014.05.25..
 * Processes all gpx files in the current working directory and corrects them
 * as long as they end with '.gpx', but do not have _input, _output_,
 * _converted at the end of the filename.
 */
public class Main {

	public static void main(String args[]) {
		/**
		 * Holds the input gpx files that are to be converted.
		 */
		File[] gpxInputFiles;
		/**
		 * Holds the geocaches that are to be written to a file;
		 */
		List<Geocache> geocacheArrayList;
		/**
		 * Processes gpx contents. Reads the input file and writes the
		 * output file.
		 */
		GpxProcessor gpxProcessor;

		/**
		 * Working with gpx files found in
		 * the current working directory that are not test files (which
		 * do not end with _input.gpx or _output.gpx).
		 */
		File folder = new File(System.getProperty("user.dir"));
		FilenameFilter inputFilesFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".gpx") && !(name.endsWith("_input.gpx") ||
						name.endsWith("_output.gpx") ||
						name.endsWith("_converted.gpx"));
			}
		};
		gpxInputFiles = folder.listFiles(inputFilesFilter);
		gpxProcessor = new GpxProcessor();

		for (File gpxInputFile : gpxInputFiles) {
			gpxProcessor.setInputFile(gpxInputFile);
			geocacheArrayList = gpxProcessor.readFileToGeocacheList();
			gpxProcessor.writeGPX(geocacheArrayList);
		}
	}

}
