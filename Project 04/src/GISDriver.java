/**
 * Driver of the entire application. Reads from the command line arguments and
 * passes it off onto the objects.
 * 
 * @author Divit Singh divit52
 * 
 */
public class GISDriver {

	/**
	 * Main method to run the program
	 * 
	 * @param args
	 *            arg0 = database file name arg1 = cmd file name arg2 = log file
	 *            name
	 */
	public static void main(String[] args) {
		GISController controller = new GISController(args[0], args[1], args[2]);
		controller.startCmdProcessor();

	}
}
