package cs1501_p3;
import java.text.StringCharacterIterator;

public class PQDLB {
    
	private PQNode root;
	
	public CarsPQ getPQ(String word){
		if(word == null || root == null) return null;

		StringCharacterIterator itr = new StringCharacterIterator(word);
		PQNode curr = root;

		while(itr.getIndex() < itr.getEndIndex()){ 
			if(curr == null){ 
				return null;
			}

			while(itr.current() != curr.getVal()){ 
				if(curr.getSibling() == null){
					return null; 
				} else{
					curr = curr.getSibling();
				}
			}

			curr = curr.getChildren();
			itr.next();
		}

		if(curr == null){ //no PQ exists
			return null;
		} else if(curr.getVal() == '*'){ 
			return curr.getPriorityQueue();
		}

		while(curr.getSibling() != null){ 
			if(curr.getVal() == '*'){
				return curr.getPriorityQueue();
			} else{
				curr = curr.getSibling();
			}
		}

		return curr.getPriorityQueue();
	}

	//Insert a make/model into the priority queue DLB
	public boolean insert(String word, CarsPQ carsPQ){
		if(word == null){ 
			return false;
		}

		StringCharacterIterator itr = new StringCharacterIterator(word);

		if(root == null){ 
			root = new PQNode(itr.current());

			PQNode curr = root;
			itr.next();

			while(itr.getIndex() < itr.getEndIndex()){ //Loop through the word
				PQNode newPQNode = new PQNode(itr.current());
				curr.setChildren(newPQNode);
				curr = curr.getChildren();

				itr.next();
			}

			curr.setChildren(new PQNode('*')); //Terminate the word in the trie with *
			if(carsPQ != null){ 
				curr = curr.getChildren();
				curr.setPriorityQueue(carsPQ);
			}
		} else{
			PQNode curr = root;

			while(itr.getIndex() < itr.getEndIndex()){ 
				while(itr.current() != curr.getVal()){
					if(curr.getSibling() == null){
						PQNode newPQNode = new PQNode(itr.current());
						curr.setSibling(newPQNode);
						curr = curr.getSibling(); 
						break;
					} else{
						curr = curr.getSibling();
					}
				}

				if(curr.getChildren() == null){ //char doesnt exist, add new Node 
					PQNode newPQNode = new PQNode(itr.current());
					curr.setChildren(newPQNode);
				}

				itr.next();
				curr = curr.getChildren();
			}

			curr.setVal('*'); 
			if(carsPQ != null){ 
				curr.setPriorityQueue(carsPQ);
			}
		}

		return true;
	}

	//boolean if make/model's priority queue exists
	public boolean exists(String word){
		if(word == null || root == null){
			 return false;
		}

		StringCharacterIterator itr = new StringCharacterIterator(word);
		PQNode curr = root;

		while(itr.getIndex() < itr.getEndIndex()){ 
			if(curr == null){ 
				return false;
			}

			while(itr.current() != curr.getVal()){ //Loop through sibling linked list
				if(curr.getSibling() == null){
					return false; //No sibling PQNode matches the curr char
				} else{
					curr = curr.getSibling();
				}
			}

			curr = curr.getChildren();
			itr.next();
		}

		if(curr == null){
			return false;
		} else if(curr.getVal() == '*'){ //Reached the end of the word, so it exists
			return true;
		}

		while(curr.getSibling() != null){ //Loop through sibling linked list
			if(curr.getVal() == '*'){
				return true;
			} else{
				curr = curr.getSibling();
			}
		}
		return false;
	}
}