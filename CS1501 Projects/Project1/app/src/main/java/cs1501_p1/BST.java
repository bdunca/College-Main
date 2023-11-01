package cs1501_p1;

public class BST<T extends Comparable<T>> implements BST_Inter<T> {
	
	private BTNode<T> root;

	//Add a new key to the BST

	public void put(T key){
		if (key == null){ 
			throw new IllegalArgumentException("put() used with a null key");
		}
        root = put(root, key);
    }

	private BTNode<T> put(BTNode<T> x, T key){
		if (x == null){
			return new BTNode<T>(key);
		} 

		int cmp = key.compareTo(x.getKey());
		if(cmp < 0){ 
			x.setLeft(put(x.getLeft(),key));
		}
		else if (cmp > 0){
			x.setRight(put(x.getRight(),key));
		}

		return x;
	}

	public T get(T key) {
		if(key == null){
			throw new IllegalArgumentException("key is null for get()");
		}
		return get(root,key);
	}

	private T get(BTNode<T> x, T key) {        
        while(x != null){
            int cmp = key.compareTo(x.getKey());
            //check current node for key
            if(cmp == 0){
                return key;
            }
            //iterate down tree looking for key
            else if(cmp < 0){
                x = x.getLeft();
            }
            else if(cmp > 0){
                x = x.getRight();
            }
        }
        return null;
    }

	//Check if the BST contains a key

	public boolean contains(T key){
		if (key == null){ 
			throw new IllegalArgumentException("key is null for contains()");
		}
		return get(key) != null;
	}


	//Remove a key from the BST, if key is present
	
	public void delete(T key){
		if(key == null){
			throw new IllegalArgumentException("key is null");
		}
		if(!contains(key)){
			return;
		}

		BTNode<T> cur = root; 
		BTNode<T> parent = null;

		while (true) {
			if (key.compareTo(cur.getKey()) > 0) {
				parent = cur;
				cur = cur.getRight();
			} else if (key.compareTo(cur.getKey()) < 0) {
				parent = cur;
				cur = cur.getLeft();
			} else if (key.compareTo(cur.getKey()) == 0) {
				break; 
			}
		}

		//if right and left are both null 
		if (cur.getRight() == null && cur.getLeft() == null) {
			if (cur == root) { 
				root = null;
			} 
			else if (parent.getLeft() == cur) { 
				parent.setLeft(null);
			} 
			else if (parent.getRight() == cur) { 
				parent.setRight(null);
			} 
		}

		//if one of left or right is null 
		else if (cur.getRight() == null || cur.getLeft() == null) {
			if (cur == root && cur.getLeft() != null) { 
				root = cur.getLeft();
			} 
			else if (cur == root && cur.getRight() != null) { 
				root = cur.getRight();
			} 

			else if (cur != root && cur.getLeft() != null && parent.getLeft() == cur) { 
				parent.setLeft(cur.getLeft());
			} 
			else if (cur != root && cur.getRight() != null && parent.getLeft() == cur) { 
				parent.setLeft(cur.getRight());
			} 

			else if (cur != root && cur.getLeft() != null && parent.getRight() == cur) { 
				parent.setRight(cur.getLeft());
			} 
			else if (cur != root && cur.getRight() != null && parent.getRight() == cur) { 
				parent.setRight(cur.getRight());
			} 
		}

		//if neither left or right is null 
		else if (cur.getLeft() != null && cur.getRight() != null) {
			BTNode<T> successor = getSuccessor(cur); 
			if (cur == root) {
				root = successor;
			} 
			else if (cur != root && parent.getLeft() == cur) {
				parent.setLeft(successor);
			} 
			else if (cur != root && parent.getRight() == cur) {
				parent.setRight(successor); }
			} 
		}

		//if successive node needed
		private BTNode<T> getSuccessor(BTNode<T> delNode) {
			BTNode<T> successor = delNode; 
			BTNode<T> successorParent = null; 
			BTNode<T> current = delNode.getRight();

			while (current != null) { 
				successorParent = successor; 
				successor = current;
				current = current.getLeft();
			}

			//if successor is not to the right 
			if (successor != delNode.getRight()) {
				successorParent.setLeft(successor.getRight());
				successor.setRight(delNode.getRight()); 
			}
			successor.setLeft(delNode.getLeft());

			return successor;
			}

	
	//Determine the height of the BST
	
	public int height(){
		return height(root);
	}
	
	private int height(BTNode<T> x){
		if(x == null){
			return 0;
		}
		return Math.max(height(x.getLeft()), height(x.getRight())) + 1;
	}

	
	//Determine if the BST is height-balanced
	
	public boolean isBalanced(){
		BTNode<T> cur = root; 
		return isBalanced(cur);
	}

	private boolean isBalanced(BTNode<T> cur) {
		if (cur == null) {
			return true;
		}

		isBalanced(cur.getLeft()); 
		isBalanced(cur.getRight());

		int lh = height(cur.getLeft()); 
		int rh = height(cur.getRight());
		int dif = Math.abs(lh - rh);

		return dif <= 1; 
	}
		

	
	//In-order traversal of the tree and produces a String containing the keys in ascending order, separated by ':'s.
	
	public String inOrderTraversal(){
		if (root == null){
			throw new IllegalArgumentException("root is null");
		}

		BTNode<T> cur = root;
		String res = this.inOrderTraversal(cur);
		return res.substring(0,res.length()-1);		
	}

	private String inOrderTraversal(BTNode<T> cur) {
		if (cur == null){ 
			return "";
		}

		String str = "";
		str += inOrderTraversal(cur.getLeft()); 
		str += cur.getKey() + ":";
		str += inOrderTraversal(cur.getRight()); 

		return str;
		}

	
	//Pre-order traversal of the BST in order to produce a String

	public String serialize(){
		BTNode<T> cur = root; 
		String res = this.serialize(cur); 
		return "R" + res.substring(1);
	}

	private String serialize(BTNode<T> cur) {
		String str = "";
		if (cur == null) {
			return ""; 
		}

		//base case: leaf node, return L(key) 
		if (cur.getLeft() == null && cur.getRight() == null) { 
			return "L" + "(" + (cur.getKey()) + ")";
		}

		//right or left is null
		if (cur.getLeft() == null || cur.getRight() == null) { 
			if (cur.getLeft() == null) { 
				//left is null
				str += "I" + "(" + (cur.getKey()) + ")" + "," + "X(NULL)" + "," + serialize(cur.getRight());
			}
			if (cur.getRight() == null) { 
				//right is null
				str += "I" + "(" + (cur.getKey()) + ")" + "," + serialize(cur.getLeft()) + "," + "X(NULL)"; 
			}
		}

		//neither left or right is null
		if (cur.getLeft() != null && cur.getRight() != null) { 
			str += "I" + "(" + (cur.getKey()) + ")" + "," + serialize(cur.getLeft())+ "," + serialize(cur.getRight()); 
		}

		return str; 
	}
	

	
	//Produce a deep copy of the BST that is reversed 
	
	public BST_Inter<T> reverse(){
		BST<T> revCopy = new BST<T>(); 
		revCopy.root = revCopy(this.root); 

		return revCopy;
	}

	private BTNode<T> revCopy(BTNode<T> oldRoot) {
		if(oldRoot == null) { 
			return null;
		}
		BTNode<T> newNode = new BTNode<T>(oldRoot.getKey());

		newNode.setLeft(revCopy(oldRoot.getRight())); 
		newNode.setRight(revCopy(oldRoot.getLeft()));

		return newNode;
		}
}