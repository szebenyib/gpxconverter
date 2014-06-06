import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Balint Szebenyi on 2014.05.25..
 * It extends the Waypoint class since a geocache is actually a special
 * kind of waypoint. The Waypoint class holds only basic parameters.
 * The Waypoint class not only extends the Waypoints class but also
 * uses them as a list to define the waypoints of the cache.
 */
public class Geocache extends Waypoint {

	private String groundspeakCacheAId;
	private String groundspeakCacheAAvailable;
	private String groundspeakCacheAarchived;
	private String groundspeakCacheAxmlns;
	private String groundspeakName;
	private String groundspeakPlaced_by;
	private String groundspeakOwner;
	private String groundspeakOwnerAId;
	private String groundspeakType;
	private String groundspeakDifficulty;
	private String groundspeakTerrain;
	private String groundspeakCountry;
	private String groundspeakLong_description;
	private List<Log> logsList;
	private List<Waypoint> waypointsList;

	/**
	 * Constructs a Geocache from a Waypoints existing variables.
	 */
	public Geocache(Waypoint waypoint) {
		super();
		this.setEle(waypoint.getEle());
		this.setTime(waypoint.getTime());
		this.setName(waypoint.getName());
		this.setCmt(waypoint.getCmt());
		this.setDesc(waypoint.getDesc());
		this.setUrl(waypoint.getUrl());
		this.setUrlname(waypoint.getUrlname());
		this.setSym(waypoint.getSym());
		this.setType(waypoint.getType());
		this.setWptALat(waypoint.getWptALat());
		this.setWptALon(waypoint.getWptALon());
		waypointsList = new ArrayList<Waypoint>();
	}

	public List<Log> getLogsList() {
		return logsList;
	}

	public void setLogsList(List<Log> logsList) {
		this.logsList = logsList;
	}

	public void setGroundspeakOwnerAId(String groundspeakOwnerAId) {
		this.groundspeakOwnerAId = groundspeakOwnerAId;
	}

	public void setGroundspeakCacheAxmlns(String groundspeakCacheAxmlns) {
		this.groundspeakCacheAxmlns = groundspeakCacheAxmlns;
	}

	public void setGroundspeakCacheAAvailable(String groundspeakCacheAAvailable) {
		this.groundspeakCacheAAvailable = groundspeakCacheAAvailable;
	}

	public void setGroundspeakCacheAarchived(String groundspeakCacheAarchived) {
		this.groundspeakCacheAarchived = groundspeakCacheAarchived;
	}

	public List<Waypoint> getWaypointsList() {
		return this.waypointsList;
	}

	/**
	 * Adds a single waypoint to the geocache
	 * @param waypoint to be added
	 */
	public void addToWaypointsList(Waypoint waypoint) {
		this.waypointsList.add(waypoint);
	}

	/**
	 * Adds a list of waypoints to the geocache
	 * @param waypointsList to be added
	 */
	public void addToWaypointsList(List<Waypoint> waypointsList) {
		this.waypointsList.addAll(waypointsList);
	}

	public void setGroundspeakCacheAId(String groundspeakCacheAId) {
		this.groundspeakCacheAId = groundspeakCacheAId;
	}

	public void setGroundspeakName(String groundspeakName) {
		this.groundspeakName = groundspeakName;
	}

	public void setGroundspeakPlaced_by(String groundspeakPlaced_by) {
		this.groundspeakPlaced_by = groundspeakPlaced_by;
	}

	public void setGroundspeakOwner(String groundspeakOwner) {
		this.groundspeakOwner = groundspeakOwner;
	}

	public void setGroundspeakType(String groundspeakType) {
		this.groundspeakType = groundspeakType;
	}

	public void setGroundspeakDifficulty(String groundspeakDifficulty) {
		this.groundspeakDifficulty = groundspeakDifficulty;
	}

	public void setGroundspeakTerrain(String groundspeakTerrain) {
		this.groundspeakTerrain = groundspeakTerrain;
	}

	public void setGroundspeakCountry(String groundspeakCountry) {
		this.groundspeakCountry = groundspeakCountry;
	}

	public void setGroundspeakLong_description(String groundspeakLong_description) {
		this.groundspeakLong_description = groundspeakLong_description;
	}

	public void removeTrailingNumbers() {
		if (this.getName().indexOf('-')
				!= this.getName().lastIndexOf('-')) {
			this.setName(this.getName().substring(0,
					this.getName().lastIndexOf('-')));
		}
	}
	/**
	 * Generates the output for gpx. Geocache specific fields are in this
	 * generation as they are not included with simple waypoints.
	 *
	 * @return GPX content
	 */
	public String getGeocacheSpecificGPXPart() {
		return "\t<groundspeak:cache id=\"" +
				this.groundspeakCacheAId + "\" " +
				"available=\"" +
				this.groundspeakCacheAAvailable + "\"" +
				" archived=\"" +
				this.groundspeakCacheAarchived + "\" " +
				"xmlns:groundspeak=\"" +
				this.groundspeakCacheAxmlns + "\">\n" +
				"\t\t<groundspeak:name>" +
				this.groundspeakName +
				"</groundspeak:name>\n" +
				"\t\t<groundspeak:placed_by>" +
				escapeChars(this.groundspeakPlaced_by) +
				"</groundspeak:placed_by>\n" +
				"\t\t<groundspeak:owner id=\"" +
				this.groundspeakOwnerAId + "\">" +
				escapeChars(this.groundspeakOwner) +
				"</groundspeak:owner>\n" +
				"\t\t<groundspeak:type>" +
				this.groundspeakType +
				"</groundspeak:type>\n" +
				"\t\t<groundspeak:difficulty>" +
				this.groundspeakDifficulty +
				"</groundspeak:difficulty>\n" +
				"\t\t<groundspeak:terrain>" +
				this.groundspeakTerrain +
				"</groundspeak:terrain>\n" +
				"\t\t<groundspeak:country>" +
				this.groundspeakCountry +
				"</groundspeak:country>\n" +
				"\t\t<groundspeak:long_description>" +
				escapeChars(this.groundspeakLong_description) +
				"\t\t</groundspeak:long_description>\n" +
				"\t\t<groundspeak:logs>\n";
	}

	//TODO: the same above is in log.java check if it cannot be avoided

	/**
	 * Wraps StringUtils.replaceEach to shorten it. Replaces special html
	 * chars that would conflict with xml structure
	 *
	 * @param str String to be escaped
	 * @return The escaped string
	 */
	private String escapeChars(String str) {
		str = StringUtils.replaceEach(
				str, new String[]{"&", "\"", "<", ">"},
				new String[]{"&amp;", "&quot;", "&lt;", "&gt;"});
		return str;
	}

}
