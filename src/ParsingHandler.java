import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Balint Szebenyi on 2014.05.29.
 * Stores everything as a Waypoint initially and if it realizes that it is more
 * it copies over its contents to a Geocache.
 */
public class ParsingHandler extends DefaultHandler {

	/**
	 * If false it is just a simple waypoint of a cache,
	 * it it is true it is a cache. Set to false at every parse and changed
	 * if necessary.
	 */
	boolean isGeocache;
	/**
	 * Stores the waypoints temporarily as long as they are not added to a
	 * geocache.
	 */
	private List<Waypoint> preceedingPointsList;
	/**
	 * Stores the waypoints temporarily as long as they are not added to a
	 * geocache.
	 */
	private List<Waypoint> trailingPointsList;
	/**
	 * Stores the waypoints temporarily as long as they are not added to a
	 * geocache.
	 */
	private List<Waypoint> multiStagesList;
	/**
	 * Holds the ids of the waypoints that are to be removed from a list.
	 * This is necessary, as the lists must be iterated forward,
	 * and if items were removed while looping it would mess up the loop. It
	 * also helps to avoid modifying the loop counter.
	 */
	private List<Integer> toRemoveId;
	/**
	 * Holds the geocaches in a list.
	 */
	private List<Geocache> geocachesList;
	/**
	 * A buffer that is used to enable processing multiline texts.
	 */
	private StringBuffer sb;
	/**
	 * Holds the contents of the groundspeak:logs tag. So all logs together.
	 */
	private List<Log> logsList;
	/**
	 * References the current waypoint in processing.
	 */
	private Waypoint currentWaypoint;
	/**
	 * References the current geocache in processing.
	 */
	private Geocache currentGeocache;
	/**
	 * References the previous geocache we have processed.
	 */
	private Geocache previousGeocache;
	/**
	 * References the current log in processing.
	 */
	private Log currentLog;

	public ParsingHandler(List<Geocache> geocacheList) {
		this.logsList = new ArrayList<Log>();
		this.preceedingPointsList = new ArrayList<Waypoint>();
		this.trailingPointsList = new ArrayList<Waypoint>();
		this.multiStagesList = new ArrayList<Waypoint>();
		this.geocachesList = geocacheList;
		this.sb = new StringBuffer();
		this.currentGeocache = null;
		this.previousGeocache = null;
		this.toRemoveId = null;
	}

	/**
	 * @return the Geocaches list.
	 */
	public List<Geocache> getGeocachesList() {
		return this.geocachesList;
	}

	/**
	 * Defines the starting markers for the xml/gpx file. Attributes are
	 * handled here.
	 *
	 * @param uri not used
	 * @param localName not used
	 * @param qName      The actual tag that is being processed
	 * @param attributes Attributes set inside the tag
	 * @throws org.xml.sax.SAXException
	 */
	public void startElement(String uri, String localName, String qName,
	                         Attributes attributes) throws SAXException {

		//Preparing the buffer to accumulate text
		this.sb.setLength(0);

		//We need to create a new Waypoint or new Geocache.
		if (qName.equalsIgnoreCase("WPT")) {
			this.currentWaypoint = new Waypoint();
			this.isGeocache = false;
		} else if (qName.equalsIgnoreCase("GROUNDSPEAK:CACHE")) {

		//Here we can decide if the found geocache is a waypoint of a
		// multi or a new geocache
			if (this.currentWaypoint.hasNameOfMulti()) {
				if (this.previousGeocache != null) {
					if (this.currentWaypoint.getNameFirstSixLetters().equals
							(this.previousGeocache.getNameFirstSixLetters())) {
						this.currentWaypoint.replaceWrongMultiSymbols();
						this.multiStagesList.add(this.currentWaypoint);
					} else {
						this.currentGeocache = new Geocache(this.currentWaypoint);
						this.currentLog = new Log();
						this.isGeocache = true;
					}
				} else {
					this.currentGeocache = new Geocache(this.currentWaypoint);
					this.currentLog = new Log();
					this.isGeocache = true;
				}
			} else {
				this.currentGeocache = new Geocache(this.currentWaypoint);
				this.currentLog = new Log();
				this.isGeocache = true;
			}
		}

		//Getting attributes that have to be handled here
		if (qName.equalsIgnoreCase("WPT")) {
			this.currentWaypoint.setWptALat(attributes.getValue("lat"));
			this.currentWaypoint.setWptALon(attributes.getValue("lon"));
		}

		if (this.isGeocache) {
			if (qName.equalsIgnoreCase("GROUNDSPEAK:CACHE")) {
				this.currentGeocache.setGroundspeakCacheAId(
						attributes.getValue("id"));
				this.currentGeocache.setGroundspeakCacheAAvailable(
						attributes.getValue("available"));
				this.currentGeocache.setGroundspeakCacheAarchived(
						attributes.getValue("archived"));
				this.currentGeocache.setGroundspeakCacheAxmlns(
						attributes.getValue("xmlns:groundspeak"));
			} else if (qName.equalsIgnoreCase("GROUNDSPEAK:OWNER")) {
				this.currentGeocache.setGroundspeakOwnerAId(
						attributes.getValue("id"));
			}

			//Processing individual logs' attributes
			if (qName.equalsIgnoreCase("GROUNDSPEAK:LOG")) {
				this.currentLog.setLogId(
						attributes.getValue("id"));
			} else if (qName.equalsIgnoreCase("GROUNDSPEAK:FINDER")) {
				this.currentLog.setLogFinderId(
						attributes.getValue("id"));
			} else if (qName.equalsIgnoreCase("GROUNDSPEAK:TEXT")) {
				this.currentLog.setLogTextIsEncoded(
						attributes.getValue("encoded"));
			}
		}
	}

