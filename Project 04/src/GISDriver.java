public class GISDriver {

	public static void main(String[] args) {
		GISController controller = new GISController("database.txt", "script.txt",
				"log.txt");
		controller.startCmdProcessor();

	}
}
