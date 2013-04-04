public class BufferPool {

	BufferNode headRef;
	private static final int POOL_SIZE = 20;
	int poolCount;

	public BufferPool() {
		headRef = null;
		poolCount = 0;
	}

	public void insertAtHead(String data) {
		removeDuplicate(data);
		System.out.println(data);
		poolCount++;
		headRef = new BufferNode(data, headRef);

		if (poolCount == 21) {
			deleteFromTail();
		}
	}

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
			System.out.println("FSLDJF");
			if (temp.next.data.equals(data)) {
				temp.next = temp.next.next;
				poolCount--;
			}
			temp = temp.next;
		}

	}

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

	public String toString() {
		String temp = "";
		BufferNode tempNode = headRef;
		while (tempNode != null) {
			temp += tempNode.data + "\r\n";
			tempNode = tempNode.next;

		}
		return temp;
	}

	private class BufferNode {
		String data;
		BufferNode next;

		public BufferNode(String data) {
			this(data, null);
		}

		public BufferNode(String data, BufferNode next) {
			this.data = data;
			this.next = next;
		}

		public String getData() {
			return data;
		}
	}
}