	/**
	 * Defines the ending markers for the xml/gpx file. Tags' content is
	 * handled here.
	 *
	 * @param uri not used
	 * @param localName not used
	 * @param qName      The actual tag that is being processed.
	 * @throws SAXException
	 */
	public void endElement(String uri, String localName,
	                       String qName) throws SAXException {

		if (qName.equalsIgnoreCase("ELE")) {
			this.currentWaypoint.setEle(this.sb.toString());
		} else if (qName.equalsIgnoreCase("TIME")) {
			this.currentWaypoint.setTime(this.sb.toString());
		} else if (qName.equalsIgnoreCase("NAME")) {
			//If there are two dashes in the name it is a multicache
			if (this.sb.toString().indexOf('-')
					!= this.sb.toString().lastIndexOf('-') &&
					this.sb.toString().substring(this.sb.toString()
							.lastIndexOf('-')).equals("-1")) {
				this.currentWaypoint.setName(this.sb.toString().
						substring(0, this.sb.toString().lastIndexOf('-')));
			} else {
				this.currentWaypoint.setName(this.sb.toString());
			}
		} else if (qName.equalsIgnoreCase("CMT")) {
			this.currentWaypoint.setCmt(this.sb.toString());
		} else if (qName.equalsIgnoreCase("DESC")) {
			this.currentWaypoint.setDesc(this.sb.toString());
		} else if (qName.equalsIgnoreCase("URL")) {
			this.currentWaypoint.setUrl(this.sb.toString());
		} else if (qName.equalsIgnoreCase("URLNAME")) {
			this.currentWaypoint.setUrlname(this.sb.toString());
		} else if (qName.equalsIgnoreCase("SYM")) {
			this.currentWaypoint.setSym(this.sb.toString());
		} else if (qName.equalsIgnoreCase("TYPE")) {
			this.currentWaypoint.setType(this.sb.toString());
		}

		/**
		 * 	It is a geocache if it has got this field (GROUNDSPEAK fields). The
		 * 	contents of	the waypoint (of Waypoint class) are moved over to a
		 * 	geocache (of Geocache class)
		 */
		//Go on with the fields
		if (this.isGeocache) {
			if (qName.equalsIgnoreCase("GROUNDSPEAK:NAME")) {
				this.currentGeocache
						.setGroundspeakName(this.sb.toString());
			} else if (qName.equalsIgnoreCase("GROUNDSPEAK:PLACED_BY")) {
				this.currentGeocache
						.setGroundspeakPlaced_by(this.sb.toString());
			} else if (qName.equalsIgnoreCase("GROUNDSPEAK:OWNER")) {
				this.currentGeocache
						.setGroundspeakOwner(this.sb.toString());
			} else if (qName.equalsIgnoreCase("GROUNDSPEAK:TYPE")) {
				//Logs started to have a groundspeak:type,
				//by adding this it won't overwrite the geocache's type
				if (this.currentGeocache.getGroundspeakType() == null) {
					this.currentGeocache
							.setGroundspeakType(this.sb.toString());
				}
			} else if (qName.equalsIgnoreCase("GROUNDSPEAK:DIFFICULTY")) {
				this.currentGeocache
						.setGroundspeakDifficulty(this.sb.toString());
			} else if (qName.equalsIgnoreCase("GROUNDSPEAK:TERRAIN")) {
				this.currentGeocache
						.setGroundspeakTerrain(this.sb.toString());
			} else if (qName.equalsIgnoreCase("GROUNDSPEAK:COUNTRY")) {
				this.currentGeocache
						.setGroundspeakCountry(this.sb.toString());
			} else if (qName.equalsIgnoreCase("GROUNDSPEAK:LONG_DESCRIPTION")) {
				this.currentGeocache
						.setGroundspeakLong_description(this.sb.toString());
			}

			//Processing individual logs
			if (qName.equalsIgnoreCase("GROUNDSPEAK:DATE")) {
				this.currentLog.setLogDate(this.sb.toString());
			} else if (qName.equalsIgnoreCase("GROUNDSPEAK:TYPE")) {
				this.currentLog.setLogType(this.sb.toString());
			} else if (qName.equalsIgnoreCase("GROUNDSPEAK:FINDER")) {
				this.currentLog.setLogFinderName(this.sb.toString());
			} else if (qName.equalsIgnoreCase("GROUNDSPEAK:TEXT")) {
				this.currentLog.setLogText(this.sb.toString());
			}

			// We are at the END of one log, we append it to the logsList of the
			// cache.
			if (qName.equalsIgnoreCase("GROUNDSPEAK:LOG")) {
				this.logsList.add(this.currentLog.deepCopyOfLog());
			}
		}
		// We are at the END of the waypoint
		if (qName.equalsIgnoreCase("WPT")) {
			//Reached the end of the current waypoint
			if (this.isGeocache) {
				//Removing unnecessary numbers from the end of the name,
				// because there are some caches that do not begin with 1 and
				// the checking at 215 only checks for that
				this.currentGeocache.removeTrailingNumbers();
				//Adding logs to the geocache and resetting the list
				this.currentGeocache.setLogsList(this.logsList);
				this.logsList = new ArrayList<Log>();

				//We are at the second cache
				if (this.previousGeocache != null) {
					//Adding preceeding, trailing and multi points to the
					// previous cache, because we know all of them only after
					// finding a new cache.
					addMultiStagesToGeocacheIfTheyExist();
					addPreceedingPointsToGeocacheIfTheyExist();
					addTrailingPointsToGeocacheIfTheyExist();
				}

				//Finally adding the geocache it to the list
				this.geocachesList.add(this.currentGeocache);
				this.previousGeocache = this.currentGeocache;
				this.currentGeocache = null;

				//It is not a multi stage. Multis are added at opening tags,
				// other waypoints are added here at the closing tags.
			} else if (!this.multiStagesList.contains(this.currentWaypoint)) {
				//Performing repairs on the waypoint
				this.currentWaypoint.replaceUnsupportedSymbols();
				//Finally adding the simple waypoint to the list IF it really
				// belongs there, its name has got to be checked as even
				// waypoints of other caches may be present in the download
				// which overlapped in this area but without their parent
				if (this.previousGeocache == null) {
					this.preceedingPointsList.add(this.currentWaypoint);
				} else {
					if (this.currentWaypoint.getName().substring(0,
							6).equalsIgnoreCase(this.previousGeocache.getName()
							.substring(0, 6))) {
						this.trailingPointsList.add(this.currentWaypoint);
					} else {
						this.preceedingPointsList.add(this.currentWaypoint);
					}
				}
			}
		}

		//We are at the END of the gpx file
		if (qName.equalsIgnoreCase("GPX")) {
			//The last geocache has got to be handled here
			addMultiStagesToGeocacheIfTheyExist();
			addPreceedingPointsToGeocacheIfTheyExist();
			addTrailingPointsToGeocacheIfTheyExist();
		}
	}

