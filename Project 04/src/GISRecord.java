/**
 * This is the GISRecord class. It contains the information of entire GIS places
 * inside the specified file to read from.
 * 
 * @author divit52 Divit Singh
 * @since March 24, 2012
 */
public class GISRecord {

	String fId;
	String fName;
	String fClass;
	String stateAlphCode;
	String stateNumCode;
	String countyName;
	String countyNum;
	String primLatDMS;
	String primLongDMS;
	String primLatDec;
	String primLongDec;
	String latDMSSrc;
	String longDMSSrc;
	String latDecSrc;
	String longDecSrc;
	String elevMeters;
	String elevFeet;
	String mapName;
	String dateCreated;
	String dateEdited;

	/**
	 * 
	 * @param fId
	 *            feature id name
	 * @param fName
	 *            feature name
	 * @param fClass
	 *            feature id class
	 * @param stateCode
	 *            feature state code
	 * @param countyName
	 *            feature county name
	 * @param countyNum
	 *            feature county number
	 * @param primLatDMS
	 *            latitude of feature in DMS
	 * @param primLongDMS
	 *            longitude of feature in DMS
	 * @param primLatDec
	 *            latitude of feature in decimal
	 * @param primLongDec
	 *            longitude of feature in decimal
	 * @param latDMSSrc
	 *            source of latitude
	 * @param longDMSSrc
	 *            source of longitude
	 * @param latDecSrc
	 *            source of latitude
	 * @param longDecSrc
	 *            source of longitude
	 * @param elevMeters
	 *            elevation of feature in meters
	 * @param elevFeet
	 *            elevation of feature in feet
	 * @param mapName
	 *            name of map
	 * @param dateCreated
	 *            date feature was created in database
	 * @param dateEdited
	 *            data feature was altered in database
	 */
	public GISRecord(String fId, String fName, String fClass,
			String stateAlphCode, String stateNumCode, String countyName,
			String countyNum, String primLatDMS, String primLongDMS,
			String primLatDec, String primLongDec, String latDMSSrc,
			String longDMSSrc, String latDecSrc, String longDecSrc,
			String elevMeters, String elevFeet, String mapName,
			String dateCreated, String dateEdited) {
		this.fId = fId;
		this.fName = fName;
		this.fClass = fClass;
		this.stateAlphCode = stateAlphCode;
		this.stateNumCode = stateNumCode;
		this.countyName = countyName;
		this.countyNum = countyNum;
		this.primLatDMS = primLatDMS;
		this.primLongDMS = primLongDMS;
		this.primLatDec = primLatDMS;
		this.primLongDec = primLongDec;
		this.latDMSSrc = latDMSSrc;
		this.longDMSSrc = longDMSSrc;
		this.latDecSrc = latDecSrc;
		this.longDecSrc = longDecSrc;
		this.elevMeters = elevMeters;
		this.elevFeet = elevFeet;
		this.mapName = mapName;
		this.dateCreated = dateCreated;
		this.dateEdited = dateEdited;

	}

	/**
	 * Returns feature name + state code concatinated together.
	 * 
	 * @return featurename + statecode
	 */
	public String nameAndState() {
		return this.fName + this.stateAlphCode;
	}

	/**
	 * Returns the Lattitude of object in long form so that it may be inserted
	 * correctly into quadtree.
	 * 
	 * @return decimal form of lattitude
	 */
	public long getLatDec() {
		return Long.parseLong(primLatDec);
	}

	/**
	 * Returns the Feature ID of this particular object.
	 * 
	 * @return String Feature ID of this object.
	 */
	public String getFid() {
		return this.fId;
	}

	/**
	 * Returns the Longitude of object in long form so that it may be inserted
	 * correctly into quadtree.
	 * 
	 * @return decimal form of longitude
	 */
	public long getLongDec() {
		return Long.parseLong(primLongDec);
	}

	/**
	 * Breaks up the entire Lattitude string so that it may attach days,
	 * minutes, and seconds onto appropriate parts of the string. It changes the
	 * lattitude to the following format: __d __m ___s K, where K is either
	 * North of South and __ are areas where numbers will be displayed.
	 * 
	 * @param lat
	 *            Lattitude of object
	 */
	public String parseLattitude(String lat) {
		int conversion = Integer.parseInt(lat.substring(0, lat.length() - 1));
		long seconds = conversion % 100;
		long minutes = (conversion % 10000) / 100;
		long days = (conversion / 10000);

		String direction = determineNS(lat.substring(lat.length() - 1,
				lat.length()));

		return "" + days + "d " + minutes + "m " + seconds + "s " + direction;
	}

	/**
	 * Breaks up the entire Longitude string so that it may attach days,
	 * minutes, and seconds onto appropriate parts of the string. It changes the
	 * longitude string in the following format: __d __m ___s K, where K is
	 * either East of West and __ are areas where numbers will be displayed.
	 * 
	 * @param lon
	 *            Longitude of object
	 */
	public String parseLongitude(String lon) {
		int conversion = Integer.parseInt(lon.substring(0, lon.length() - 1));
		int seconds = conversion % 100;
		int minutes = (conversion % 10000) / 100;
		int days = (conversion / 10000);
		String direction = determineEW(lon.substring(lon.length() - 1,
				lon.length()));
		return "" + days + "d " + minutes + "m " + seconds + "s " + direction;
	}

	/**
	 * Returns a formatted string representing all of the information of this
	 * GISRecord Object. It will return the correctly parsed lattitude and
	 * longitude values.
	 * 
	 * @return String that represents all of the values in this object.
	 */
	public String getInfoString() {
		String latDMS = parseLattitude(primLatDMS);
		String lonDMS = parseLattitude(primLongDMS);
		return fId + " " + fName + " " + latDMS + " " + lonDMS;
	}

	/**
	 * Helper method to determine if the lattitude should be in the Northern
	 * Hemisphere or the Southern.
	 * 
	 * @param dir
	 *            direction of the last character of lattitude field. Expects
	 *            either a "N" or "S".
	 * @return String that determines the correct hemisphere.
	 */
	public String determineNS(String dir) {
		if (dir.equals("N")) {
			return "North";
		} else {
			return "South";
		}
	}

	/**
	 * Helper method to determine if the longitude should be in the Easter
	 * Hemisphere or the Western.
	 * 
	 * @param dir
	 *            direction of the last character of longitude field. Expects
	 *            either a "E" or "W".
	 * @return String that determines the correct hemisphere.
	 */
	public String determineEW(String dir) {
		if (dir.equals("E")) {
			return "East";
		} else {
			return "West";
		}
	}

	/**
	 * Returns information in String form.
	 * 
	 * @return Information about object
	 */
	public String toString() {
		return fId + " " + fName + " " + stateAlphCode + " " + stateNumCode
				+ " " + countyName + " " + countyNum + " " + primLatDMS + " "
				+ primLongDMS + " " + latDMSSrc + " " + longDMSSrc + " "
				+ latDecSrc + " " + longDecSrc + " " + elevMeters + " "
				+ elevFeet + " " + mapName + " " + dateCreated + " "
				+ dateEdited;
	}

}
