/*
 * Divit Singh
 * CS 3114
 * Project 03 PR QuadTree
 */
import java.util.Vector;

//
/**
 * @author divit52 Divit Singh
 * 
 *         This is a PRQuadTree. It allows someone to place a point on the
 *         region and is coded so that only one point may exist is its own
 *         region. An example would be if you insert 1 point in a rectangle,
 *         nothing would happen except simply putting a point. If another point
 *         was to go on it, depending on its location, this tree structure would
 *         keep dividing the region until each point is in its own region.
 * 
 *         This is part of a project that was assigned in my Data Structures
 *         class.
 * 
 *         Enjoy reading through my Code!
 * 
 * @param <T>
 *            Any type of object that implements T or has a parent class that
 *            implements it
 */
public class prQuadtree<T extends Compare2D<? super T>> {

	/**
	 * These are the global parameters. The root is the root of the quadtree.
	 * The xMin-yMax's represent the max range of the region being used. The
	 * prQuadTreeSize is to keep track of how many elements are in the tree.
	 */
	prQuadNode root;
	long xMin, xMax, yMin, yMax;
	long prQuadTreeSize;

	/**
	 * 
	 * Class to keep a prQuadNode object.
	 */
	abstract class prQuadNode {
	}

	/**
	 * Node Class that represents the leaf nodes of the PrQuadTree. It contains
	 * elements inside of it.
	 * 
	 */
	class prQuadLeaf extends prQuadNode {
		Vector<T> Elements;
		// private static final int BUCKET_SIZE = 1;
		private int bucketSize;

		/**
		 * PrQuadLeaf Contructor that specifies Vector of elements.
		 * 
		 * @param elem
		 *            Elemtn to insert into the vector.
		 */
		public prQuadLeaf(T elem, int bucketSize) {
			this.bucketSize = bucketSize;
			Elements = new Vector<T>();
			Elements.add(elem);
		}

		/**
		 * Adds item to vector.
		 * 
		 * @param elem
		 *            element to add
		 * @return true if added, false otherwise
		 */
		public boolean addItem(T elem) {
			if (Elements.size() < bucketSize) {
				Elements.add(elem);
				return true;
			}
			return false;
		}

		/**
		 * Returns bucket size.
		 * 
		 * @return bucket size of quadnode
		 */
		public int getBucketSize() {
			return bucketSize;
		}

		/**
		 * Removes specified item from the vector.
		 * 
		 * @param elem
		 *            Element to remove
		 * @return true if removed, false otherwise
		 */
		public boolean removeItem(T elem) {
			int indexToRemove = Elements.indexOf(elem);
			if (indexToRemove == -1) {
				return false;
			} else {
				Elements.remove(indexToRemove);
				return true;
			}
		}

		/**
		 * Returns size of vector
		 * 
		 * @return size
		 */
		public int quadSize() {
			return Elements.size();
		}

		/**
		 * Gets specified element from the vector.
		 * 
		 * @param index
		 *            index of element to retrieved
		 * @return element at specified index
		 */
		public T getElement(int index) {
			return Elements.get(index);
		}

	}

	/**
	 * Class that that represents internal nodes. Has 4 subsections that
	 * represents the quadrants of the region.
	 * 
	 */
	class prQuadInternal extends prQuadNode {
		prQuadNode NW, NE, SE, SW;
	}

	/**
	 * Initializes an empty quadTree with the specified region.
	 * 
	 * @param xMin
	 *            Minimum Horizontal of region
	 * @param xMax
	 *            Maximum Horizontal of region
	 * @param yMin
	 *            Minimum Vertical of region
	 * @param yMax
	 *            Maximum Vertical of region
	 */
	public prQuadtree(long xMin, long xMax, long yMin, long yMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		root = null;
		prQuadTreeSize = 0;
	}

	/**
	 * Inserts element into the Quad Tree.
	 * 
	 * @param elem
	 *            Element to insert into quadtree.
	 * @return true if insertion is succesfull, false otherwise
	 */
	public boolean insert(T elem) {
		if (elem == null || !elem.inBox(xMin, xMax, yMin, yMax)) {
			return false;
		} else {
			long beforeSize = getQuadTreeSize();
			root = insert(root, elem, xMin, xMax, yMin, yMax);
			if (beforeSize == getQuadTreeSize()) {
				return false;
			}
			return true;
		}
	}

