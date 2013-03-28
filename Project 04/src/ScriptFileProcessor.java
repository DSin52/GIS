import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ScriptFileProcessor {
	String dataBaseFile;
	File dataFile;
	FileWriter dataWriter;
	FileWriter logWriter;
	private long longMin;
	private long longMax;
	private long latMin;
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
		this.latMin = latMin;
		this.latMax = latMax;
		tree = new prQuadtree<Coordinates>(longMin, longMax, latMin, latMax);
	}

	public void writeToDB(String recordFile) {
		try {
			RandomAccessFile record = new RandomAccessFile(recordFile, "r");
			dataWriter = new FileWriter(dataFile);
			record.readLine();
			while (record.getFilePointer() < record.length()) {
				String gis = record.readLine();
				String[] gisRecord = gis.split("[|]");
				if (checkBounds(gisRecord[8], gisRecord[7]) == true) {
					dataWriter.write(gis + "\r\n");
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
			while (dataAccess.getFilePointer() < dataAccess.length()) {
				long filePointerRef = dataAccess.getFilePointer();
				String[] gisRecord = dataAccess.readLine().split("[|]");
				Coordinates gisCoord = new Coordinates(
						convertToLong(gisRecord[8]), convertToLat(gisRecord[7]));
				if (tree.find(gisCoord) != null) {
					tree.find(gisCoord).getList().add(filePointerRef);
				} else {
					tree.insert(gisCoord);
					tree.find(gisCoord).getList().add(filePointerRef);
				}
			}
			dataAccess.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void findCoordinate(String lon, String lat) {
		try {
			RandomAccessFile dataAccess = new RandomAccessFile(dataFile, "r");
			long longitude = convertToLong(lon);
			long lattitude = convertToLat(lat);
			Coordinates gisCoord = new Coordinates(longitude, lattitude);
			Coordinates treeRefCoord = tree.find(gisCoord);
			for (int i = 0; i < treeRefCoord.getList().size(); i++) {
				long filePointer = treeRefCoord.getList().get(i);
				dataAccess.seek(filePointer);
				System.out.println(filePointer);
				System.out.println(dataAccess.readLine());
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
		long lonNum = convertToLong(lon);
		long latNum = convertToLat(lat);
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

}
