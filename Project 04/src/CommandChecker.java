/**
 * This class is created to check for error in script files.
 */
public class CommandChecker {

	/**
	 * Goes through all the possible commands and verifies if they are of the
	 * correct form.
	 * 
	 * @param command
	 *            Command to error check
	 * @return command, if error free or null
	 */
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

	/**
	 * Checks to see if the String passed in is in-fact a number.
	 * 
	 * @param s
	 *            String that represents a number
	 * @return true if number, false otherwise
	 */
	public boolean isNumber(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if the command specified conforms to command line specifications.
	 * 
	 * @param command
	 *            command to check all command arguments
	 * @return
	 */
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

	/**
	 * Returns if a certain string can be converted into longitude.
	 * 
	 * @param command
	 *            string that represents longitude
	 * @return true if number, false otherwise
	 */
	public boolean isLong(String command) {
		try {
			convertToLong(command);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Returns if a certain string can be converted into lattitude.
	 * 
	 * @param command
	 *            string that represents lattitude
	 * @return true if number, false otherwise
	 */
	public boolean isLat(String command) {
		try {
			convertToLat(command);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Returns a latitude parsed value of a string.
	 * 
	 * @param lat
	 *            String to convert to latitude
	 * @return latitude integer
	 */
	public Long convertToLat(String lat) {
		long conversion = Long.parseLong(lat.substring(0, lat.length() - 1));
		if (lat.substring(lat.length() - 1, lat.length()).equals("S")) {
			return -(conversion);
		}
		return (conversion);
	}

	/**
	 * Returns a longitude parsed value of a string.
	 * 
	 * @param lon
	 *            String to convert to longitude
	 * @return longitude integer
	 */
	public Long convertToLong(String lon) {
		long conversion = Long.parseLong(lon.substring(0, lon.length() - 1));
		if (lon.substring(lon.length() - 1, lon.length()).equals("W")) {
			return -(conversion);
		}
		return (conversion);
	}

	/**
	 * Converts string value into Seconds
	 * 
	 * @param value
	 *            string to convert into longitude seconds
	 * @return amount of seconds corresponding to value
	 */
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

	/**
	 * Converts string value into Seconds
	 * 
	 * @param value
	 *            string to convert into latitude seconds
	 * @return amount of seconds corresponding to value
	 */
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
