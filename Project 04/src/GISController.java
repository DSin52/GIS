import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class GISController {

	FileWriter logWriter;
	CommandScriptProcessor cmdProc;
	RandomAccessFile cmdScript;
	String logFile;
	String dataBaseFile;
	String cmdScriptFile;

	public GISController(String dataBaseFile, String cmdScriptFile,
			String logFile) {
		this.dataBaseFile = dataBaseFile;
		this.cmdScriptFile = cmdScriptFile;
		this.logFile = logFile;
	}

	public void initFileWriters() {
		try {
			logWriter = new FileWriter(logFile, false);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
