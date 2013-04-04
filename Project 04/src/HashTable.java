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
		++currentSize;
		if (currentSize >= (.7 * entries.length)) {
			rehash();
		}
		entries[currentPos] = new HashEntry(key, filePointerRef);

		System.out.println("Current Size: " + currentSize + ": "
				+ entries.length);

	}

	public void allocateSize(int curSize) {

		for (int i = 0; i < sizeArrayCheat.length - 1; i++) {
			if (curSize == sizeArrayCheat[i]) {
				this.entries = new HashEntry[sizeArrayCheat[i + 1]];
			}
		}
		for (int i = 0; i < entries.length; i++) {
			entries[i] = null;
		}
	}

	public void rehash() {
		HashEntry[] oldRef = entries;
		allocateSize(oldRef.length);
		currentSize = 0;
		for (int i = 0; i < oldRef.length; i++) {
			if (oldRef[i] != null) {
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
		return (((int) hashValue) % entries.length);
	}

	public long get(String key) {
		int position = findPos(key);
		if (entries[position] == null) {
			System.out.println("NULL");
		}
		System.out.println(key);
		return entries[position].getValue();
	}

	public int size() {
		return entries.length;
	}

	private int findPos(String key) {
		System.out.println("KEY: " + key);
		int offset = 0;
		int hashedKey = elfHash(key);
		int incHash = (hashedKey % entries.length);
		int newProbe = 0;
		System.out.println("test: " + hashedKey + " " + incHash);
		for (int i = 0; i < entries.length && entries[incHash] != null
				&& !entries[incHash].getKey().equals(key); i++) {
			offset = (int) ((Math.pow(i, 2) + i) / 2) % entries.length;
			incHash = (hashedKey + offset);
			newProbe++;
			if (incHash >= entries.length) {
				incHash = incHash - entries.length;
			}

		}

		if (newProbe > probeSequence) {
			probeSequence = newProbe;
		}

		return incHash;

	}

	public int getProbeSequence() {
		return probeSequence;
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

		public HashEntry(String key, long offset) {
			this.key = key;
			this.offset = offset;
		}

		public String getKey() {
			return key;
		}

		public long getValue() {
			return offset;
		}

	}
}