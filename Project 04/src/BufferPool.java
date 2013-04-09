/**
 * This class represents the buffer pool of nodes that are being used. It has a
 * max size of 20.
 */

public class BufferPool {

	BufferNode headRef;
	private static final int POOL_SIZE = 20;
	int poolCount;

	/**
	 * Sets headRef to null and number of items in pool to 0.
	 */
	public BufferPool() {
		headRef = null;
		poolCount = 0;
	}

	/**
	 * Inserts an element to the front of the linked list.
	 * 
	 * @param data
	 *            String to insert
	 */
	public void insertAtHead(String data) {
		removeDuplicate(data);
		poolCount++;
		headRef = new BufferNode(data, headRef);

		if (poolCount == 21) {
			deleteFromTail();
		}
	}

	/**
	 * Removes duplicates from the list.
	 * 
	 * @param data
	 *            String that is being check for a a duplicate
	 */
	public void removeDuplicate(String data) {
		if (headRef == null) {
			return;
		}
		BufferNode temp = headRef;
		if (temp.data.equals(data)) {
			headRef = headRef.next;
			return;
		}
		while (temp != null && temp.next != null) {
			if (temp.next.data.equals(data)) {
				temp.next = temp.next.next;
				poolCount--;
			}
			temp = temp.next;
		}

	}

	/**
	 * Deletes the last element in the linked list.
	 */
	public void deleteFromTail() {
		BufferNode temp = headRef;
		if (temp.next == null) {
			temp = null;
		}
		while (temp.next != null && temp.next.next != null) {
			temp = temp.next;
		}
		temp.next = null;
		poolCount--;
	}

	/**
	 * Returns information about the elements in the list in sequential order.
	 */
	public String toString() {
		String temp = "";
		BufferNode tempNode = headRef;
		while (tempNode != null) {
			temp += tempNode.data + "\r\n";
			tempNode = tempNode.next;

		}
		return temp;
	}

	/**
	 * Inner class to represent the nodes in the linked list.
	 */
	private class BufferNode {
		String data;
		BufferNode next;

		/**
		 * Node constructor that needs a data value. Sets next to null.
		 * 
		 * @param data
		 *            data to insert into node.
		 */
		public BufferNode(String data) {
			this(data, null);
		}

		/**
		 * Constructor that sets nodes value and the next value as well.
		 * 
		 * @param data
		 *            Data value of node
		 * @param next
		 *            next node in list
		 */
		public BufferNode(String data, BufferNode next) {
			this.data = data;
			this.next = next;
		}

		/**
		 * Returns data value of node.
		 * 
		 * @return value of node
		 */
		public String getData() {
			return data;
		}
	}
}
