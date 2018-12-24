import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TreeMap;


public class Decompressor {
	
	//instance variables
	private BitInputStream bits;
	private BitOutputStream writer;
	private HuffmanTree huffTree;
		
	public Decompressor(InputStream in, OutputStream out) {
		bits = new BitInputStream(in);
		writer = new BitOutputStream(out);
	}

	public int decompress(IHuffViewer myViewer) throws IOException {
		// Step 1: Read in the magic number
		int magic = bits.readBits(IHuffConstants.BITS_PER_INT); 
		if (magic != IHuffConstants.MAGIC_NUMBER) {
		    myViewer.showError("Error reading compressed file. \n" +
		            "File did not start with the huff magic number.");
		    return -1;
		}
		// Step 2: Read in the STORE COUNTS/TREE constant to determine how to build the tree
		int constant = bits.readBits(IHuffConstants.BITS_PER_INT);
		// Step 3: Build the tree from the header using helper methods
		if (constant == IHuffConstants.STORE_COUNTS)
			buildTreeSCF();
		else 
			buildTreeSTF();
		
		// Step 4 & 5: write the real data from the compressed data; return size of file
		int numBits = decode();
		
		bits.close();
		writer.close();
		
		return numBits;
	}
	
	// read 1 bit at a time and walk tree
    private int decode() throws IOException {
    	int bitCount = 0;
    	HuffmanTree.TreeTraverser tt = huffTree.getTreeTraverser();
        // get ready to walk tree, start at root
        boolean done = false;
        while(!done) {
        	// start from the top of the tree
        	tt.reset();
        	int ascii = -1;
        	// read one bit at a time and go left if 0, right if 1
        	while(tt.hasNext()) {
        		int bit = bits.readBits(1);
        		if(bit == -1)
        			throw new IOException("Error reading compressed file. \n" +
                    "unexpected end of input. No PSEUDO_EOF value.");
        		else if(bit == 0) 
        			ascii = tt.goLeft();
        		else 
        			ascii = tt.goRight();
            }
        	// Step 5: we have reached the end/PEOF
        	if(ascii == IHuffConstants.PSEUDO_EOF)
        		done = true;
        	else {
        		// general case, write out the entire integer (in bits) stored in the leaf node
        		writer.writeBits(IHuffConstants.BITS_PER_WORD, ascii);
        		bitCount += IHuffConstants.BITS_PER_WORD;
        	}
        }
        return bitCount;
    }
	
    /*
     * Builds the tree from the Standard Count Format header data
     */
	public void buildTreeSCF() throws IOException {
		// create a frequency map
		TreeMap<Integer, Integer> chunks = new TreeMap<>();
		for (int i = 0; i < IHuffConstants.ALPH_SIZE; i++) {
			int inbits = bits.readBits(IHuffConstants.BITS_PER_INT);
			if (inbits != 0)
				chunks.put(i, inbits);
		}
		// Add in the PEOF value to the map
		chunks.put(IHuffConstants.PSEUDO_EOF, 1);
		// create a priority queue from the map
		PriorityQueue314<TreeNode> pq = new PriorityQueue314<>();
		for (Integer asciiValue : chunks.keySet()) {
			TreeNode newNode = new TreeNode(asciiValue, chunks.get(asciiValue));
			pq.enqueue(newNode);
		}
		// Make new Huffman tree from the priority queue
		huffTree = new HuffmanTree(pq);
	}
	
	/*
	 * Builds the tree from the Standard Tree Format header data
	 */
	public void buildTreeSTF() throws IOException {
		//read in the size of the tree
		int treeSize = bits.readBits(IHuffConstants.BITS_PER_INT);
		String STFCode = readSTF(treeSize);
		huffTree = new HuffmanTree(STFCode);
	}
	
	// helper method that returns the bits read in as a string
	public String readSTF(int treeSize) throws IOException {
		String code = "";
		for(int i = 0; i < treeSize; i++) {
			code += bits.readBits(1);
		}
		return code;
	}
}
