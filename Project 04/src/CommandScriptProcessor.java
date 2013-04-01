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
	String log;
	CommandChecker checker;
	String seperator = "---------------------------"
			+ "---------------------------------" + "--------------------";

	public CommandScriptProcessor(String dataBaseFile, String cmdScriptFile,
			String log, RandomAccessFile cmdScript, FileWriter logWriter) {
		this.dataBaseFile = dataBaseFile;
		this.cmdScriptFile = cmdScriptFile;
		this.cmdScript = cmdScript;
		this.log = log;
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
						logWriter.write("Error in script file command\r\n");
						break;

					case WORLD:
						logWriter.write(commentCheck + "\r\n\r\n");
						logWriter.write("GIS Program \r\n\r\n");
						logWriter.write("dbFile: \t" + dataBaseFile + "\r\n");
						logWriter.write("script: \t" + cmdScriptFile + "\r\n");
						logWriter.write("log: \t\t" + log + "\r\n");
						SimpleDateFormat sd = new SimpleDateFormat(
								"E MMM dd HH:mm:ss zzz yyyy");
						Date date = new Date();
						logWriter.write("Start Time: \t" + sd.format(date)
								+ "\r\n");
						logWriter
								.write("Quadtree children are printed in the order SW SE NE NW \r\n"
										+ seperator + "\r\n\r\n");
						scriptProc = new ScriptFileProcessor(dataBaseFile,
								logWriter,
								checker.convertToSecondsLong(command[1]),
								checker.convertToSecondsLong(command[2]),
								checker.convertToSecondsLat(command[3]),
								checker.convertToSecondsLat(command[4]));
						logWriter
								.write("Lattitude/longitude values in index entries are shown as signed integers, in total seconds.\r\n\r\n");
						logWriter.write("World boundaries are set to:\r\n");
						logWriter.write("\t\t\t"
								+ scriptProc.convertToSecondsLat(command[4])
								+ "\r\n");
						logWriter.write("\t"
								+ scriptProc.convertToSecondsLong(command[1])
								+ "\t\t\t"
								+ scriptProc.convertToSecondsLong(command[2])
								+ "\r\n");
						logWriter.write("\t\t\t"
								+ scriptProc.convertToSecondsLat(command[3])
								+ "\r\n" + seperator + "\r\n");
						break;

					case IMPORT:
						logWriter.write("Command " + cmdCounter++ + ":\t"
								+ command[0] + "\t" + command[1] + "\r\n\r\n");
						scriptProc.writeToDB(command[1]);
						logWriter.write("Imported Features by name:\t"
								+ scriptProc.getImportedFilesNum()
								+ "\r\nLongest probe sequence:\t\t"
								+ scriptProc.hashTable.getProbeSequence()
								+ "\r\nImported Locations:\t\t"
								+ scriptProc.hashTable.getFilled() + "\r\n"
								+ seperator + "\r\n");
						break;

					case WHAT_IS_AT:
						logWriter.write("Command " + cmdCounter++ + ":" + "\t"
								+ command[0] + "\t" + command[1] + "\t"
								+ command[2] + "\r\n\r\n");
						scriptProc.whatIsAt(command[2], command[1]);
						logWriter.write(seperator + "\r\n");
						break;
					case WHAT_IS_AT_C:
						logWriter.write("Command " + cmdCounter++ + ":" + "\t"
								+ command[0] + "\t" + command[1] + "\t"
								+ command[2] + "\t" + command[3] + "\r\n\r\n");
						scriptProc.whatIsAtC(command[3], command[2]);
						logWriter.write(seperator + "\r\n");
						break;
					case WHAT_IS_AT_L:
						logWriter.write("Command " + cmdCounter++ + ":" + "\t"
								+ command[0] + "\t" + command[1] + "\t"
								+ command[2] + "\t" + command[3] + "\r\n\r\n");
						scriptProc.whatIsAtL(command[3], command[2]);
						logWriter.write(seperator + "\r\n");
						break;
					case WHAT_IS_IN:
						logWriter.write("Command " + cmdCounter++ + "\t"
								+ command[0] + "\t" + command[1] + "\t"
								+ command[2] + "\t" + command[3] + "\t"
								+ command[4] + "\r\n\r\n");
						scriptProc.whatIsInFinder(command[1], command[2],
								command[3], command[4]);
						logWriter.write(seperator + "\r\n");
						break;
					case WHAT_IS_IN_L:
						logWriter.write("Command " + cmdCounter++ + "\t"
								+ command[0] + "\t" + command[1] + "\t"
								+ command[2] + "\t" + command[3] + "\t"
								+ command[4] + "\t" + command[5] + "\r\n\r\n");
						scriptProc.whatIsInLFinder(command[2], command[3],
								command[4], command[5]);
						logWriter.write(seperator + "\r\n");
						break;
					case WHAT_IS_IN_C:
						logWriter.write("Command " + cmdCounter++ + "\t"
								+ command[0] + "\t" + command[1] + "\t"
								+ command[2] + "\t" + command[3] + "\t"
								+ command[4] + "\t" + command[5] + "\r\n\r\n");

						scriptProc.whatIsInCFinder(command[2], command[3],
								command[4], command[5]);
						logWriter.write(seperator + "\r\n");
						break;
					case WHAT_IS:
						logWriter.write("Command " + cmdCounter++ + "\t"
								+ command[0] + "\t" + command[1] + "\t"
								+ command[2] + "\r\n\r\n");

						scriptProc.whatIsFinder(command[1], command[2]);
						logWriter.write(seperator + "\r\n");
						break;
					case WHAT_IS_L:
						logWriter.write("Command " + cmdCounter++ + "\t"
								+ command[0] + "\t" + command[1] + "\t"
								+ command[2] + "\t" + command[3] + "\r\n\r\n");
						scriptProc.whatIsLFinder(command[2], command[3]);
						logWriter.write(seperator + "\r\n");
						break;
					case DEBUG_QUAD:
						logWriter.write("Command " + cmdCounter++ + ":\t"
								+ command[0] + "\t" + command[1] + "\r\n\r\n");
						scriptProc.debugQuad();
						logWriter.write(seperator + "\r\n");
						break;
					case DEBUG_HASH:
						logWriter.write("Command " + cmdCounter++ + ":\t"
								+ command[0] + "\t" + command[1] + "\r\n\r\n");
						logWriter
								.write("Format of display is \r\nSlot number: data record \r\nCurrent table size is: "
										+ scriptProc.hashTable.size()
										+ "\r\nNumber of Elements in table is "
										+ scriptProc.hashTable.getFilled()
										+ "\r\n\r\n");

						scriptProc.debugHash();
						logWriter.write(seperator + "\r\n");

						break;
					case DEBUG_POOL:
						logWriter.write("Command " + cmdCounter++ + ":\t"
								+ command[0] + "\t" + command[1] + "\r\n\r\n");

						logWriter.write(seperator + "\r\n");

						break;
					case QUIT:
						logWriter.write("quitting..");
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
