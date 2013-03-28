import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandScriptProcessor {
	String dataBaseFile;
	String cmdScriptFile;
	FileWriter logWriter;
	RandomAccessFile cmdScript;
	ScriptFileProcessor scriptProc;
	CommandChecker checker;
	String seperator = "---------------------------"
			+ "---------------------------------" + "--------------------";

	public CommandScriptProcessor(String dataBaseFile, String cmdScriptFile,
			RandomAccessFile cmdScript, FileWriter logWriter) {
		this.dataBaseFile = dataBaseFile;
		this.cmdScriptFile = cmdScriptFile;
		this.cmdScript = cmdScript;
		this.logWriter = logWriter;
		checker = new CommandChecker();
	}

	public void execCommands() {
		int cmdCounter = 1;
		try {

			while (cmdScript.getFilePointer() < cmdScript.length()) {
				String commentCheck = cmdScript.readLine();
				String[] command = commentCheck.split("\t");
				if (commentCheck.substring(0, 1).equals(";")) {
					logWriter.write(commentCheck + "\r\n");
				} else {
					switch (checker.checkCommandIntegrity(command)) {

					default:
						logWriter.write("Error in script file command");
						break;

					case WORLD:
						logWriter.write(commentCheck + "\r\n\r\n");
						logWriter.write("GIS Program \r\n\r\n");
						logWriter.write("dbFile: \t" + dataBaseFile + "\r\n");
						logWriter.write("script: \t" + cmdScriptFile + "\r\n");
						SimpleDateFormat sd = new SimpleDateFormat(
								"E MMM dd HH:mm:ss zzz yyyy");
						Date date = new Date();
						logWriter.write("Start Time: \t" + sd.format(date)
								+ "\r\n");
						logWriter
								.write("Quadtree children are printed in the order SW SE NE NW \r\n"
										+ seperator + "\r\n\r\n");
						scriptProc = new ScriptFileProcessor(dataBaseFile,
								logWriter, checker.convertToLong(command[1]),
								checker.convertToLong(command[2]),
								checker.convertToLat(command[3]),
								checker.convertToLat(command[4]));
						logWriter
								.write("Lattitude/longitude values in index entries are shown as signed integers, in total seconds.\r\n\r\n");
						logWriter.write("World boundaries are set to:\r\n");
						logWriter.write("\t\t\t" + command[4] + "\r\n");
						logWriter.write("\t" + command[1] + "\t\t\t"
								+ command[2] + "\r\n");
						logWriter.write("\t\t\t" + command[3] + "\r\n"
								+ seperator + "\r\n");
						break;

					case IMPORT:
						logWriter.write("Command " + cmdCounter++ + ":\t"
								+ command[0] + "\t" + command[1] + "\r\n\r\n");
						scriptProc.writeToDB(command[1]);
						logWriter
								.write("Imported Features by name:\t"
										+ scriptProc.getImportedFilesNum()
										+ "\r\nLongest probe sequence:\t\t0\r\nImported Locations:\t\t"
										+ scriptProc.getImportedFilesNum()
										+ "\r\n" + seperator + "\r\n");
						break;

					case WHAT_IS_AT:
						logWriter.write("Command " + cmdCounter++ + "\t"
								+ command[0] + "\t" + command[1] + "\t"
								+ command[2] + "\r\n\r\n");

						scriptProc.findCoordinate(command[2], command[1]);
						break;
					}
				}
			}
		} catch (IOException e) {
			// e.printStackTrace();
			try {
				logWriter.write("Error in Reading Command Script File");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}
}