	/**
	 * Inserts recursively the root into is specified bounds region. If root is
	 * null, it makes a new leaf node with the element inside of it. Otherwise
	 * it creates an internal node and recursively inserts a leaf node into the
	 * first empty region. It finds the empty region by reducing the size of the
	 * region being searched. When it finds an empty region, it inserts a leaf
	 * node into it.
	 * 
	 * @param root
	 *            root of the tree being used
	 * @param elem
	 *            element that is trying to be inserted
	 * @param xMin
	 *            Horizontal minimum of region being searched
	 * @param xMax
	 *            Horizontal maximum of region being searched
	 * @param yMin
	 *            Vertical minimum of region being searched
	 * @param yMax
	 *            Vertical maximum of region being searched
	 * @return Node that has references to the the entire resulting tree
	 *         including the inserted element;
	 */
	private prQuadNode insert(prQuadNode root, T elem, long xMin, long xMax,
			long yMin, long yMax) {

		// If root is null, just create a new leaf node with element in it.
		if (root == null) {
			root = new prQuadLeaf(elem, 1);
			prQuadTreeSize++;
			return root;
		} else if (isLeaf(root)) {

			prQuadLeaf rootRef = (prQuadLeaf) root;
			// checks to see if the element ins't already in the tree
			for (int i = 0; i < rootRef.quadSize(); i++)
				if (rootRef.getElement(i).equals(elem)) {
					return root;
				}
			// otherwise if the root is a leaf, check to see if bucket is full,
			// if it is not, add it to the bucket.
			if (rootRef.quadSize() < rootRef.getBucketSize()) {

				rootRef.Elements.add(elem);
				return rootRef;

			} else {

				// otherwise create an internal node and
				// recursively insert the element using the internal node as the
				// root
				prQuadNode internalNode = new prQuadInternal();
				// you want to first recursively put in the current leaf nodes
				// element into the tree
				for (int i = 0; i < rootRef.getBucketSize(); i++) {
					internalNode = insert(internalNode, rootRef.getElement(i),
							xMin, xMax, yMin, yMax);
				}

				// then you want to recursively put the element to insert into
				// the
				// tree
				internalNode = insert(internalNode, elem, xMin, xMax, yMin,
						yMax);
				return internalNode;
			}
		}
		// Get reference of the internal node because it is always going to be
		// an internal node in this case
		prQuadInternal internalNode = (prQuadInternal) root;

		// Get the relative direction where you want the element to be inserted
		// by searching for what quadrant it is going to go into.
		Direction insertDir = elem.inQuadrant(xMin, xMax, yMin, yMax);
		switch (insertDir) {
		// Insert into into proper bounds by doing simple arithmetic
		case NE:
			internalNode.NE = insert(internalNode.NE, elem,
					((xMin + xMax) / 2), xMax, ((yMin + yMax) / 2), yMax);
			break;
		case NW:
			internalNode.NW = insert(internalNode.NW, elem, xMin,
					((xMin + xMax) / 2), ((yMin + yMax) / 2), yMax);
			break;
		case SE:
			internalNode.SE = insert(internalNode.SE, elem,
					((xMin + xMax) / 2), xMax, yMin, ((yMin + yMax) / 2));
			break;
		case SW:
			internalNode.SW = insert(internalNode.SW, elem, xMin,
					((xMin + xMax) / 2), yMin, ((yMin + yMax) / 2));
			break;
		default:
			break;
		}
		// return the internal node that has all of its references either
		// pointing to null
		// or containing pointers to other nodes
		return internalNode;

	}

	/**
	 * Tells if a specific node is an internal node.
	 * 
	 * @param root
	 *            root to test if internal
	 * @return true if internal, false otherwise
	 */
	public boolean isInternal(prQuadNode root) {
		return (root.getClass().getName().equals("prQuadtree$prQuadInternal"));
	}

	/**
	 * Tells if a specific node is a leaf node.
	 * 
	 * @param root
	 *            root to test if leaf
	 * @return true if leaf, false otherwise
	 */
	public boolean isLeaf(prQuadNode root) {
		return (root.getClass().getName().equals("prQuadtree$prQuadLeaf"));
	}

	/**
	 * Returns number of leaf nodes in the tree.
	 * 
	 * @return number of leaf nodes in the tree.
	 */
	public long getQuadTreeSize() {
		return prQuadTreeSize;
	}

	/**
	 * Deletes specified element from the tree.
	 * 
	 * @param Elem
	 *            Element to be deleted
	 * @return true if deleted from the tree, false otherwise
	 */
	public boolean delete(T Elem) {
		if (find(Elem) == null) {
			return false;
		}
		root = delete(root, Elem, xMin, xMax, yMin, yMax);
		return true;
	}