	/**
	 * Builds up the contents between tags into a buffer
	 *
	 * @param ch     character array under processing
	 * @param start  start position
	 * @param length how many characters from starting position are needed
	 * @throws org.xml.sax.SAXException
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		sb.append(ch, start, length);
	}

	/**
	 * Adds the Stages from the multiStagesList to the previous geocache.
	 */
	private void addMultiStagesToGeocacheIfTheyExist() {
		if (!this.multiStagesList.isEmpty()) {
			this.previousGeocache.addToWaypointsList(multiStagesList);
			this.multiStagesList = new ArrayList<Waypoint>();
		}
	}

	/**
	 * Adds the Waypoints that have a preceeding position in the original gpx
	 * file. The added waypoints will be removed from the list after having
	 * been added.
	 */
	private void addPreceedingPointsToGeocacheIfTheyExist() {
		//Init
		this.toRemoveId = new ArrayList<Integer>(this
				.preceedingPointsList.size());

		//Adding
		for (int i = 0; i < this.preceedingPointsList.size(); i++) {
			if (this.preceedingPointsList.get(i).getName().substring(0,
					6).equalsIgnoreCase(this.previousGeocache
					.getName()
					.substring(0, 6))) {
				this.previousGeocache.addToWaypointsList(this
						.preceedingPointsList.get(i));
				this.toRemoveId.add(i);
			}
		}

		//Removing
		for (int i = this.toRemoveId.size() - 1; i >= 0; i--) {
			this.preceedingPointsList.remove(this.toRemoveId.get(i).intValue());
		}
	}

	private void addTrailingPointsToGeocacheIfTheyExist() {
		//Adding
		for (Waypoint aTrailingPointsList : this.trailingPointsList) {
			if (aTrailingPointsList.getName().substring(0,
					6).equalsIgnoreCase(this.previousGeocache
					.getName()
					.substring(0, 6))) {
				this.previousGeocache.addToWaypointsList(aTrailingPointsList);
			} else {
				this.preceedingPointsList.add(aTrailingPointsList);
			}
		}

		//Resetting the list
		this.trailingPointsList = new ArrayList<Waypoint>();
	}

}
