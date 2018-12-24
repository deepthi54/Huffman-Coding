import java.util.Map;

public class PQTester {

	public PQTester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
//		PriorityQueue314<Integer> pq = new PriorityQueue314<>();
//		
//		pq.enqueue(12);
//		pq.enqueue(17);
//		pq.enqueue(16);
//		pq.enqueue(5);
//		pq.enqueue(12);
//		
//		System.out.println(pq);
//		System.out.println(pq.size());
//		pq.dequeue();
//		System.out.println(pq);
//		System.out.println(pq.size());
		
		String len = "00001000100011110000000010010000010100101001110010000101000100000";
		String randomString = "101";
		System.out.println(Integer.parseInt(randomString, 2));
		
		System.out.println(IHuffConstants.STORE_COUNTS);
		System.out.println();
		System.out.println(IHuffConstants.PSEUDO_EOF);
		
		/*PriorityQueue314<TreeNode> pq = new PriorityQueue314<>();
		TreeNode obj1 = new TreeNode('e', 12);
		TreeNode obj2 = new TreeNode('a', 1);
		TreeNode obj3 = new TreeNode('f', 6);
		TreeNode obj4 = new TreeNode('E', 5);
		TreeNode obj5 = new TreeNode('Z', 5);
		
		pq.enqueue(obj4);

		pq.enqueue(obj5);

		pq.enqueue(obj2);

		pq.enqueue(obj1);

		pq.enqueue(obj3);
		
		System.out.println(pq);

		HuffmanTree ht = new HuffmanTree(pq);
		ht.printTree(); 
		
		System.out.println(ht.makeMap(true));
		System.out.println(ht.getNumInternals());
		System.out.println(ht.getNumLeaves());
		
		System.out.println(ht.preOrder());*/
		
		PriorityQueue314<TreeNode> pq = new PriorityQueue314<>();
		TreeNode obj1 = new TreeNode(' ', 14);
		TreeNode obj2 = new TreeNode('#', 1);
		TreeNode obj3 = new TreeNode('A', 3);
		TreeNode obj4 = new TreeNode('B', 4);
		TreeNode obj5 = new TreeNode('S', 3);
		TreeNode obj6 = new TreeNode(IHuffConstants.PSEUDO_EOF, 1);
		
		pq.enqueue(obj1);
		pq.enqueue(obj2);
		pq.enqueue(obj3);
		pq.enqueue(obj4);
		pq.enqueue(obj5);
		pq.enqueue(obj6);
		
		HuffmanTree ht = new HuffmanTree(pq);
		System.out.println(ht.makeCompressMap());
		ht.printTree();
		System.out.println(ht.preOrder());
		System.out.println("00001000100011110000000010010000010100101001110010000101000100000");
		ht = new HuffmanTree("00001000100011110000000010010000010100101001110010000101000100000");
		//Map<String, Integer>codesMap = ht.makeDecompressMap();
		//System.out.println(codesMap);
		ht.printTree();
		
		System.out.println();
		
		HuffmanTree.TreeTraverser tt = ht.getTreeTraverser();
		int ascii = tt.goLeft();
		ascii = tt.goLeft();
		ascii = tt.goLeft();
		ascii = tt.goRight();
		
		System.out.println(ascii);
		
		
		
		
		

	}

}
