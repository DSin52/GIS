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
			} else if (command.length == 4) {
				if (command[1].equals("-l") && isLat(command[2])
						&& isLong(command[3])) {
					return Command.WHAT_IS_AT_L;
				} else if (command[1].equals("-c") && isLat(command[2])
						&& isLong(command[3])) {
					return Command.WHAT_IS_AT_C;
				}
			}
		}

		if (command[0].equals("what_is_in")) {
			if (isLat(command[1]) && isLong(command[2]) && isNumber(command[3])
					&& isNumber(command[4])) {
				return Command.WHAT_IS_IN;
			} else if (command[1].equals("-l") && isLong(command[2])
					&& isLong(command[3]) && isNumber(command[4])
					&& isNumber(command[5])) {
				return Command.WHAT_IS_IN_L;
			} else if (command[1].equals("-c") && isLong(command[2])
					&& isLong(command[3]) && isNumber(command[4])
					&& isNumber(command[5])) {
				return Command.WHAT_IS_IN_C;
			}
		}

		if (command[0].equals("what_is")) {
			if (command.length == 3) {
				return Command.WHAT_IS;
			}
			if (command.length == 4) {
				if (command[1].equals("-l")) {
					return Command.WHAT_IS_L;
				}
			}
		}

		if (command[0].equals("debug")) {
			if (command[1].equals("quad")) {
				return Command.DEBUG_QUAD;
			}
			if (command[1].equals("pool")) {
				return Command.DEBUG_POOL;
			}
			if (command[1].equals("hash")) {
				return Command.DEBUG_HASH;
			}
		}

		if (command[0].equals("quit")) {
			return Command.QUIT;
		}

		return Command.INVALID;

	}

	public boolean isNumber(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
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

	public boolean isLong(String command) {
		try {
			convertToLong(command);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public boolean isLat(String command) {
		try {
			convertToLat(command);
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

}
