import java.util.ArrayList;


public class PriorityQueue314<E extends Comparable<? super E>> {

	//instance variables
	private ArrayList<E> con;
	
	public PriorityQueue314() {
		con = new ArrayList<>();
	}
	
	public boolean enqueue(E value) {
		if(value == null) 
			throw new IllegalArgumentException("Cannot enqueue null value");
		//if the queue is empty add to the end
		if(con.size() == 0)
			con.add(value);
		//otherwise check where to put value
		else {
			int i = 0;
			boolean notAdded = true;
			//keep going until we've added or passed the end of our ArrayList
			while(i < con.size() && notAdded) {
				//if value is less than the element at i, add it 
				if(value.compareTo(con.get(i)) < 0) {
					con.add(i,  value);
					notAdded = false;
				}
				i++;
			}
			//if we still havent added, add to the end
			if(notAdded) 
				con.add(value);
		}
		return true;
	}
	
	public E dequeue() {
		//if the ArrayList is empty, you cannot dequeue
		if(con.size() == 0)
			throw new IllegalStateException("Cannot dequeue on empty queue");
		//get return value
		E returnVal = con.get(0);
		//remove it 
		con.remove(0);
		return returnVal;
	}
	
	public boolean isEmpty() {
		if(con.size() == 0)
			return true;
		return false;
	}
	
	public int size() {
		return con.size();
	}
	
}
