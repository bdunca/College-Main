package cs1501_p3;
import java.text.StringCharacterIterator;

public class vinDLB{
	private CarNode root;

	//Insert a car's VIN into the DLB
	public boolean insert(Car car){
		String word = car.getVIN();
		if(word == null) return false;

		CarNode inDLB = existsButNotSet(word);
		if(inDLB != null){ 
			inDLB.setCar(car);
			return true;
		}

		StringCharacterIterator itr = new StringCharacterIterator(word);

		if(root == null){ //If nothing exists
			root = new CarNode(itr.current());

			CarNode curr = root;
			itr.next();

			while(itr.getIndex() < itr.getEndIndex()){ //Loop through the VIN
				CarNode newCarNode = new CarNode(itr.current());
				curr.setChildren(newCarNode);
				curr = curr.getChildren();

				itr.next();
			}

			curr.setChildren(new CarNode('*')); //Terminate the VIN in the trie with *
			if(car != null){ //If a PQ is inserted, travel to the current CarNode's children with * and insert the PQ at that CarNode
				curr = curr.getChildren();
				curr.setCar(car);
			}
		} else{
			CarNode curr = root;

			while(itr.getIndex() < itr.getEndIndex()){ 
				while(itr.current() != curr.getVal()){
					if(curr.getSibling() == null){
						CarNode newCarNode = new CarNode(itr.current());
						curr.setSibling(newCarNode);
						curr = curr.getSibling(); //Traverse to newly created CarNode
						break;
					} else{
						curr = curr.getSibling();
					}
				}

				if(curr.getChildren() == null){ //Add a new CarNode because the character we're looking for doesn't exist
					CarNode newCarNode = new CarNode(itr.current());
					curr.setChildren(newCarNode);
				}

				itr.next();
				curr = curr.getChildren();
			}

			curr.setVal('*'); 
			if(car != null){ 
				curr.setCar(car);
			}
		}

		return true;
	}

	//Return a boolean indicating whether a specific VIN already exists in the trie
	public boolean exists(String word){
		if(word == null || root == null) return false;

		StringCharacterIterator itr = new StringCharacterIterator(word);
		CarNode curr = root;

		while(itr.getIndex() < itr.getEndIndex()){ 
			if(curr == null){ //If the CarNode we're at is null, then VIN does not exist
				return false;
			}

			while(itr.current() != curr.getVal()){ //Loop through sibling linked list
				if(curr.getSibling() == null){
					return false; //No sibling CarNode matches the current character
				} else{
					curr = curr.getSibling();
				}
			}

			curr = curr.getChildren();
			itr.next();
		}

		if(curr == null){ 
			return false;
		} else if(curr.getVal() == '*'){
			if(curr.getCar() != null) return true;
			else return false;
		}

		while(curr.getSibling() != null){
			if(curr.getVal() == '*'){
				if(curr.getCar() != null){
					 return true;
				}
				else{ 
					return false;
				}
			} else{
				curr = curr.getSibling();
			}
		}

		return false;
	}

	//find if exists but no *
	private CarNode existsButNotSet(String word){
		if(word == null || root == null) return null;

		StringCharacterIterator itr = new StringCharacterIterator(word);
		CarNode curr = root;

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

		if(curr == null){ 
			return null;
		} else if(curr.getVal() == '*'){ 
			if(curr.getCar() == null) return curr;
		}

		while(curr.getSibling() != null){ 
			if(curr.getVal() == '*'){
				if(curr.getCar() == null) return curr;
			} else{
				curr = curr.getSibling();
			}
		}

		return null;
	}

	//Find the Car with a given VIN
	public Car getCar(String word){
		if(word == null || root == null) return null;

		StringCharacterIterator itr = new StringCharacterIterator(word);
		CarNode curr = root;

		while(itr.getIndex() < itr.getEndIndex()){ 
			if(curr == null){ 
				return null;
			}

			while(itr.current() != curr.getVal()){ 
				if(curr.getSibling() == null){
					return null; //No sibling CarNode matches the current character
				} else{
					curr = curr.getSibling();
				}
			}

			curr = curr.getChildren();
			itr.next();
		}

		if(curr == null){ //No CarNode exists to terminate the VIN, so no Car exists
			return null;
		} else if(curr.getVal() == '*'){ //Reached the end of the VIN, so a Car exists
			return curr.getCar();
		}

		while(curr.getSibling() != null){ 
			if(curr.getVal() == '*'){
				return curr.getCar();
			} else{
				curr = curr.getSibling();
			}
		}

		return curr.getCar();
	}

	//Remove a Car with given vin by setting * to null
	public boolean remove(String word){
		if(word == null || root == null) return false;

		StringCharacterIterator itr = new StringCharacterIterator(word);
		CarNode curr = root;

		while(itr.getIndex() < itr.getEndIndex()){ 
			if(curr == null){ 
				return false;
			}

			while(itr.current() != curr.getVal()){ 
				if(curr.getSibling() == null){
					return false; 
				} else{
					curr = curr.getSibling();
				}
			}

			curr = curr.getChildren();
			itr.next();
		}

		if(curr == null){ 
			return false;
		} else if(curr.getVal() == '*'){ 
			if(curr.getCar() != null){
				curr.setCar(null);
				return true;
			}
		}

		while(curr.getSibling() != null){ 
			if(curr.getVal() == '*'){
				if(curr.getCar() != null){
					curr.setCar(null);
					return true;
				}
			} else{
				curr = curr.getSibling();
			}
		}

		return false;
	}
}