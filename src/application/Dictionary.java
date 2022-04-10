package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Dictionary {
	
	private HashMap<String, String> dictionary;
	
	private File word_list;
	
	private String cur_word;
	
	//dictionary constructor
	public Dictionary() throws FileNotFoundException {
		word_list = new File("src/assets/words.txt");
		dictionary = new HashMap<String, String>();
		Scanner file_scanner;
		file_scanner = new Scanner(word_list);
		while(file_scanner.hasNextLine()) {
			String word = file_scanner.nextLine();
			//not really trying to find a hashmap, just creating a hash
			dictionary.put(new String(word), new String(word));
		}
		file_scanner.close();
	}
	
	//choose a word to be the current word
	public void chooseWord(char c) {
		Set<String> keys = dictionary.keySet();
		Object[] words = keys.toArray();
		//generate random indices until index with word that starts with the letter is found
		Random r = new Random();
		int index = r.nextInt(words.length);
		while(words[index].toString().charAt(0) != c) {
			index = r.nextInt(words.length);
		}
		cur_word = (String) words[index];
	}
	
	//returns the current word for this round
	public String getCurrentWord() {
		return cur_word;
	}
	
	//returns if the dictionary contains the word
	public boolean containsWord(String word) {
		return dictionary.containsKey(word);
	}
	
}
