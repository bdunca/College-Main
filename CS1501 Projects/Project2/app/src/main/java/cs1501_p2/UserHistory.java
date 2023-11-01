package cs1501_p2;
import java.util.*;

public class UserHistory implements Dict{
	private Node root;
	private String currPrefix;
	private int count;
	private PriorityQueue<Integer> pq = new PriorityQueue<Integer>(Collections.reverseOrder()); //reverse comparator so poll gives max
	private HashMap<String, Integer> hashmap = new HashMap<String, Integer>(); //maps 

	public UserHistory(){ 
		root = new Node(); 
		count = 0;
		currPrefix = "";
	}

	public String getPrefix(){
		return currPrefix; 
	}

    /**
	 * Add a new word to the dictionary
	 *
	 * @param 	key New word to be added to the dictionary
	 */	
	public void add(String key){
        if(contains(key)){
			root = recAdd(root, key, 0);
			return;
        }
		root = recAdd(root, key, 0);
		count += 1;
    }

	private Node recAdd(Node curr, String key, int i){
		if(curr == null){
			curr = new Node();
		}
		if(i == key.length()){
			curr.value += 1;
			return curr;
		}

		curr.next[(int) key.charAt(i)] = recAdd(curr.next[(int) key.charAt(i)], key, i+1);
		return curr;
	}

	/**
	 * Check if the dictionary contains a word
	 *
	 * @param	key	Word to search the dictionary for
	 *
	 * @return	true if key is in the dictionary, false otherwise
	 */
	public boolean contains(String key){
		Node res = recContains(root, key, 0);

		if(res == null || res.value == 0){
			return false;
		}
		return true;
	}

	private Node recContains(Node curr, String key, int i){
		if (curr == null){ 
			return null;
		}
		if (i == key.length()) {
			if (curr.value > 0) {
				return curr; 
			}
			else{ 
				return null; 
			}
		}
		return recContains(curr.next[(int) key.charAt(i)], key, i+1); 
	}

	/**
	 * Check if a String is a valid prefix to a word in the dictionary
	 *
	 * @param	pre	Prefix to search the dictionary for
	 *
	 * @return	true if prefix is valid, false otherwise
	 */
	public boolean containsPrefix(String pre){
		Node curr = root;

		for(int i = 0; i < pre.length(); i++){
			if(curr.next[(int) pre.charAt(i)] == null){
				return false;
			}
			curr = curr.next[(int) pre.charAt(i)];
		}
		return true;
	}

	/**
	 * Search for a word one character at a time
	 *
	 * @param	next Next character to search for
	 *
	 * @return	int value indicating result for current by-character search:
	 *				-1: not a valid word or prefix
	 *				 0: valid prefix, but not a valid word
	 *				 1: valid word, but not a valid prefix to any other words
	 *				 2: both valid word and a valid prefix to other words
	 */
	public int searchByChar(char next){
		Node curr = root;
		currPrefix += next;
		boolean validPrefix = false;
		boolean validWord = false;

		for(int i = 0; i < currPrefix.length(); i++){
			curr = curr.next[(int) currPrefix.charAt(i)];

			if(curr == null){
				return -1;
			}
			if(i == currPrefix.length() - 1){
				if(curr.value > 0){
					validWord = true;
				}
				else{
					validPrefix = true;
				}

				for(int j = 0; j < 256; j++){
					if(curr.next[j] != null){
						validPrefix = true;
					}
				}
			}
		}
		if(validPrefix && !validWord){
			return 0;
		}
		else if(!validPrefix && validWord){
			return 1;
		}
		else if(validPrefix && validWord){
			return 2;
		}
		else{
			return -1;
		}
	}

	/**
	 * Reset the state of the current by-character search
	 */
	public void resetByChar(){
		currPrefix = "";
		hashmap = new HashMap<String, Integer>();
		pq = new PriorityQueue<Integer>(Collections.reverseOrder());
	}

	/**
	 * Suggest up to 5 words from the dictionary based on the current
	 * by-character search. Ordering should depend on the implementation.
	 * 
	 * @return	ArrayList<String> List of up to 5 words that are prefixed by
	 *			the current by-character search
	 */
	
	public ArrayList<String> suggest(){
		Node curr = root;
		ArrayList<String> suggestions = new ArrayList<String>();

		for (int i = 0; i < currPrefix.length(); i++) {
			curr = curr.next[(int) currPrefix.charAt(i)]; 
			if (curr == null){ 
				return suggestions;
			}
			if (i == currPrefix.length() - 1) {
				recSuggest(curr, currPrefix); 
			}
		}

		String[] pqList = new String[5]; 
		int n = 0;
		while (pq.size() != 0){
			if (n == 5){ 
				break;
			}
			int max = pq.peek();
			for (String word : hashmap.keySet()) {
				if (n == 5){ 
					break;
				}
				if (hashmap.get(word) == max) {
					pqList[n] = word; 
					n += 1; 
					pq.poll();
				} 
			}
		}

		ArrayList<String> returnList = new ArrayList<String>();
		for (String word : pqList) {
			if (word != null){ 
				returnList.add(word); 
			}
		}
		return returnList;
	}

	private void recSuggest(Node curr, String word){
		if (curr == null){ 
			return;
		}
		if (curr.value > 0) {
			pq.add(curr.value);
			hashmap.put(word, curr.value); 
		}
		for (int i = 0; i < 256; i++) {
			recSuggest(curr.next[i], word + (char) i); 
		}
	}

	/**
	 * List all of the words currently stored in the dictionary
	 * @return	ArrayList<String> List of all valid words in the dictionary
	 */
	public ArrayList<String> traverse(){
		ArrayList<String> allCurrWords = new ArrayList<String>(); 

		allCurrWords = recTraverse(root, allCurrWords, ""); //look through words already used
		Collections.sort(allCurrWords); //sort in ascending order by use
		return allCurrWords; //return sorted 5 top suggestions
	}

	private ArrayList<String> recTraverse(Node curr, ArrayList<String> allCurrWords, String result){
		if (curr == null){ 
			return allCurrWords;
		} 
		if (curr.value > 0){
			for (int i = 0; i < curr.value; i++) {
				allCurrWords.add(result); 
			}
		}
		for (int i = 0; i < 256; i++) {
			recTraverse(curr.next[i], allCurrWords, result + (char)i); 
		}
		return allCurrWords; 
	}
		
	/**
	 * Count the number of words in the dictionary
	 *
	 * @return	int, the number of (distinct) words in the dictionary
	 */
	public int count(){
		return count;
	}

	class Node{
		public int value;
		public Node[] next = new Node[256];
		public Node(){
		}
	}

}