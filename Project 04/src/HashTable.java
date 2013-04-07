/**
 * This class represents the hash table functionality. It uses an array for its
 * data. Its probe sequence is quadratic of the form: (n^2 + n) / 2. The
 * resizing of the table is of the prime for 4k + 3 to ensure that no collisions
 * are unaccounted for.
 * 
 * @author Divit Singh divit52
 * 
 */
public class HashTable {
	private HashEntry[] entries;
	private int currentSize;
	private int[] sizeArrayCheat = new int[] { 1019, 2027, 4079, 8123, 16267,
			32503, 65011, 130027, 260111, 520279, 1040387, 2080763, 4161539,
			8323151, 16646323 };
	private int probeSequence;

	/**
	 * Creates table with size of 1019 initially.
	 */
	public HashTable() {
		this(1019);
	}

	/**
	 * Creates table with size specified
	 * 
	 * @param tableSize
	 *            size of table
	 */
	public HashTable(int tableSize) {
		entries = new HashEntry[tableSize];
		currentSize = 0;
		probeSequence = 1;
	}

	/**
	 * Inserts key and the offset from the database file into table.
	 * 
	 * @param key
	 *            key to insert into table
	 * @param filePointerRef
	 *            offset from database file
	 */
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

	/**
	 * Resizes the array to the next form of 4k + 3.
	 * 
	 * @param curSize
	 *            current size of array
	 */
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

	/**
	 * This was given to us. This is the hashing function of the keys.
	 * 
	 * @param toHash
	 *            key to hash
	 * @return hashed key
	 */
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

	/**
	 * Gets all information with the specified key.
	 * 
	 * @param key
	 *            key to search for
	 * @return all values that correspond to given key
	 */
	public long get(String key) {
		int position = findPos(key);
		if (entries[position] == null) {
			System.out.println("NULL");
		}
		System.out.println("POSITION: " + position);
		try {
			return entries[position].getValue();
		} catch (NullPointerException e) {
			return -1;
		}

	}

	/**
	 * Size of table.
	 * 
	 * @return size
	 */
	public int size() {
		return entries.length;
	}

	private int findPos(String key) {
		System.out.println("KEY: " + key);
		int offset = 0;
		int hashedKey = elfHash(key);
		int incHash = (hashedKey % entries.length);
		for (int i = 0; i < entries.length && entries[incHash] != null
				&& !entries[incHash].getKey().equals(key); i++) {
			offset = (int) ((Math.pow(i, 2) + i) / 2) % entries.length;
			incHash = (hashedKey + offset);
			if (incHash >= entries.length) {
				incHash = incHash - entries.length;
			}

			probeSequence = Math.max(probeSequence, i);
		}

		return incHash;

	}

	public int getProbeSequence() {
		return probeSequence;
	}

	public int getFilled() {
		return currentSize;
	}

	/**
	 * Returns information about elements in the hash table.
	 */
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

	/**
	 * Represents the hash entry. Contains the key and offset.
	 */
	private class HashEntry {
		private String key;
		private long offset;

		/**
		 * Constructor for the hash entry.
		 * 
		 * @param key
		 *            key for storage
		 * @param offset
		 *            offset for storage
		 */
		public HashEntry(String key, long offset) {
			this.key = key;
			this.offset = offset;
		}

		/**
		 * Returns the key.
		 * 
		 * @return key
		 */
		public String getKey() {
			return key;
		}

		/**
		 * Returns the value.
		 * 
		 * @return value
		 */
		public long getValue() {
			return offset;
		}

	}
}