	/**
	 * Goes through the tree and deletes the node that contains the element
	 * specified.
	 * 
	 * @param root
	 *            root of the tree being searched
	 * @param Elem
	 *            Element to delete from the tree
	 * @param xMin
	 *            Horizontal minimum of region being searched
	 * @param xMax
	 *            Horizontal maximum of region being searched
	 * @param yMin
	 *            Vertical minimum of region being searched
	 * @param yMax
	 *            Vertical maximum of region being searched
	 * @return node that may have deleted the node if found, unchanged otherwise
	 */
	private prQuadNode delete(prQuadNode root, T Elem, long xMin, long xMax,
			long yMin, long yMax) {
		// if root is null then do nothing
		if (root == null) {
			return null;
		}
		// if it is a leaf node and contains the element
		// delete the element from the tree and decrement size
		// return null so that it detaches it from the parent
		if (isLeaf(root)) {
			prQuadLeaf rootRef = (prQuadLeaf) root;
			if (rootRef.quadSize() == 1 && rootRef.getElement(0).equals(Elem)) {
				prQuadTreeSize--;
				return null;
			} else if (rootRef.quadSize() > 1) {
				for (int i = 0; i < rootRef.quadSize(); i++) {
					if (rootRef.getElement(i).equals(Elem)) {
						rootRef.Elements.remove(i);
						return root;
					}
				}
				return root;
			} else {

				return root;
			}
		}
		// if it is an internal node, find the direction where the element to
		// delete would lie in if it exists
		prQuadInternal internalNode = (prQuadInternal) root;

		Direction deleteDir = Elem.inQuadrant(xMin, xMax, yMin, yMax);
		// then recursvely go to that region so that it may delete it using the
		// base case
		// specified up top
		switch (deleteDir) {
		case NE:
			internalNode.NE = delete(internalNode.NE, Elem,
					((xMin + xMax) / 2), xMax, ((yMin + yMax) / 2), yMax);

			break;
		case NW:
			internalNode.NW = delete(internalNode.NW, Elem, xMin,
					((xMin + xMax) / 2), ((yMin + yMax) / 2), yMax);

			break;
		case SE:
			internalNode.SE = delete(internalNode.SE, Elem,
					((xMin + xMax) / 2), xMax, yMin, ((yMin + yMax) / 2));

			break;
		case SW:
			internalNode.SW = delete(internalNode.SW, Elem, xMin,
					((xMin + xMax) / 2), yMin, ((yMin + yMax) / 2));

			break;
		default:
			break;
		}

		// this is what does the magic afterwards...
		// it pretty much cleans up the null referenced internal nodes
		// if there is only one non null element, and it is a leaf
		// then make the internal node the leaf node and get rid of pointless
		// references
		if (getLeafLeaves(internalNode) <= 1
				&& getInternalLeaves(internalNode) == 0) {
			if (internalNode.NE != null) {
				return internalNode.NE;
			} else if (internalNode.NW != null) {
				return internalNode.NW;
			} else if (internalNode.SE != null) {
				return internalNode.SE;
			} else if (internalNode.SW != null) {
				return internalNode.SW;
			} else {
				;
			}
		}
		// returned the (possibly) modified internal node
		return internalNode;

	}

	/**
	 * Returns number of leaf nodes that are within the tree.
	 * 
	 * @param root
	 *            root of the tree to start counting from
	 * @return number of leaf nodes in the tree
	 */
	public int getLeafLeaves(prQuadNode root) {
		int count = 0;

		prQuadInternal internalNode = (prQuadInternal) root;

		if (internalNode.NE != null && isLeaf(internalNode.NE)) {
			count++;
		}
		if (internalNode.NW != null && isLeaf(internalNode.NW)) {
			count++;

		}
		if (internalNode.SE != null && isLeaf(internalNode.SE)) {
			count++;

		}
		if (internalNode.SW != null && isLeaf(internalNode.SW)) {
			count++;

		}

		return count;

	}

	/**
	 * Returns number of internal nodes that are within the tree.
	 * 
	 * @param root
	 *            root of the tree to start counting from
	 * @return number of internal nodes in the tree
	 */
	public int getInternalLeaves(prQuadNode root) {
		int count = 0;

		prQuadInternal internalNode = (prQuadInternal) root;

		if (internalNode.NE != null && isInternal(internalNode.NE)) {
			count++;
		}
		if (internalNode.NW != null && isInternal(internalNode.NW)) {
			count++;
		}
		if (internalNode.SE != null && isInternal(internalNode.SE)) {
			count++;
		}
		if (internalNode.SW != null && isInternal(internalNode.SW)) {
			count++;
		}

		return count;
	}

	/**
	 * Tries to find the specified element in the tree.
	 * 
	 * @param Elem
	 *            element that is being searched
	 * @return Element being searched, or null if not found
	 */
	public T find(T Elem) {
		if (find(Elem, root, xMin, xMax, yMin, yMax) == null) {
			return null;
		}
		int index = ((prQuadLeaf) find(Elem, root, xMin, xMax, yMin, yMax)).Elements
				.indexOf(Elem);

		return ((prQuadLeaf) find(Elem, root, xMin, xMax, yMin, yMax)).Elements
				.get(index);
	}

