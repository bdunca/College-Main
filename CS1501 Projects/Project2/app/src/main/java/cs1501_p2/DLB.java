package cs1501_p2;

import java.util.ArrayList;

public class DLB implements Dict{    

    private DLBNode root;
    private int count;
    private String currPrefix;


    public DLB(){ 
        root = null;
        count = 0;
        currPrefix = "";
    }

    //private getter/setters
    private DLBNode getRightDLBNode(char let, DLBNode curr) {
        while (curr != null) {
            if (curr.getLet() == let) {
                break; 
            }
            curr = curr.getRight(); 
        }
        return curr; 
    }

    private DLBNode setRightDLBNode(char let, DLBNode curr){
        if(curr.getLet() == let){
            return curr.getDown();
        }

        if(curr.getRight() == null){
            DLBNode temp = new DLBNode(let);
            curr.setRight(temp);
            return curr.getRight();
        }
        else{
            while(curr.getRight() != null){
                if(curr.getLet() == let){
                    break;
                }
                curr = curr.getRight();
            }
            if(curr.getLet() == let){
                return curr.getDown();
            }
            else{
                DLBNode temp = new DLBNode(let);
                curr.setRight(temp);
                return curr.getRight();
            }
        }
    }

    private DLBNode setDownDLBNode(char let, DLBNode curr){
        if(curr.getLet() == '^'){  //if end of valid key
            return setRightDLBNode(let, curr);
        }
        else if(curr.getDown() == null){
            DLBNode temp = new DLBNode(let);
            curr.setDown(temp);
            return curr.getDown();
        }
        else{
            return setRightDLBNode(let, curr);
        }
    }


    /**
     * Add a new word to the dictionary
     *
     * @param   key New word to be added to the dictionary
     */ 
    public void add(String key){
        if(contains(key)){
            return;
        }

        key += '^'; //add endChar to valid key
        DLBNode curr = root;
        if(root == null){
            root = new DLBNode(key.charAt(0));
            curr = root;

            for(int i = 1; i < key.length(); i++){
                DLBNode newDown = setDownDLBNode(key.charAt(i), curr);
                curr = newDown;
            }
            count += 1;
            return;
        }
        for(int i = 0; i < key.length(); i++){
            DLBNode newDown = setDownDLBNode(key.charAt(i), curr);
            curr = newDown;
        }
        count += 1;
    }

    /**
     * Check if the dictionary contains a word
     *
     * @param   key Word to search the dictionary for
     *
     * @return  true if key is in the dictionary, false otherwise
     */
    public boolean contains(String key){
        DLBNode curr = root;

        for(int i = 0; i < key.length(); i++){
            curr = getRightDLBNode(key.charAt(i), curr);
            if(curr == null){
                return false;
            }

            curr = curr.getDown();
        }
        
        DLBNode endNode = getRightDLBNode('^', curr); //get valid key
        if(endNode != null){
            return true;
        }

        return false;  //otherwise not in dict, return false
    }

    private int listLength(DLBNode curr){
        int count = 0;
        if(curr == null){
            return 0;
        }

        else if(curr.getRight()==null){
            return 0;
        }

        while(curr != null){
            curr = curr.getRight();
            count++;
        }
        return count;
    }

    /**
     * Check if a String is a valid prefix to a word in the dictionary
     *
     * @param   pre Prefix to search the dictionary for
     *
     * @return  true if prefix is valid, false otherwise
     */
    public boolean containsPrefix(String pre){
        DLBNode curr = root;

        for(int i = 0; i < pre.length(); i++){
            curr = getRightDLBNode(pre.charAt(i), curr);
            if(curr == null){
                return false;
            }
            curr = curr.getDown();
        }

        DLBNode endNode = getRightDLBNode('^', curr);
        if(endNode == null){
            return true;
        }
        else if(listLength(curr) > 1){
            return true;
        }
        return false;
    }

    /**
     * Search for a word one character at a time
     *
     * @param   next Next character to search for
     *
     * @return  int value indicating result for current by-character search:
     *              -1: not a valid word or prefix
     *               0: valid prefix, but not a valid word
     *               1: valid word, but not a valid prefix to any other words
     *               2: both valid word and a valid prefix to other words
     */
    public int searchByChar(char next){
        DLBNode curr = root;
        currPrefix += next;
        boolean validPrefix = false;
        boolean validWord = false;

        for(int i = 0; i < currPrefix.length(); i++){
            curr = getRightDLBNode(currPrefix.charAt(i), curr);
            
            if(curr == null){
                return -1;
            }
            curr = curr.getDown();

            if(i == currPrefix.length() - 1){
                if(curr != null){
                    if (getRightDLBNode('^',curr) != null){ 
                        validWord = true;
                    } 
                    else{ 
                        validPrefix = true;
                    }
                    if (curr.getRight() != null){
                        validPrefix = true; 
                    }
                }
            }
        }
        if(validPrefix && validWord){
            return 2;
        }
        else if(validPrefix && !validWord){
            return 0;
        }
        else if(!validPrefix && validWord){
            return 1;
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
    }

    /**
     * Suggest up to 5 words from the dictionary based on the current
     * by-character search. Ordering should depend on the implementation.
     * 
     * @return  ArrayList<String> List of up to 5 words that are prefixed by
     *          the current by-character search
     */
    public ArrayList<String> suggest() {
        DLBNode curr = root;
        ArrayList<String> allCurrWords = new ArrayList<String>(); 

        for (int i = 0; i < currPrefix.length(); i++) {
            curr = getRightDLBNode(currPrefix.charAt(i), curr); 
            if (curr == null){
                return allCurrWords;
            }
            curr = curr.getDown();
            if (i == currPrefix.length() - 1) {
                allCurrWords = recSuggest(curr, allCurrWords, currPrefix); 
            }
        }
        return allCurrWords; 
    }
    private ArrayList<String> recSuggest(DLBNode curr, ArrayList<String> allCurrWords, String word) {
        if (curr == null){ 
            return allCurrWords;
        }
        if (allCurrWords.size() == 5){ 
            return allCurrWords;
        }
        if (getRightDLBNode('^', curr) != null) {
            allCurrWords.add(word); 
        }

        recSuggest(curr.getDown(), allCurrWords, word+curr.getLet()); 
        recSuggest(curr.getRight(), allCurrWords, word);

        return allCurrWords;
    }

    /**
     * List all of the words currently stored in the dictionary
     * @return  ArrayList<String> List of all valid words in the dictionary
     */
    public ArrayList<String> traverse(){
        ArrayList<String> allCurrWords = new ArrayList<String>();
        return recTraverse(root, allCurrWords, "");
    }

    //recursive traverse helper
    private ArrayList<String> recTraverse(DLBNode curr, ArrayList<String> allCurrWords, String result) {
        if(curr == null){
            return allCurrWords;
        } 
        if(getRightDLBNode('^', curr) != null) {
            allCurrWords.add(result); 
        }

        recTraverse(curr.getDown(), allCurrWords, result+curr.getLet()); 
        recTraverse(curr.getRight(), allCurrWords, result);
        
        return allCurrWords;
        }

    /**
     * Count the number of words in the dictionary
     *
     * @return  int, the number of (distinct) words in the dictionary
     */
    public int count(){
        return count;
    }
}