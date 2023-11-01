package cs1501_p2;

import java.io.*;
import java.util.*;

public class AutoCompleter implements AutoComplete_Inter{

	private DLB dlb;
	private UserHistory userhistory;

	public AutoCompleter(String dict, String history) {
		dlb = new DLB();
		userhistory = new UserHistory();
		
		try{
			File dict_file = new File(dict);
			Scanner reader = new Scanner(dict_file);

			while(reader.hasNextLine()){
				String word = reader.nextLine();
				dlb.add(word);
			}
			reader.close();

			File userFile = new File(history);
			Scanner userReader = new Scanner(userFile);

			while(userReader.hasNextLine()) {
				String userWord = userReader.nextLine();
				userhistory.add(userWord); 
			}
			userReader.close();
		}

		catch(FileNotFoundException e){
			//System.out.println("File not found");
			e.printStackTrace();
		}
	}

	public AutoCompleter(String dict){
		dlb = new DLB();
		userhistory = new UserHistory();

		try{
			File dict_file = new File(dict);
			Scanner reader = new Scanner(dict_file);

			while(reader.hasNextLine()){
				String word = reader.nextLine();
				dlb.add(word);
			}
			reader.close();
		}
		catch(FileNotFoundException e){
			//System.out.println("File not found");
			e.printStackTrace();
		}
	}

    /**
	 * Produce up to 5 suggestions based on the current word the user has
	 * entered These suggestions should be pulled first from the user history
	 * dictionary then from the initial dictionary. Any words pulled from user
	 * history should be ordered by frequency of use. Any words pulled from
	 * the initial dictionary should be in ascending order by their character
	 * value ("ASCIIbetical" order).
	 *
	 * @param 	next char the user just entered
	 *
	 * @return	ArrayList<String> List of up to 5 words prefixed by cur
	 */	
	public ArrayList<String> nextChar(char next){
		ArrayList<String> res = new ArrayList<String>(); // resulting list to be returned
		
		//if user history is currently empty, return 5 suggestions
		dlb.searchByChar(next);
		if(userhistory.count() == 0){
			return dlb.suggest();
		}

		userhistory.searchByChar(next);
		ArrayList<String> userSugs = userhistory.suggest();
		for(String sug : userSugs){
			if(res.size() == 5){
				return res;
			}
			res.add(sug);
		}

		if(res.size() == 5){
			return res;
		}
		ArrayList<String> tempDict = dlb.suggest();
		for(String word : tempDict){
			if(res.size() == 5){
				return res;
			}
			if(res.contains(word)){
				continue;
			}

			res.add(word);
		}
		return res;
    }

	/**
	 * Process the user having selected the current word
	 *
	 * @param 	cur String representing the text the user has entered so far
	 */
	public void finishWord(String cur){
		dlb.resetByChar();
		userhistory.resetByChar();
		userhistory.add(cur);
	}

	/**
	 * Save the state of the user history to a file
	 *
	 * @param	fname String filename to write history state to
	 */
	public void saveUserHistory(String fname){
		ArrayList<String> traversal = userhistory.traverse();
		
		try{
			FileWriter filewriter = new FileWriter(fname);
			for(String word : traversal){
				filewriter.write(word + System.lineSeparator());
			}
			filewriter.close();
		}
		catch(IOException e){
			//System.out.println("IO Exception");
			e.printStackTrace();
		}

		dlb.resetByChar();
		userhistory.resetByChar();
	}
}