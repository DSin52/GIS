public class HashTable {

	private HashEntry[] records;
	private int capacity;
	private int size;
	private int[] sizeArray;
	private int longestProbeSize;

	public HashTable() {
		this.capacity = 3019;
		sizeArray = new int[] { 1019, 2027, 4079, 8123, 16267, 32503, 65011,
				130027, 260111, 520279, 1040387, 2080763, 4161539, 8323151,
				16646323 };
		records = new HashEntry[capacity];
		size = 0;
		longestProbeSize = 0;
	}

	public int size() {
		return this.size;
	}

	public int elfHash(String toHash) {
		int hashValue = 0;
		for (int Pos = 0; Pos < toHash.length(); Pos++) { // use all elements
			hashValue = (hashValue << 4) + toHash.charAt(Pos); // shift/mix
			int hiBits = hashValue & 0xF0000000; // get high nybble
			if (hiBits != 0)
				hashValue ^= hiBits >> 24; // xor high nybble with second nybble
			hashValue &= ~hiBits; // clear high nybble
		}
		return (hashValue % capacity);
	}

	public void put(String key, long offset) {
		if (key != null && !key.equals("")) {
			int hashedKey = elfHash(key);
			int i = 0;
			while (records[hashedKey] != null
					&& !records[hashedKey].getKey().equals(key)) {
				hashedKey = (int) ((hashedKey + quadProb(i)) % capacity);
				i++;
				longestProbeSize++;
				if (hashedKey > capacity) {
					resizeTable();
				}
			}
			records[hashedKey] = new HashEntry(key, offset);
			size++;
			if (size > (0.7 * capacity)) {
				resizeTable();
			}
		}
	}

	public int getLongestProbeSize() {
		return longestProbeSize;
	}

	private int quadProb(int i) {

		int ref = (int) (Math.pow(i, 2) + i) / 2;
		System.out.println(ref);
		return ref;
	}

	public long get(String key) {
		int hashedKey = elfHash(key);
		int i = 1;
		while (records[hashedKey] != null
				&& !records[hashedKey].getKey().equals(key)) {
			hashedKey = (int) ((hashedKey + quadProb(i)) % capacity);
		}
		if (records[hashedKey] == null) {
			return -1;
		} else {
			return records[hashedKey].getValue();
		}

	}

	private void resizeTable() {
		HashEntry[] oldRef = records;
		allocateArray(sizeArray[indexOfTableSize() + 1]);
		for (int i = 0; i < oldRef.length; i++) {
			if (oldRef[i] != null) {
				records[i] = oldRef[i];
			}
		}
	}

	private void allocateArray(int size) {
		this.records = new HashEntry[size];
		capacity = size;
	}

	private int indexOfTableSize() {
		for (int i = 0; i < sizeArray.length; i++) {
			if (records.length == sizeArray[i]) {
				return i;
			}
		}
		return -1;
	}

	public String toString() {

		String tableInfo = "";
		for (int i = 0; i < capacity; i++) {
			if (records[i] != null) {
				tableInfo += i + ":\t" + "[" + records[i].key + ", ["
						+ records[i].value + "]]\r\n";
			}
		}
		return tableInfo;
	}

	private class HashEntry {
		private String key;
		private long value;

		public HashEntry(String key, long value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public long getValue() {
			return value;
		}
	}

}
