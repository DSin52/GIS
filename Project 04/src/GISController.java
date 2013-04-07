import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This class is the controller for the entire program. It contains all the
 * necessary initializations and collection of information to pass down
 * accordingly.
 */
public class GISController {

	FileWriter logWriter;
	CommandScriptProcessor cmdProc;
	RandomAccessFile cmdScript;
	String logFile;
	String dataBaseFile;
	String cmdScriptFile;

	/**
	 * Constructor of controller that initializes all important aspects of the
	 * program.
	 * 
	 * @param dataBaseFile
	 *            name of database file to write to
	 * @param cmdScriptFile
	 *            name of cmd file to read from
	 * @param logFile
	 *            name of log file to log results to
	 */
	public GISController(String dataBaseFile, String cmdScriptFile,
			String logFile) {
		this.dataBaseFile = dataBaseFile;
		this.cmdScriptFile = cmdScriptFile;
		this.logFile = logFile;
	}

	/**
	 * Simply initializes the log writer.
	 */
	public void initFileWriters() {
		try {
			logWriter = new FileWriter(logFile, false);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Starts processing the command. Checks to see if all the arguments are
	 * declared correctly then passes it off to the processor for further
	 * operations.
	 */
	public void startCmdProcessor() {
		initFileWriters();
		try {
			if (!(dataBaseFile.substring(dataBaseFile.length() - 4,
					dataBaseFile.length())).equals(".txt")) {
				logWriter.write("Incorrent database file name");
				logWriter.close();
				System.exit(1);
			}
			try {
				File file = new File(dataBaseFile);
				if (file.exists()) {
					file.delete();
				}
				cmdScript = new RandomAccessFile(cmdScriptFile, "r");

				cmdProc = new CommandScriptProcessor(dataBaseFile,
						cmdScriptFile, logFile, cmdScript, logWriter);

				cmdProc.execCommands();

				cmdScript.close();

			} catch (FileNotFoundException e) {
				logWriter.write("File Not Found.");
				System.exit(1);
			}

			logWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
