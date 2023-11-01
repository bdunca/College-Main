package cs1501_p3;

public class PQNode {
    private PQNode children;
	private PQNode sibling;
	private char value;
	private CarsPQ priorityQueue;

	public PQNode(char val){
		value = val;
	}

	public char getVal(){
		return value;
	}

	public PQNode getChildren(){
		return children;
	}

	public PQNode getSibling(){
		return sibling;
	}

	public CarsPQ getPriorityQueue(){
		return priorityQueue;
	}

	public void setVal(char val){
		value = val;
	}

	public void setChildren(PQNode nextReference){
		children = nextReference;
	}

	public void setSibling(PQNode nextReference){
		sibling = nextReference;
	}

	public void setPriorityQueue(CarsPQ newPQ){
		priorityQueue = newPQ;
	} 
}