	/**
	 * Goes through the tree, trying to find the element that is being searched.
	 * 
	 * @param Elem
	 *            Element that is being searched for
	 * @param root
	 *            Root of the tree that is being searched
	 * @param xMin
	 *            Horizontal minimum of the region that is being searched
	 * @param xMax
	 *            Horizontal maximum of the region that is being searched
	 * @param yMin
	 *            Vertical minimum of the region that is being searched
	 * @param yMax
	 *            Vertical maximum of the region that is being searched
	 * @return node containing the element if found, false otherwise
	 */
	private prQuadNode find(T Elem, prQuadNode root, long xMin, long xMax,
			long yMin, long yMax) {
		// If it is a leaf node, then check to see if it has the element that is
		// being searched
		if (root == null) {
			return null;
		}
		if (isLeaf(root)) {
			prQuadLeaf rootRef = (prQuadLeaf) root;
			for (int i = 0; i < rootRef.quadSize(); i++) {

				if (rootRef.Elements.get(i).equals(Elem)) {
					return rootRef;
				}
			}
			return null;
			// otherwise if it is an internal node, find what region element
			// lies in
			// and recursively search that region until either the node that has
			// the element is found or null if not found
		} else {
			prQuadInternal rootInternal = (prQuadInternal) root;
			Direction findDir = Elem.inQuadrant(xMin, xMax, yMin, yMax);

			switch (findDir) {
			case NE:
				return find(Elem, rootInternal.NE, (xMin + xMax) / 2, xMax,
						(yMin + yMax) / 2, yMax);
			case NW:
				return find(Elem, rootInternal.NW, xMin, (xMin + xMax) / 2,
						(yMin + yMax) / 2, yMax);
			case SE:
				return find(Elem, rootInternal.SE, (xMin + xMax) / 2, xMax,
						yMin, (yMin + yMax) / 2);
			case SW:
				return find(Elem, rootInternal.SW, xMin, (xMin + xMax) / 2,
						yMin, (yMin + yMax) / 2);
			default:
				return null;
			}
		}
	}

	/**
	 * Finds all the elements that are within the specified region, and gives
	 * them in a vector.
	 * 
	 * @param xLo
	 *            Horizontal minimum of region being searched
	 * @param xHi
	 *            Horizontal maximum of region being searched
	 * @param yLo
	 *            Vertical minimum of region being searched
	 * @param yHi
	 *            Vertical maximum of region being searched
	 * @return Vector containing all of the nodes that are within a specified
	 *         region
	 */
	public Vector<T> find(long xLo, long xHi, long yLo, long yHi) {
		Vector<T> pointVec = new Vector<T>();
		findHelper(root, xLo, xHi, yLo, yHi, pointVec);
		return pointVec;
	}

	/**
	 * Goes through region searching for all nodes that lie within a certain
	 * region.
	 * 
	 * @param root
	 *            Root of tree being searched
	 * @param xLo
	 *            Horizontal minimum of region being searched
	 * @param xHi
	 *            Horizontal maximum of region being searched
	 * @param yLo
	 *            Vertical minimum of region being searched
	 * @param yHi
	 *            Vertical maximum of region being searched
	 * @param pointVec
	 *            Vector to put all elements that are in the region into
	 */
	private void findHelper(prQuadNode root, long xLo, long xHi, long yLo,
			long yHi, Vector<T> pointVec) {
		if (root == null) {
			return;
		} else if (isLeaf(root)) {
			// if its a leaf node, then see if the data value is within a
			// certain region
			// if it is, add it to the vector
			prQuadLeaf rootRef = (prQuadLeaf) root;
			for (int i = 0; i < rootRef.quadSize(); i++) {
				if (rootRef.getElement(i).inBox(xLo, xHi, yLo, yHi)) {
					pointVec.add(rootRef.getElement(i));
				}
			}
			return;
		} else {
			// if its an internal node, then look its datapoints inside the
			// internal node to see
			// if they contain a point tha tlies within the specified region
			prQuadInternal internalRoot = (prQuadInternal) root;
			findHelper(internalRoot.NE, xLo, xHi, yLo, yHi, pointVec);
			findHelper(internalRoot.NW, xLo, xHi, yLo, yHi, pointVec);
			findHelper(internalRoot.SE, xLo, xHi, yLo, yHi, pointVec);
			findHelper(internalRoot.SW, xLo, xHi, yLo, yHi, pointVec);
		}

	}
}
