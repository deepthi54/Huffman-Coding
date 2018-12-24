import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

public class Compressor {
	
	//instance variables
	private BitInputStream bits;
	private BitOutputStream writer;
	private int format;
    private HuffmanTree huffTree;
    private Map<Integer, Integer> chunks;
    private Map<Integer, String> codesMap;
    private int originalSize;
    private int compressedSize;
        
	public Compressor(InputStream in, int headerFormat) {
		bits = new BitInputStream(in);
		format = headerFormat;
	}
	
	public int preprocess() throws IOException {
        int inbits;
        chunks = new TreeMap<>();
        
        // While there are more bits to be read
        while((inbits = bits.readBits(IHuffConstants.BITS_PER_WORD)) != -1) {
        	originalSize += IHuffConstants.BITS_PER_WORD;
        	// If it is already in the map, increase the frequency
        	if(chunks.containsKey(inbits)) 
        		chunks.put(inbits, chunks.get(inbits) + 1);
        	// Otherwise create new key
        	else 
        		chunks.put(inbits, 1);
        }
        // Create new Priority Queue of TreeNodes from the map
        PriorityQueue314<TreeNode> pq = new PriorityQueue314<>();
        for(Integer i: chunks.keySet()) {
        	TreeNode newNode = new TreeNode(i, chunks.get(i));
        	pq.enqueue(newNode);
        }
        
        // Adding Pseudo EOF Value to our map and tree
        chunks.put(IHuffConstants.PSEUDO_EOF, 1);
        TreeNode newNode = new TreeNode(IHuffConstants.PSEUDO_EOF, 1);
        pq.enqueue(newNode);
        
        huffTree = new HuffmanTree(pq);
        codesMap = huffTree.makeCompressMap(); // we want ascii values as keys
        
        findCompressedSize();
        bits.close();
        
        return originalSize - compressedSize;
    }
	
	/*
	 * Updates the instance variable for the size of the new compressed file to use in the return value
	 * in preprocess and compress
	 */
	private void findCompressedSize() {
		compressedSize += IHuffConstants.BITS_PER_INT; // MAGIC NUMBER
    	compressedSize += IHuffConstants.BITS_PER_INT; // STORE COUNTS or TREE constant
        if (format == IHuffConstants.STORE_COUNTS) {
        	// frequencies for every possible ASCII character
        	compressedSize += IHuffConstants.ALPH_SIZE * IHuffConstants.BITS_PER_INT;
        } else {
        	// tree size
        	compressedSize += IHuffConstants.BITS_PER_INT; 
        	// length of tree encoding
        	int sizeOfTree = (IHuffConstants.BITS_PER_WORD + 1) * huffTree.getNumLeaves() + huffTree.getTotalNumNodes();
        	compressedSize += sizeOfTree;
        }
        for (Integer key : chunks.keySet()) {
        	// multiply the frequency of each number times the length of its code
        	String binaryString = codesMap.get(key);
        	compressedSize += chunks.get(key) * binaryString.length();
        }
	}
	
	public int compress(InputStream in, OutputStream out) throws IOException {
		bits = new BitInputStream(in);
		writer = new BitOutputStream(out);
		// Step 1: Write out the magic number 
		writer.writeBits(IHuffConstants.BITS_PER_INT, IHuffConstants.MAGIC_NUMBER);
		if (format == IHuffConstants.STORE_COUNTS) {
			// Step 2: Write out the STORE COUNTS constant
			writer.writeBits(IHuffConstants.BITS_PER_INT, IHuffConstants.STORE_COUNTS);
			// Step 3: Write out the frequencies
			for (int i = 0; i < IHuffConstants.ALPH_SIZE; i++)  {
				if (chunks.containsKey(i))
					writer.writeBits(IHuffConstants.BITS_PER_INT, chunks.get(i));
				else 
					writer.writeBits(IHuffConstants.BITS_PER_INT, 0);
			}
		} else {
			// Step 2: Write out the STORE TREE constant
			writer.writeBits(IHuffConstants.BITS_PER_INT, IHuffConstants.STORE_TREE);
			// Step 3: Write out the header data to rebuild the tree
			int sizeOfTree = (IHuffConstants.BITS_PER_WORD + 1) * huffTree.getNumLeaves() + huffTree.getTotalNumNodes();
			writer.writeBits(IHuffConstants.BITS_PER_INT, sizeOfTree);
			String storeTree = huffTree.preOrder();
			writeBinaryBits(storeTree);
		}
		int inbits;
		// Step 4: Write out the actual compressed data
		while((inbits = bits.readBits(IHuffConstants.BITS_PER_WORD)) != -1) {
			String encoding = codesMap.get(inbits);
			writeBinaryBits(encoding);
		}
		// Step 5: Write out PEOF coding 
		String peofCode = codesMap.get(IHuffConstants.PSEUDO_EOF);
		writeBinaryBits(peofCode);
		writer.flush();
		bits.close();
		writer.close();
		return compressedSize;
	}
	
	/*
	 * Method that writes out each bit in a bitstring one at a time
	 */
	private void writeBinaryBits(String encoding) {
		for (int i = 0; i < encoding.length(); i++) {
			if (encoding.charAt(i) == '0')
				writer.writeBits(1, 0);
			else
				writer.writeBits(1, 1);
		}
	}
	
	public int getCompressedSize() {
		return compressedSize;
	}
	
	public int getOriginalSize() {
		return originalSize;
	}
}
