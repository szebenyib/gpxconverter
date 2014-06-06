import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

//TODO: buffered writing to avoid storing everything in memory
//I don't know if it is needed though.

/**
 * Created by Balint Szebenyi on 2014.06.01..
 * Reads and writes from and to GPX files.
 */
public class GpxProcessor {

	/**
	 * References the file being processed.
	 */
	private File inputFile;

	/**
	 * Constructor.
	 */
	public GpxProcessor() {

	}

	/**
	 * Reads the file that was set for the class. It collects the contents of
	 * the gpx file to an ArrayList of Geocaches. The class handles the
	 * filenames, the lists, but the actual parsing of the tags is handled by
	 * the ParsingHandler class.
	 *
	 * @return A list of Geocaches ArrayList that can be used to create a GPX
	 * file.
	 */
	public List<Geocache> readFileToGeocacheList() {
		/**
		 * Used to read a file later on as inputstream.
		 */
		Reader reader = null;
		/**
		 * Configures and creates a parser.
		 */
		SAXParserFactory factory;
		/**
		 * The parser that is created in the factory.
		 */
		SAXParser saxParser = null;
		/**
		 * Stores the geocaches that can be returned later on.
		 */
		List<Geocache> geocacheArrayList;
		/**
		 * Defines and holds the logic of the saxparser. It tells what tags
		 * to check and what transformations shall be done inside them.
		 */
		ParsingHandler handler;

		factory = SAXParserFactory.newInstance();
		geocacheArrayList = new ArrayList<Geocache>();
		handler = new ParsingHandler(geocacheArrayList);
		try {
			saxParser = factory.newSAXParser();
			FileInputStream inputStream = new FileInputStream(inputFile);
			reader = new InputStreamReader(inputStream, "UTF-8");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		//If there is a file and we can read on
		if (reader != null && saxParser != null) {
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			try {
				saxParser.parse(is, handler);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return handler.getGeocachesList();
	}

	/**
	 * Writes the gpx file itself. The filename is automatically generated,
	 * it will look like "'input'_converted.gpx".
	 *
	 * @param gAL geocachesArrayList abbreviated
	 */
	public File writeGPX(List<Geocache> gAL) {

		/**
		 * Writes the file's contents.
		 */
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(getOutputFileName()),
					"UTF-8"));
			writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
					"<gpx xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-" +
					"instance\" xmlns:xsd=\"http://www.w3" +
					".org/2001/XMLSchema\" version=\"1.0\" creator" +
					"=\"Groundspeak, Inc. All Rights Reserved. http://www" +
					".groundspeak.com\" xsi:schemaLocation=\"http://www" +
					".topografix.com/GPX/1/0 http://www.topografix" +
					".com/GPX/1/0/gpx.xsd http://www.groundspeak.com/" +
					"cache/1/0 http://www.groundspeak.com/cache/1/0/cache" +
					".xsd\" xmlns=\"http://www.topografix.com/GPX/1/0\">\n");

			for (Geocache cache : gAL) {
				//Beginning + specific part for geocache
				writer.write(
						cache.getGeneralGPXPart(false) +
								cache.getGeocacheSpecificGPXPart());
				//Log part for geocache
				int countOfLogs = cache.getLogsList().size();
				for (int j = 0; j < countOfLogs; j++) {
					writer.write(cache.getLogsList().get(j).getLogGPXPart());
				}
				//Ending part for geocache
				writer.write("\t\t</groundspeak:logs>\n" +
						"\t</groundspeak:cache>\n" +
						"</wpt>\n\n");
				//Waypoints for geocache
				int countOfWaypoints = cache.getWaypointsList().size();
				for (int j = 0; j < countOfWaypoints; j++) {
					writer.write(
							cache.getWaypointsList().get(j).getGeneralGPXPart
									(true));
				}
			}
			writer.write("</gpx>");

		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		return new File(System.getProperty("user.dir") +
				System.getProperty("file.separator") +
				getOutputFileName());
	}

	/**
	 * Generates the output filename from the File inputFile.
	 *
	 * @return A String representation of the filename.
	 */
	private String getOutputFileName() {
		int lastIndex = inputFile.getName().lastIndexOf('.');
		String nameWithoutExtension = inputFile.getName().
				substring(0, lastIndex);
		return nameWithoutExtension + "_converted.gpx";
	}

	/**
	 * Sets the file that is to be processed.
	 *
	 * @param inputFile that is to be repaired.
	 */
	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}
}
