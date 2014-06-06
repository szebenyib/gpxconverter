import org.apache.commons.lang3.StringUtils;

/**
 * Created by Balint Szebenyi on 2014.05.30..
 * Stores the individual logs and creates a gpx text for them.
 */
public class Log {

	private String logId;
	private String logDate;
	private String logFinderId;
	private String logFinderName;
	private String logTextIsEncoded;
	private String logText;

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public void setLogDate(String logDate) {
		this.logDate = logDate;
	}

	public void setLogFinderId(String logFinderId) {
		this.logFinderId = logFinderId;
	}

	public void setLogFinderName(String logFinderName) {
		this.logFinderName = logFinderName;
	}

	public void setLogTextIsEncoded(String logTextIsEncoded) {
		this.logTextIsEncoded = logTextIsEncoded;
	}

	public void setLogText(String logText) {
		this.logText = logText;
	}

	public Log deepCopyOfLog() {
		Log newLog = new Log();
		newLog.setLogId(this.logId);
		newLog.setLogDate(this.logDate);
		newLog.setLogFinderId(this.logFinderId);
		newLog.setLogFinderName(this.logFinderName);
		newLog.setLogTextIsEncoded(this.logTextIsEncoded);
		newLog.setLogText(this.logText);
		return newLog;
	}

	public String getLogGPXPart() {
		return "\t\t\t<groundspeak:log id=\"" +
				this.logId +
				"\">\n" +
				"\t\t\t\t<groundspeak:date>" +
				this.logDate +
				"</groundspeak:date>\n" +
				"\t\t\t\t<groundspeak:finder id=\"" +
				this.logFinderId +
				"\">" +
				escapeChars(this.logFinderName) +
				"</groundspeak:finder>\n" +
				"\t\t\t\t<groundspeak:text encoded=\"" +
				this.logTextIsEncoded +
				"\">" +
				escapeChars(this.logText) +
				"</groundspeak:text>\n" +
				"\t\t\t</groundspeak:log>\n";
	}

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
