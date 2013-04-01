public class HashTable {

	private HashEntry[] array;
	private int currentSize;
	private int[] sizeArrayCheat = new int[] { 1019, 2027, 4079, 8123, 16267,
			32503, 65011, 130027, 260111, 520279, 1040387, 2080763, 4161539,
			8323151, 16646323 };
	private int probeSequence;

	public HashTable() {
		this(1019);
	}

	public HashTable(int size) {
		array = new HashEntry[1019];
		makeEmpty();
		probeSequence = 1;
	}

	private void setProbeSequence(int x) {
		this.probeSequence = x;
	}

	public int getProbeSequence() {
		return this.probeSequence;
	}

	public void makeEmpty() {
		currentSize = 0;
		for (int i = 0; i < array.length; i++) {
			array[i] = null;
		}
	}

	public boolean contains(String key) {
		int currentPos = findPos(key);
		if (isActive(currentPos)) {
			return true;
		}
		return false;
	}

	public void insert(String key, long filePointerRef) {
		int currentPos = findPos(key);
		if (isActive(currentPos)) {
			return;
		}

		array[currentPos] = new HashEntry(key, filePointerRef, true);
		if (++currentSize > (.7 * array.length)) {
			rehash();
		}
	}

	/**
	 * Return true if currentPos exists and is active.
	 * 
	 * @param currentPos
	 *            the result of a call to findPos.
	 * @return true if currentPos is active.
	 */
	private boolean isActive(int currentPos) {
		return array[currentPos] != null && array[currentPos].isActive;
	}

	public void remove(String key) {
		int currentPos = findPos(key);
		if (isActive(currentPos)) {
			array[currentPos].isActive = false;
		}
		currentSize--;
	}

	private void allocateArray(int arraySize) {
		int cheat = -1;
		for (int i = 0; i < sizeArrayCheat.length; i++) {
			if (arraySize == sizeArrayCheat[i]) {
				cheat = i;
			}
		}
		array = new HashEntry[sizeArrayCheat[cheat]];
	}

	private int findPos(String key) {
		int offset = 0;
		int hashedKey = elfHash(key);
		// while (array[hashedKey] != null && !array[hashedKey].key.equals(key))
		// {
		// offset += ((((int) (Math.pow(offset, 2)) + offset) / 2) %
		// array.length);
		// hashedKey = (hashedKey + offset);
		// // setProbeSequence(++probeSequence);
		// }
		// return hashedKey;
		int incHash = hashedKey;
		for (int i = 0; i < array.length && array[incHash] != null
				&& !array[incHash].key.equals(key); i++) {
			offset = (int) ((Math.pow(i, 2) + i) / 2) % array.length;
			incHash = hashedKey + offset;
		}
		return incHash;

	}

	private void rehash() {
		HashEntry[] oldArray = array;
		allocateArray(oldArray.length);
		currentSize = 0;

		for (int i = 0; i < oldArray.length; i++) {
			if (oldArray[i] != null && oldArray[i].isActive) {
				insert(oldArray[i].key, oldArray[i].value);
			}
		}

	}

	public long get(String key) {
		int currentPos = findPos(key);
		if (contains(key)) {
			return array[currentPos].value;
		}
		return -1;
	}

	public int elfHash(String toHash) {
		long hashValue = 0;
		for (int Pos = 0; Pos < toHash.length(); Pos++) { // use all elements
			hashValue = (hashValue << 4) + toHash.charAt(Pos); // shift/mix
			long hiBits = hashValue & 0xF0000000L; // get high nybble
			if (hiBits != 0)
				hashValue ^= hiBits >> 24; // xor high nybble with second nybble
			hashValue &= ~hiBits; // clear high nybble
		}
		return (((int) hashValue) % array.length);
	}

	public String toString() {

		String tableInfo = "";
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {
				tableInfo += i + ":\t" + "[" + array[i].key + ", ["
						+ array[i].value + "]]\r\n";
			}
		}
		return tableInfo;
	}

	public int getFilled() {
		return currentSize;
	}

	public int size() {
		return array.length;
	}

	private static class HashEntry {
		public String key; // the element
		long value;
		public boolean isActive; // false if marked deleted

		public HashEntry(String key, long value, boolean isActive) {
			this.key = key;
			this.value = value;
			this.isActive = isActive;
		}

	}

}