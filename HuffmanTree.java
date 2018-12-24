import java.util.HashMap;
import java.util.Map;

public class HuffmanTree {
	
	private TreeNode root;
	private int numLeafNodes;
	private int numInternalNodes;
	private String treeCode;
	
	private final int LENGTH_OF_LEAF_CODE = IHuffConstants.BITS_PER_WORD + 1;
	
	/*
	 * Constructor that builds the tree from a given Priority Queue 
	 */
	public HuffmanTree(PriorityQueue314<TreeNode> priorityQueue) {
		this.root = buildTree(priorityQueue); 
	}
	
	/*
	 * Constructor that builds a tree using the header data (from a string)
	 */
	public HuffmanTree(String code) {
		treeCode = code;
		root = decodeTree();
	}
	
	/*
	 * Recursive method that rebuilds a tree using STF header information
	 * 1 in header data means leaf, 0 means internal node
	 */
	private TreeNode decodeTree() {
		// Base Case: We're at a leaf
		if (treeCode.charAt(0) == '1') {
			// Read in the next 9 digits (length of leaf encoding) and get its ascii value
			String ascii = treeCode.substring(1, LENGTH_OF_LEAF_CODE + 1);
			int value = Integer.parseInt(ascii, 2);
			// move past the digits we just looked at
			treeCode = treeCode.substring(LENGTH_OF_LEAF_CODE + 1);
			// attach new leaf with the ascii value
			return new TreeNode(value, 0);
		}
		// otherwise, when we're at 0 (internal node), move past the 0
		treeCode = treeCode.substring(1);
		// make the recursive call and set the left node and set the right node
		TreeNode nLeft = decodeTree();
		TreeNode nRight = decodeTree();
		// return a new internal node with the left and right children
		return new TreeNode(nLeft, 0, nRight);
	}
	
	/*
	 * The method that creates the actual Huffman Tree from the Priority Queue  
	 */
	private TreeNode buildTree(PriorityQueue314<TreeNode> pq) {
		// continue while there are 2 or more items left in priority queue
		numLeafNodes = pq.size();
		while (pq.size() > 1) {
			numInternalNodes++;
			
			// the value of the new internal node is the sum of the next two items in the pq
			TreeNode lchild = pq.dequeue();
			TreeNode rchild = pq.dequeue();
			int value = lchild.getFrequency() + rchild.getFrequency();
			// create a new internal node and enqueue it back to the priority queue
			TreeNode newInternalNode = new TreeNode(lchild, value, rchild);
			pq.enqueue(newInternalNode);
		}
		
		TreeNode result = pq.dequeue();
		return result;
	}
	
	public int getNumLeaves() {
		return numLeafNodes;
	}
	
	public int getNumInternals() {
		return numInternalNodes;
	}
	
	public int getTotalNumNodes() {
		return numLeafNodes + numInternalNodes;
	}
	
	public void printTree() {
        printTree(root, "");
    }

	// method (taken from Mike's BST method) for debugging
    private void printTree(TreeNode n, String spaces) {
        if(n != null){
            printTree(n.getRight(), spaces + "   ");
            System.out.println(spaces + (char)n.getValue() + n.getFrequency());
            printTree(n.getLeft(), spaces + "   ");
        }
    }
    
    /*
     * Creates the map for each ascii value and its corresponding code
     */
    public Map<Integer, String> makeCompressMap() {
    	HashMap<Integer, String> result = new HashMap<>();
    	compressMapHelp(root, "", result);
    	return result;
    }
    
    /*
     * helper method that keeps track of the current code and adds the final one to the Map
     */
    private void compressMapHelp(TreeNode n, String currentCode, HashMap<Integer, String> result) {
    	// Base Case: if we've reached the bottom, we have our code! Add it to the map
		if (n.isLeaf()) 
				result.put(n.getValue(), currentCode);
		else {
			// Otherwise, move down left and add a 0, and move down right and add a 1 to the current code
			if (n.getLeft() != null) 
				compressMapHelp(n.getLeft(), currentCode + "0", result);
			if (n.getRight() != null) 
				compressMapHelp(n.getRight(), currentCode + "1", result);
		}
	}
    
	/*
	 * Returns a bitstring representation of the tree using a preorder traversal.
	 * Used for the Standard Tree Format Header
	 */
	public String preOrder() {
		String header = "";
		header = preOrderHelper(root, header);
		return header;
	}
	
	// helper method that recursively traverses the tree in preorder
	private String preOrderHelper(TreeNode n, String currentHeader) {
		final int NUM_DIGITS = IHuffConstants.BITS_PER_WORD + 1;
		// Base case: reached a leaf
		if (n.isLeaf()) {
			currentHeader += "1";
			// pad the number with enough 0s
			String binaryNum = Integer.toBinaryString(n.getValue());
			int originalLen = binaryNum.length();
			// if the binary number doesn't have enough leading 0s, add them to the front
			for (int i = 0; i < NUM_DIGITS - originalLen; i++) 
				binaryNum = "0" + binaryNum;
			currentHeader += binaryNum;
		} else {
			// Preorder: parent node, then left child, then right child
			currentHeader += "0";
			if (n.getLeft() != null)
				currentHeader = preOrderHelper(n.getLeft(), currentHeader);
			if (n.getRight() != null)
				currentHeader = preOrderHelper(n.getRight(), currentHeader);
		}
		return currentHeader;
	}
	
	public TreeTraverser getTreeTraverser() {
		return new TreeTraverser();
	}
	
	/*
	 * Nested class (similar to an iterator) that traverses any given tree
	 */
	public class TreeTraverser {
		//instance variables
		private TreeNode currNode;
		
		public TreeTraverser() {
			reset();
		}
		
		public boolean hasNext() {
			return !currNode.isLeaf();
		}
		
		// move back to the root of the tree
		public void reset() {
			currNode = HuffmanTree.this.root;
		}
		
		// moves to the left child and returns the value in the node passed over
		public int goLeft() {
			int result = -1;
			assert hasNext();
			
			currNode = currNode.getLeft();
			if(currNode.isLeaf())
				result = currNode.getValue();
			return result;
		}
		
		// moves to the right child and returns the value in the node passed over
		public int goRight() {
			int result = -1;
			assert hasNext();
			
			currNode = currNode.getRight();
			if(currNode.isLeaf())
				result = currNode.getValue();
			return result;
		}
	}
}
