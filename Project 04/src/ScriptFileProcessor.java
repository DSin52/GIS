import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Vector;

/**
 * This class goes through and contains all methods that actually execute the
 * commands accordingly.
 * 
 * @author Divit Singh divit52
 * 
 */
public class ScriptFileProcessor {
	String dataBaseFile;
	File dataFile;
	FileWriter dataWriter;
	FileWriter logWriter;
	private long longMin;
	private long longMax;
	private long latMin;
	BufferPool pool;
	HashTable hashTable;
	private long latMax;
	private int importedFiles;
	private prQuadtree<Coordinates> tree;

	public ScriptFileProcessor(String dataBaseFile, FileWriter logWriter,
			long longMin, long longMax, long latMin, long latMax) {
		this.dataBaseFile = dataBaseFile;
		dataFile = new File(dataBaseFile);
		this.logWriter = logWriter;
		this.longMin = longMin;
		importedFiles = 0;
		pool = new BufferPool();
		this.longMax = longMax;
		hashTable = new HashTable();
		this.latMin = latMin;
		this.latMax = latMax;
		tree = new prQuadtree<Coordinates>(longMin, longMax, latMin, latMax);
	}

	/**
	 * Writes information to database that is read from record file into the
	 * database if it lies between the bounds of the region being searched for.
	 * 
	 * @param recordFile
	 *            file to read from
	 */
	public void writeToDB(String recordFile) {
		try {
			importedFiles = 0;
			RandomAccessFile record = new RandomAccessFile(recordFile, "r");
			dataWriter = new FileWriter(dataFile, true);
			if (dataFile.length() == 0) {
				dataWriter.write(record.readLine() + "\n");
			}
			if (dataFile.length() > 0) {
				record.readLine();
			}
			while (record.getFilePointer() < record.length()) {
				String gis = record.readLine();
				// String[] gisRecord = gis.split("[|]");
				GISRecord gRec = createGIS(gis);
				// System.out.println(gRec.fName);
				if (checkBounds(gRec.primLongDMS, gRec.primLatDMS) == true) {
					dataWriter.write(gis + "\n");
					importedFiles++;
				}
			}

			dataWriter.close();
			record.close();
			addCoordinates();
			System.out.println(tree.prQuadTreeSize);
		} catch (FileNotFoundException e) {
			try {
				logWriter.write("File not found.");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(1);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException ex) {
			try {
				logWriter.write("Database has no name specified");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Returns number of imported files.
	 * 
	 * @return number of imported files
	 */
	public int getImportedFilesNum() {
		return importedFiles;
	}

	/**
	 * Adds all the coordinates that are in database into the quad tree as well
	 * as the hashtable.
	 */
	public void addCoordinates() {
		try {
			RandomAccessFile dataAccess = new RandomAccessFile(dataFile, "r");
			dataAccess.readLine();
			while (dataAccess.getFilePointer() < dataAccess.length()) {
				long filePointerRef = dataAccess.getFilePointer();
				GISRecord gRec = createGIS(dataAccess.readLine());
				Coordinates gisCoord = new Coordinates(
						convertToSecondsLong(gRec.primLongDMS),
						convertToSecondsLat(gRec.primLatDMS));

				hashTable.insert(gRec.fName + ":" + gRec.stateAlphCode,
						filePointerRef);
				if (tree.find(gisCoord) != null) {
					boolean isContained = false;
					for (int i = 0; i < tree.find(gisCoord).offsets.size(); i++) {
						dataAccess.seek(tree.find(gisCoord).offsets.get(i));
						GISRecord tester = createGIS(dataAccess.readLine());
						if (gRec.fId.equals(tester.fId)) {
							isContained = true;
						}
					}
					if (!isContained) {
						tree.find(gisCoord).getList().add(filePointerRef);
					}
					dataAccess.seek(filePointerRef);
					dataAccess.readLine();
				} else {
					tree.insert(gisCoord);
					tree.find(gisCoord).getList().add(filePointerRef);
				}
			}
			// System.out.println(hashTable.size());

			dataAccess.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns all values that are at specified coordinates. Puts results into
	 * log.
	 * 
	 * @param lon
	 *            longitude
	 * @param lat
	 *            latitude
	 */
	public void whatIsAt(String lon, String lat) {
		try {
			RandomAccessFile dataAccess = new RandomAccessFile(dataFile, "r");
			long longitude = convertToSecondsLong(lon);
			long lattitude = convertToSecondsLat(lat);
			Coordinates gisCoord = new Coordinates(longitude, lattitude);
			Coordinates treeRefCoord = tree.find(gisCoord);
			if (treeRefCoord == null) {
				logWriter.write("\tNothing was found in " + lon + "\t" + lat
						+ "\r\n");
				dataAccess.close();
				return;
			} else {
				logWriter.write("The following features were found at " + lon
						+ "\t" + lat + ":\r\n");
				for (int i = 0; i < treeRefCoord.getList().size(); i++) {
					long filePointer = treeRefCoord.getList().get(i);
					dataAccess.seek(filePointer);
					// String[] splitter = dataAccess.readLine().split("[|]");
					String poolRef = dataAccess.readLine();
					pool.insertAtHead(filePointer + ":\t" + poolRef);
					GISRecord gRec = createGIS(poolRef);
					logWriter.write(tree.find(gisCoord).getList().get(i)
							+ ":\t" + gRec.fName + "\t" + gRec.countyName
							+ "\t" + gRec.stateAlphCode + "\r\n");
				}
			}
			dataAccess.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Returns all nonempty values at specified location in a uniform manner.
	 * Puts results into log.
	 * 
	 * @param lon
	 *            longitude
	 * @param lat
	 *            latitude
	 */
	public void whatIsAtL(String lon, String lat) {
		try {
			RandomAccessFile dataAccess = new RandomAccessFile(dataFile, "r");
			long longitude = convertToSecondsLong(lon);
			long lattitude = convertToSecondsLat(lat);
			Coordinates gisCoord = new Coordinates(longitude, lattitude);
			Coordinates treeRefCoord = tree.find(gisCoord);
			if (treeRefCoord == null) {
				logWriter.write("\tNothing was found in " + lon + "\t" + lat
						+ "\r\n");
				dataAccess.close();
				return;
			} else {
				logWriter.write("The following features were found at " + lon
						+ "\t" + lat + ":\r\n");
				for (int i = 0; i < treeRefCoord.getList().size(); i++) {
					long filePointer = treeRefCoord.getList().get(i);
					dataAccess.seek(filePointer);
					String poolRef = dataAccess.readLine();
					pool.insertAtHead(filePointer + ":\t" + poolRef);
					String[] gArray = poolRef.split("[|]");
					GISRecord gRec = createGIS(poolRef);
					for (int j = 0; j < 19; j++) {
						if (gArray[j].length() > 0
								&& gRec.gisFields()[j].length() > 0) {
							logWriter.write(gRec.gisFields()[j] + "\t:\t"
									+ gArray[j] + "\r\n");
						}
					}
				}
			}
			dataAccess.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Returns number of values that are at specified coordinates.
	 * 
	 * @param lon
	 *            longitude
	 * @param lat
	 *            latitude
	 */
	public void whatIsAtC(String lon, String lat) {
		try {
			long longitude = convertToSecondsLong(lon);
			long lattitude = convertToSecondsLat(lat);
			Coordinates gisCoord = new Coordinates(longitude, lattitude);
			Coordinates treeRefCoord = tree.find(gisCoord);
			if (treeRefCoord == null) {
				logWriter.write("\tNothing was found in " + lon + "\t" + lat
						+ "\r\n");
				return;
			} else {
				logWriter.write(treeRefCoord.offsets.size()
						+ " features were found at " + lon + "\t" + lat
						+ "\r\n");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Returns the minimum longitude value in the world.
	 * 
	 * @return minimum longitude
	 */
	public long getLongMin() {
		return longMin;
	}

	/**
	 * Returns the max longitude value in the world.
	 * 
	 * @return max longitude
	 */
	public long getLongMax() {
		return longMax;
	}

	/**
	 * Returns the minimum latitude value in the world.
	 * 
	 * @return minimum latitude
	 */
	public long getLatMin() {
		return latMin;
	}

	/**
	 * Returns the max latitude value in the world.
	 * 
	 * @return max latitude
	 */
	public long getLatMax() {
		return latMax;
	}

	/**
	 * Converts strings into seconds and sees if they lie within the bounds.
	 * 
	 * @param lon
	 *            longitude
	 * @param lat
	 *            latitude
	 * @return true if within boundaries, false otherwise
	 */
	private boolean checkBounds(String lon, String lat) {
		long lonNum = convertToSecondsLong(lon);
		long latNum = convertToSecondsLat(lat);
		if (lonNum >= getLongMin() && lonNum <= getLongMax()
				&& latNum >= getLatMin() && latNum <= getLatMax()) {
			return true;
		}
		return false;
	}

	/**
	 * Converts specified string into numerical form.
	 * 
	 * @param lat
	 *            latitude
	 * @return number corresponding to string
	 */
	public Long convertToLat(String lat) {
		long conversion = Long.parseLong(lat.substring(0, lat.length() - 1));
		if (lat.substring(lat.length() - 1, lat.length()).equals("S")) {
			return -(conversion);
		}
		return (conversion);
	}

	/**
	 * Converts specified string into numerical form.
	 * 
	 * @param lon
	 *            longitude
	 * @return number corresponding to string
	 */
	public Long convertToLong(String lon) {
		long conversion = Long.parseLong(lon.substring(0, lon.length() - 1));
		if (lon.substring(lon.length() - 1, lon.length()).equals("W")) {
			return -(conversion);
		}
		return (conversion);
	}

	/**
	 * Creates a new GISRecord object with Feature Id, Feature name, Lattitude
	 * and Longitude in that order.
	 * 
	 * @param info
	 *            String that contains all the information needed to make a
	 *            GISRecord.
	 * @return GISRecord which contains all the information provided in info.
	 */
	public GISRecord createGIS(String info) {
		String[] lineInfo = splitLine(info);
		if (lineInfo.length == 20) {
			GISRecord gFile = new GISRecord(lineInfo[0], lineInfo[1],
					lineInfo[2], lineInfo[3], lineInfo[4], lineInfo[5],
					lineInfo[6], lineInfo[7], lineInfo[8], lineInfo[9],
					lineInfo[10], lineInfo[11], lineInfo[12], lineInfo[13],
					lineInfo[14], lineInfo[15], lineInfo[16], lineInfo[17],
					lineInfo[18], lineInfo[19]);
			return gFile;
		} else {
			GISRecord gFile = new GISRecord(lineInfo[0], lineInfo[1],
					lineInfo[2], lineInfo[3], lineInfo[4], lineInfo[5],
					lineInfo[6], lineInfo[7], lineInfo[8], lineInfo[9],
					lineInfo[10], lineInfo[11], lineInfo[12], lineInfo[13],
					lineInfo[14], lineInfo[15], lineInfo[16], lineInfo[17],
					lineInfo[18], ",");
			return gFile;
		}

	}

	/**
	 * Splits a string into an array. Uses "|" as a regular expression to have a
	 * split point. Returns an array containing all the values of the string
	 * provided.
	 * 
	 * @param line
	 *            line that has all of the information needed to be processed.
	 * @return String[] which contains information from line at specific index
	 *         positions.
	 */
	public String[] splitLine(String line) {
		String[] lineInfo = line.split("[|]");
		return lineInfo;
	}

	/**
	 * Returns all values that match the specified bounds.
	 * 
	 * @param latString
	 *            latitude being searched for
	 * @param longString
	 *            longitude being searched for
	 * @param latDifS
	 *            leniency in latitude bounds searching
	 * @param longDifS
	 *            leniency in longitude bounds searching
	 */
	public void whatIsInFinder(String latString, String longString,
			String latDifS, String longDifS) {
		long lattitude = convertToSecondsLat(latString);
		long longitude = convertToSecondsLong(longString);
		long latDif = Long.parseLong(latDifS);
		long longDif = Long.parseLong(longDifS);
		Vector<Coordinates> coordVec = tree.find(longitude - longDif, longitude
				+ longDif, lattitude - latDif, lattitude + latDif);
		try {
			if (coordVec.size() == 0) {
				logWriter.write("Nothing was found in (" + longString + " +/- "
						+ longDifS + ", " + latString + " +/- " + latDifS
						+ ")\r\n");
				return;
			} else {

				RandomAccessFile dataAccess = new RandomAccessFile(dataFile,
						"r");
				Coordinates coordRef;
				int numCoords = 0;
				for (int i = 0; i < coordVec.size(); i++) {
					Coordinates coords = coordVec.get(i);
					numCoords += coords.offsets.size();
				}
				logWriter.write("\tThe following " + numCoords
						+ " features were found in (" + longString + " +/- "
						+ longDif + ", " + latString + " +/- " + latDif
						+ ")\r\n");
				for (int i = 0; i < coordVec.size(); i++) {
					coordRef = coordVec.get(i);
					for (long offset : coordRef.offsets) {
						logWriter.write(offset + ":\t");
						dataAccess.seek(offset);
						// String[] splitter =
						// dataAccess.readLine().split("[|]");
						String poolRef = dataAccess.readLine();
						pool.insertAtHead(offset + ":\t" + poolRef);
						GISRecord gRec = createGIS(poolRef);
						logWriter.write(gRec.fName + "\t" + gRec.stateAlphCode
								+ "\t" + gRec.primLatDMS + "\t"
								+ gRec.primLongDMS + "\r\n");
					}
				}

				dataAccess.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns all non-empty values of coordinates that are within the specified
	 * bounds.
	 * 
	 * @param latString
	 *            latitude string being searched
	 * @param longString
	 *            longitude string being searched
	 * @param latDifS
	 *            latitude difference to account for
	 * @param longDifS
	 *            longitude difference to account for
	 */
	public void whatIsInLFinder(String latString, String longString,
			String latDifS, String longDifS) {
		long lattitude = convertToSecondsLat(latString);
		long longitude = convertToSecondsLong(longString);
		long latDif = Long.parseLong(latDifS);
		long longDif = Long.parseLong(longDifS);
		Vector<Coordinates> coordVec = tree.find(longitude - longDif, longitude
				+ longDif, lattitude - latDif, lattitude + latDif);
		try {
			if (coordVec.size() == 0) {
				logWriter.write("Nothing was found in (" + longString + " +/- "
						+ longDifS + ", " + latString + " +/- " + latDifS
						+ ")\r\n");
				return;
			} else {
				int numCoords = 0;
				for (int i = 0; i < coordVec.size(); i++) {
					Coordinates coords = coordVec.get(i);
					numCoords += coords.offsets.size();
				}
				logWriter.write("\tThe following " + numCoords
						+ " features were found in (" + longString + " +/- "
						+ longDif + ", " + latString + " +/- " + latDif
						+ ")\r\n");
				RandomAccessFile dataAccess = new RandomAccessFile(dataFile,
						"r");
				for (Coordinates treeRefCoord : coordVec) {
					for (int i = 0; i < treeRefCoord.getList().size(); i++) {
						long filePointer = treeRefCoord.getList().get(i);
						dataAccess.seek(filePointer);
						String poolRef = dataAccess.readLine();
						pool.insertAtHead(filePointer + ":\t" + poolRef);
						String[] gArray = poolRef.split("[|]");
						// System.out.println(gRef);
						GISRecord gRec = createGIS(poolRef);
						int j;
						for (j = 0; j < 19; j++) {
							if (gArray[j].length() > 0
									&& gRec.gisFields()[j].length() > 0) {
								logWriter.write(gRec.gisFields()[j] + "\t:\t"
										+ gArray[j] + "\r\n");
							}

						}
						if (j == 19) {
							logWriter.write("\r\n");
						}
					}
				}

				dataAccess.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns number of values within the specified boundaries
	 * 
	 * @param latString
	 *            latitude being searched
	 * @param longString
	 *            longitude being searched
	 * @param latDifS
	 *            latitude leniency being searched
	 * @param longDifS
	 *            longitude leniency being searched
	 */
	public void whatIsInCFinder(String latString, String longString,
			String latDifS, String longDifS) {
		long lattitude = convertToSecondsLat(latString);
		long longitude = convertToSecondsLong(longString);
		long latDif = Long.parseLong(latDifS);
		long longDif = Long.parseLong(longDifS);
		Vector<Coordinates> coordVec = tree.find(longitude - longDif, longitude
				+ longDif, lattitude - latDif, lattitude + latDif);

		try {
			int totalOffsets = 0;
			for (Coordinates coord : coordVec) {
				totalOffsets += coord.offsets.size();
			}

			logWriter.write("\t" + totalOffsets + " features were found in ("
					+ longString + " +/- " + longDif + ", " + latString
					+ " +/- " + latDif + ")\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public long convertToSecondsLong(String value) {
		String days = value.substring(0, 3);
		String minutes = value.substring(3, 5);
		String secondString = value.substring(5, value.length() - 1);
		long dayToSeconds = Long.parseLong(days) * 3600;
		long minutesToSeconds = Long.parseLong(minutes) * 60;
		long seconds = Long.parseLong(secondString);
		if (value.substring(value.length() - 1).equals("W")) {
			return -(dayToSeconds + minutesToSeconds + seconds);
		}
		return dayToSeconds + minutesToSeconds + seconds;
	}

	public long convertToSecondsLat(String value) {
		String days = value.substring(0, 2);
		String minutes = value.substring(2, 4);
		String secondString = value.substring(4, value.length() - 1);
		long dayToSeconds = Long.parseLong(days) * 3600;
		long minutesToSeconds = Long.parseLong(minutes) * 60;
		long seconds = Long.parseLong(secondString);
		if (value.substring(value.length() - 1).equals("S")) {
			return -(dayToSeconds + minutesToSeconds + seconds);
		}
		return dayToSeconds + minutesToSeconds + seconds;
	}

	/**
	 * Returns size of hash table.
	 * 
	 * @return size of hashtable
	 */
	public int hashTableSize() {
		return hashTable.getFilled();
	}

	/**
	 * Gives all values that have the same feature and state name.
	 * 
	 * @param feature
	 *            feature being searched
	 * @param state
	 *            state being searched
	 */
	public void whatIsFinder(String feature, String state) {
		ArrayList<Long> offsets = hashTable.getList(feature + ":" + state);
		RandomAccessFile dataAccess;

		try {
			if (offsets.size() == 0) {

				logWriter.write("No records match " + feature + " and " + state
						+ "\r\n");
				return;

			}
			dataAccess = new RandomAccessFile(dataFile, "r");
			for (int i = 0; i < offsets.size(); i++) {
				dataAccess.seek(offsets.get(i));
				String poolRef = dataAccess.readLine();
				pool.insertAtHead(offsets.get(i) + ":\t" + poolRef);
				GISRecord gRec = createGIS(poolRef);
				logWriter.write(offsets.get(i) + ":\t" + gRec.countyName + "\t"
						+ gRec.primLongDMS + "\t" + gRec.primLatDMS + "\r\n");
			}
			dataAccess.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns all nonempty values corresponding to the coordinate in hash
	 * table.
	 * 
	 * @param feature
	 *            feature being searched
	 * @param state
	 *            state being searched
	 */
	public void whatIsLFinder(String feature, String state) {
		RandomAccessFile dataAccess;
		ArrayList<Long> offsets = hashTable.getList(feature + ":" + state);
		try {
			if (offsets.size() == 0) {

				logWriter.write("No records match " + feature + " and " + state
						+ "\r\n");
				return;

			}
			dataAccess = new RandomAccessFile(dataFile, "r");
			for (int i = 0; i < offsets.size(); i++) {
				dataAccess.seek(offsets.get(i));
				String poolRef = dataAccess.readLine();
				pool.insertAtHead(offsets.get(i) + ":\t" + poolRef);
				String[] gArray = poolRef.split("[|]");
				GISRecord gRec = createGIS(poolRef);
				logWriter.write(offsets.get(i) + ":\t" + gRec.countyName + "\t"
						+ gRec.primLongDMS + "\t" + gRec.primLatDMS + "\r\n");
				logWriter.write("Found matching record at offset "
						+ offsets.get(i) + ":\r\n\r\n");
				for (int j = 0; j < 19; j++) {
					if (gArray[j].length() > 0
							&& gRec.gisFields()[j].length() > 0) {
						logWriter.write(gRec.gisFields()[j] + "\t:\t"
								+ gArray[j] + "\r\n");
					}

				}
			}
			dataAccess.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Returns tree representaiton of values in a uniform matter.
	 */
	public void debugQuad() {
		tree.printTreeHelper(tree.root, " ", logWriter);
	}

	/**
	 * Returns all values in hashtable in uniform manner.
	 */
	public void debugHash() {
		try {
			logWriter.write(hashTable.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Returns all contents in the pool in a uniform matter MRU --> LRU.
	 */
	public void debugPool() {
		try {
			logWriter.write(pool.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
