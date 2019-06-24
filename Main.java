import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {

	public static String arrayOfStrings[] = new String[120000];
	public static String currentWord = "";
	public static int wordlength;
	public static int guesses;
	public static char guess;
	public static boolean isCheat;
	public static ArrayList<String> currentWords = new ArrayList<String>();
	public static Set<Character> guessedLetters = new HashSet<Character>();
	public static Scanner sc = new Scanner(System.in);


	public static void main(String[] args) {
		readWords("dictionary.txt"); //reads file into an array
		initiate();
		playGame();
		playAgain();
	}


	private static void playAgain() {
		System.out.println("Want to play again? (yes/no)");
		String ans = sc.nextLine();
		if(ans.charAt(0) == 'y') {
			initiate();
			playGame();
			playAgain();
		} 
		else if(ans.charAt(0) == 'n') {
			System.exit(0);
		}
		else {
			playAgain();
		}
	}


	private static void playGame() {
		String key = "";
		for(int i = 0; i < wordlength; i++) {
			currentWord += "-";
		}
		while(guesses > 0) {
			printStatus();
			System.out.println("Please guess a letter");
			key = partitionWords(getGuess());
			updateCurrentWord(key);
			//			System.out.println(currentWords);
			if(currentWords.size() == 1) {
				printStatus();
				System.out.println("You won! Good job. Your word was " + currentWords.get(0) + ".");
				break;
			}
		}
		if(guesses == 0) {
			System.out.println("Your word was " + currentWords.get(1) + ". Nice try!");
		}

	}

	private static void updateCurrentWord(String key) {
		System.out.println(key);
		for(int i = 0; i < key.length(); i++) {
			if(Character.isDigit(key.charAt(i))) {
				char[] currentWordChar = currentWord.toCharArray();
				currentWordChar[Integer.parseInt("" + key.charAt(i))] = guess;
				currentWord = String.valueOf(currentWordChar);
			}
		}
		if(key.equals("[]")) {
			guesses--;
		}
	}


	private static String partitionWords(char guess) {
		Map<String, Integer> familyGroups = new HashMap<String, Integer>();
		for(int i = 0; i < currentWords.size(); i++) {
			ArrayList<Integer> indices = new ArrayList<Integer>();
			int searchStart = 0;
			int index = 0;
			while(true) {	
				index = currentWords.get(i).indexOf(guess, searchStart);
				if(index == -1) break;
				indices.add(index);
				searchStart = index + 1;
			}
			if(familyGroups.containsKey(indices.toString())) {
				familyGroups.put(indices.toString(), familyGroups.get(indices.toString()) + 1);
			} else {
				familyGroups.put(indices.toString(), 1);
			}
		}
		int biggestFamily = 0;
		String biggestFamilyKey = "";
		for(String key : familyGroups.keySet()) {
			if(familyGroups.get(key) > biggestFamily) {
				biggestFamily = familyGroups.get(key);
				biggestFamilyKey = key;
			}
		}
		ArrayList<String> remainingWords = new ArrayList<String>();

		for(int i = 0; i < currentWords.size(); i++) {
			ArrayList<Integer> indices = new ArrayList<Integer>();
			int searchStart = 0;
			int index = 0;
			while(true) {	
				index = currentWords.get(i).indexOf(guess, searchStart);
				if(index == -1) break;
				indices.add(index);
				searchStart = index + 1;
			}
			if(biggestFamilyKey.equals(indices.toString())) {
				remainingWords.add(currentWords.get(i));
			}			
		}
		System.out.println("Your letters are in the place " + biggestFamilyKey);
		currentWords = remainingWords;

		return biggestFamilyKey;
	}


	private static char getGuess() {
		String guessed = sc.nextLine();
		if(guessed.length() != 1 || guessedLetters.contains(guessed.charAt(0)) || !Character.isLetter(guessed.charAt(0))) {
			System.out.println("Please make sure you entered a single lowercase letter that you have not guessed already.");
			return getGuess();
		} else {
			guessedLetters.add(guessed.charAt(0));
			guess = guessed.charAt(0);
			return guessed.charAt(0);
		}
	}


	private static void printStatus() {

		System.out.println("You have " + guesses + " guesses left");
		System.out.println("You have guessed " + guessedLetters.toString());
		System.out.println("Your current word is " + currentWord);
		if(isCheat) {
			System.out.println("You have " + currentWords.size() + " words remaining");
		}
	}


	private static void initiate() {
		currentWords = new ArrayList<String>();
		guessedLetters = new HashSet<Character>();
		currentWord = "";
		promptWordLength(); //asks user for a word length
		shrinkSize(wordlength); //takes the big array and shrinks it into one with words of the preferred word length
		promptGuessNumber();//gets the number of guesses that the user wants
		promptCheatMode();//asks if user wants to enable cheat mode

	}



	private static void promptCheatMode() {
		System.out.println("Would you like to enable cheat mode? (yes/no)");

		char c = sc.next().trim().charAt(0);

		if(c == 'y') {
			System.out.println("Cheat mode enabled.");
			isCheat = true;
		} else if (c == 'n') {
			System.out.println("Cheat mode disabled.");
			isCheat = false;
		} else {
			promptCheatMode();
		}
	}


	private static void promptGuessNumber() {
		System.out.println("How many guesses do you want?");
		guesses = sc.nextInt();
		if(guesses <= 0) {
			promptGuessNumber();
		}
		System.out.println("You have " + guesses + " guesses.");
	}


	private static void shrinkSize(int wordlength) {
		for(int i = 0; i < arrayOfStrings.length; i++) {
			if(arrayOfStrings[i].length() == wordlength) {
				currentWords.add(arrayOfStrings[i]);
			}
		}
		System.out.println("There are " + currentWords.size() + " words in our dictionary that have this length.");
	}


	private static void promptWordLength() {
		boolean isDone = false;
		while(isDone == false) {
			System.out.println("What word length would you like?");
			wordlength = sc.nextInt();

			for(int i = 0; i < arrayOfStrings.length; i++) {
				if(wordlength == arrayOfStrings[i].length()) {
					isDone = true;
				}
			}
		}
	}

	public static String[] readWords(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = "";
			int counter = 0;
			while (((line = reader.readLine()) != null) && (counter < 120000)) {
				arrayOfStrings[counter] = line;
				counter++;
			}
			reader.close();
		}
		catch (Exception ex) { System.out.println("Exception: " + ex.getMessage()); }
		return arrayOfStrings;
	}
}
