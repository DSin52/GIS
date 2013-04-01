public class GISDriver {

	public static void main(String[] args) {
		GISController controller = new GISController(args[0], args[1], args[2]);
		controller.startCmdProcessor();

	}
}
