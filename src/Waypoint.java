/**
 * Created by Balint Szebenyi on 2014.05.29..
 * The root of all the waypoints, it stores them, provides functions for
 * processing and also a gpx text representation.
 */
public class Waypoint {

	/**
	 * If variable name is prefixed with 'A' it stand for an attribute
	 */
	private String wptALat;
	private String wptALon;
	private String ele;
	private String time;
	private String name;
	private String cmt;
	private String desc;
	private String url;
	private String urlname;
	private String sym;
	private String type;

	public Waypoint() {

	}

	public String getWptALon() {
		return wptALon;
	}

	public void setWptALon(String wptALon) {
		this.wptALon = wptALon;
	}

	public String getWptALat() {
		return wptALat;
	}

	public void setWptALat(String wptALat) {
		this.wptALat = wptALat;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEle() {
		return ele;
	}

	public void setEle(String ele) {
		this.ele = ele;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCmt() {
		return cmt;
	}

	public void setCmt(String cmt) {
		this.cmt = cmt;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlname() {
		return urlname;
	}

	public void setUrlname(String urlname) {
		this.urlname = urlname;
	}

	public String getSym() {
		return sym;
	}

	public void setSym(String sym) {
		this.sym = sym;
	}

	@Override
	public String toString() {
		return this.name;
	}

	/**
	 * Replaces symbols from geocaching.hu that are not recognised by locus.
	 */
	public void replaceUnsupportedSymbols() {
		if (this.sym.equalsIgnoreCase("Dot")) {
			this.sym = "Reference Point";
		}
		if (this.type.equalsIgnoreCase("Dot")) {
			this.type = "Reference Point";
		}
	}

	/**
	 * Replaces symbols in multi stages that are invalid.
	 */
	public void replaceWrongMultiSymbols() {
		if (this.sym.equalsIgnoreCase("Geocache")) {
			this.sym = "Physical stage";
		}
		if (this.type.equalsIgnoreCase("Geocache")) {
			this.type = "Physical stage";
		}
	}

	/**
	 * Generates the output for the top part that is common both for
	 * waypoints and geocaches. Only the common fields are included.
	 *
	 * @param waypointOnly If it is only a waypoint a closing </wpt> tag is
	 *                     included in the end. If false the closing tag
	 *                     is not included.
	 * @return GPX content
	 */
	public String getGeneralGPXPart(boolean waypointOnly) {
		String str = "<wpt lat=\"" + this.wptALat + "\" " +
				"lon=\"" + this.wptALon + "\">\n";
		if (this.ele == null) {
			str += "\t<ele>" + 0 + "</ele>\n";
		} else {
			str += "\t<ele>" + this.ele + "</ele>\n";
		}
		str += "\t<time>" + this.time + "</time>\n" +
				"\t<name>" + this.name + "</name>\n" +
				"\t<cmt>" + this.cmt + "</cmt>\n" +
				"\t<desc><![CDATA[" +
				this.desc +
				"]]></desc>\n" +
				"\t<url>" + this.url + "</url>\n" +
				"\t<urlname><![CDATA[" +
				this.urlname +
				"]]></urlname>\n" +
				"\t<sym>" + this.sym + "</sym>\n" +
				"\t<type><![CDATA[" +
				this.type +
				"]]></type>\n";
		if (waypointOnly) {
			//Include closing tag
			return str + "</wpt>\n\n";
		} else {
			return str;
		}
	}

	/**
	 * Tells if the name of the waypoint is one of a multi by checking if
	 * there are two dashes ('-') present in the name.
	 * @return True if yes.
	 */
	public boolean hasNameOfMulti() {
		return this.name.indexOf('-') != this.name.lastIndexOf('-');
	}

	/**
	 * Gets the names first six letters. It is mainly a substring wrapper but
	 * enhances readability of the code.
	 * @return The first six letters of the name as a String.
	 */
	public String getNameFirstSixLetters() {
		return this.name.substring(0, 6);
	}

}
