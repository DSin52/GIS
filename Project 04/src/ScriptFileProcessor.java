import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

public class ScriptFileProcessor {
	String dataBaseFile;
	File dataFile;
	FileWriter dataWriter;
	FileWriter logWriter;
	private long longMin;
	private long longMax;
	private long latMin;
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
		this.longMax = longMax;
		hashTable = new HashTable();
		this.latMin = latMin;
		this.latMax = latMax;
		tree = new prQuadtree<Coordinates>(longMin, longMax, latMin, latMax);
	}

	public void writeToDB(String recordFile) {
		try {
			RandomAccessFile record = new RandomAccessFile(recordFile, "r");
			dataWriter = new FileWriter(dataFile);
			dataWriter.write(record.readLine() + "\n");
			while (record.getFilePointer() < record.length()) {
				String gis = record.readLine();
				// String[] gisRecord = gis.split("[|]");
				GISRecord gRec = createGIS(gis);
				if (checkBounds(gRec.primLongDMS, gRec.primLatDMS) == true) {
					dataWriter.write(gis + "\n");
					importedFiles++;
				}
			}

			dataWriter.close();
			record.close();
			addCoordinates();
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

	public int getImportedFilesNum() {
		return importedFiles;
	}

	public void addCoordinates() {
		try {
			RandomAccessFile dataAccess = new RandomAccessFile(dataFile, "r");
			dataAccess.readLine();
			while (dataAccess.getFilePointer() < dataAccess.length()) {
				long filePointerRef = dataAccess.getFilePointer();
				// String[] gisRecord = dataAccess.readLine().split("[|]");
				GISRecord gRec = createGIS(dataAccess.readLine());
				Coordinates gisCoord = new Coordinates(
						convertToSecondsLong(gRec.primLongDMS),
						convertToSecondsLat(gRec.primLatDMS));
				// System.out.println(hashTable.elfHash(gRec.fName
				// + gRec.stateAlphCode)
				// % hashTable.table.length);
				hashTable.insert(gRec.fName + ":" + gRec.stateAlphCode,
						filePointerRef);
				// hashTable.insert(gRec.fName + gRec.stateAlphCode,
				// filePointerRef);
				if (tree.find(gisCoord) != null) {
					tree.find(gisCoord).getList().add(filePointerRef);
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
					GISRecord gRec = createGIS(dataAccess.readLine());
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
					String gRef = dataAccess.readLine();
					String[] gArray = gRef.split("[|]");
					GISRecord gRec = createGIS(gRef);
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

	public long getLongMin() {
		return longMin;
	}

	public long getLongMax() {
		return longMax;
	}

	public long getLatMin() {
		return latMin;
	}

	public long getLatMax() {
		return latMax;
	}

	private boolean checkBounds(String lon, String lat) {
		long lonNum = convertToSecondsLong(lon);
		long latNum = convertToSecondsLat(lat);
		if (lonNum >= getLongMin() && lonNum <= getLongMax()
				&& latNum >= getLatMin() && latNum <= getLatMax()) {
			return true;
		}
		return false;
	}

	public Long convertToLat(String lat) {
		long conversion = Long.parseLong(lat.substring(0, lat.length() - 1));
		if (lat.substring(lat.length() - 1, lat.length()).equals("S")) {
			return -(conversion);
		}
		return (conversion);
	}

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
				logWriter.write("\tThe following " + coordVec.size()
						+ " features were found in (" + latString + " +/- "
						+ latDif + ", " + longString + " +/- " + longDif
						+ ")\r\n");
				RandomAccessFile dataAccess = new RandomAccessFile(dataFile,
						"r");
				Coordinates coordRef;
				for (int i = 0; i < coordVec.size(); i++) {
					coordRef = coordVec.get(i);
					for (long offset : coordRef.offsets) {
						logWriter.write(offset + ":\t");
						dataAccess.seek(offset);
						// String[] splitter =
						// dataAccess.readLine().split("[|]");
						GISRecord gRec = createGIS(dataAccess.readLine());
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
				logWriter.write("\tThe following " + coordVec.size()
						+ " features were found in (" + latString + " +/- "
						+ latDif + ", " + longString + " +/- " + longDif
						+ ")\r\n");
				RandomAccessFile dataAccess = new RandomAccessFile(dataFile,
						"r");
				for (Coordinates treeRefCoord : coordVec) {
					for (int i = 0; i < treeRefCoord.getList().size(); i++) {
						long filePointer = treeRefCoord.getList().get(i);
						dataAccess.seek(filePointer);
						String gRef = dataAccess.readLine();
						String[] gArray = gRef.split("[|]");
						GISRecord gRec = createGIS(gRef);
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
					+ latString + " +/- " + latDif + ", " + longString
					+ " +/- " + longDif + ")\r\n");
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

	public int hashTableSize() {
		return hashTable.size();
	}

	public void whatIsFinder(String feature, String state) {
		long offset = hashTable.get(feature + ":" + state);
		try {
			RandomAccessFile dataAccess = new RandomAccessFile(dataFile, "r");
			dataAccess.seek(offset);
			String gRef = dataAccess.readLine();
			GISRecord gRec = createGIS(gRef);
			logWriter.write(offset + ":\t" + gRec.countyName + "\t"
					+ gRec.primLongDMS + "\t" + gRec.primLatDMS + "\r\n");
			dataAccess.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void whatIsLFinder(String feature, String state) {

		long offset = hashTable.get(feature + ":" + state);
		RandomAccessFile dataAccess;
		try {
			dataAccess = new RandomAccessFile(dataFile, "r");
			dataAccess.seek(offset);
			String gRef = dataAccess.readLine();
			String[] gArray = gRef.split("[|]");
			GISRecord gRec = createGIS(gRef);
			for (int j = 0; j < 19; j++) {
				if (gArray[j].length() > 0 && gRec.gisFields()[j].length() > 0) {
					logWriter.write(gRec.gisFields()[j] + "\t:\t" + gArray[j]
							+ "\r\n");
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

	public void debugQuad() {
		tree.printTreeHelper(tree.root, " ", logWriter);
	}

	public void debugHash() {
		try {
			logWriter.write(hashTable.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
