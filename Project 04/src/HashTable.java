public class HashTable {
	private HashEntry[] entries;
	private int currentSize;
	private int[] sizeArrayCheat = new int[] { 1019, 2027, 4079, 8123, 16267,
			32503, 65011, 130027, 260111, 520279, 1040387, 2080763, 4161539,
			8323151, 16646323 };
	private int probeSequence;

	public HashTable() {
		this(1019);
	}

	public HashTable(int tableSize) {
		entries = new HashEntry[tableSize];
		currentSize = 0;
		probeSequence = 1;
	}

	public void insert(String key, long filePointerRef) {
		int currentPos = findPos(key);
		if (++currentSize >= (.7 * entries.length)) {
			rehash();
		}
		System.out.println("Current Position: " + currentPos);
		entries[currentPos] = new HashEntry(key, filePointerRef, true);

	}

	public void allocateSize(int curSize) {

		for (int i = 0; i < sizeArrayCheat.length - 1; i++) {
			if (curSize == sizeArrayCheat[i]) {
				entries = new HashEntry[sizeArrayCheat[i + 1]];
			}
		}
	}

	public void rehash() {
		HashEntry[] oldRef = entries;
		allocateSize(entries.length);
		for (int i = 0; i < oldRef.length; i++) {
			if (oldRef[i] != null && !oldRef[i].isActive) {
				insert(oldRef[i].getKey(), oldRef[i].getValue());
			}
		}
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
		return (((int) hashValue));
	}

	public long get(String key) {
		return -1;
	}

	public int size() {
		return entries.length;
	}

	private int findPos(String key) {
		int offset = 0;
		int incHash = 0;

		for (int i = 0; i < entries.length && entries[incHash] != null
				&& !entries[incHash].key.equals(key); i++) {
			offset = (int) (((Math.pow(i, 2) + i) / 2));
			incHash = (elfHash(key) + offset) % entries.length;
		}
		return incHash;

	}

	public int getProbeSequence() {
		return -1;
	}

	public int getFilled() {
		return currentSize;
	}

	public String toString() {

		String tableInfo = "";
		for (int i = 0; i < entries.length; i++) {
			if (entries[i] != null) {
				tableInfo += i + ":\t" + "[" + entries[i].getKey() + ", ["
						+ entries[i].getValue() + "]]\r\n";
			}
		}
		return tableInfo;
	}

	private class HashEntry {
		private String key;
		private long offset;
		boolean isActive;

		public HashEntry(String key, long offset, boolean isActive) {
			this.key = key;
			this.offset = offset;
			this.isActive = isActive;
		}

		public String getKey() {
			return key;
		}

		public long getValue() {
			return offset;
		}

	}
}