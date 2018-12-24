/*  Student information for assignment:
 *
 *  On our honor, Deepthi Pittala and Caitlin O'Callaghan, this programming assignment is our own work
 *  and we have not provided this code to any other student.
 *
 *  Number of slip days used: 1
 *
 *  Student 1
 *  UTEID: cmo2227
 *  email address: caitlinocallag@gmail.com
 *  Grader name: Smruti
 *
 *  Student 2
 *  UTEID: dp29968
 *  email address: pittala.deepthi@gmail.com
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SimpleHuffProcessor implements IHuffProcessor {
	
	//instance variables
    private IHuffViewer myViewer;
    private Compressor cp;

    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
    	int difference = cp.getCompressedSize() - cp.getOriginalSize();
    	if (difference > 0 && force == false) {
    		myViewer.showError("Compressed file has " + difference + " more bits than uncompressed file."
    				+ "Select 'force compression' option to compress.");
    		return -1;
    	}
    	int num = cp.compress(in, out);
        showString("" + num);
        return num;
    }

    public int preprocessCompress(InputStream in, int headerFormat) throws IOException {
        cp = new Compressor(in, headerFormat);
        int num = cp.preprocess();
        showString("" + num);
        return num;
    }

    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    public int uncompress(InputStream in, OutputStream out) throws IOException {
        Decompressor dcp = new Decompressor(in, out);
        int num = dcp.decompress(myViewer);
        showString("" + num);

        return num;
    }

    private void showString(String s){
        if(myViewer != null)
            myViewer.update(s);
    }
}
