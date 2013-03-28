public class CommandChecker {

	public Command checkCommandIntegrity(String[] command) {
		if (command[0].equals("world") && integrityCheck(command)) {
			if (command.length == 5) {
				return Command.WORLD;
			}
		}

		if (command[0].equals("import")) {
			if (command.length == 2) {
				if ((command[1].substring(command[1].length() - 4,
						command[1].length())).equals(".txt")) {
					return Command.IMPORT;
				}
			}
		}

		if (command[0].equals("what_is_at")) {
			if (command.length == 3) {
				if (integrityCheck(command)) {
					return Command.WHAT_IS_AT;
				}
			}
		}

		return Command.INVALID;

	}

	public boolean integrityCheck(String[] command) {
		try {
			for (int i = 1; i < command.length; i++) {
				convertToLat(command[i]);
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
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

}
