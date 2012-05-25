/* $Id$ */
/*
 * $Copyright$
 */
package lyamamot.wordsfinder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lyamamot
 */
public class WordsSolver {
	private static int mostFrequentLetterCount = -1;
	private static char mostFrequentLetter = ' ';
	private static char[] letterFrequencies = new char[26];
	private static List<String> words = new LinkedList<String>();
	
	private void index(String s) {
		String uniqueLetters = s.replaceAll("(.)(?=.*?\\1)", "");
		char[] letters = uniqueLetters.toCharArray();
		for (char c : letters) {
			letterFrequencies[c - 'a']++;
			
			if (letterFrequencies[c - 'a'] > mostFrequentLetterCount) {
				mostFrequentLetterCount = letterFrequencies[c - 'a'];
				mostFrequentLetter = c;
			}
		}
	}
	
	public void solve(String wordsFile, String pattern, String notLetters) throws FileNotFoundException, IOException {
		// Find all of the guessed letters by removing the "@" signs.
		String guessedLetters = pattern.replace("@", "").replace("#", "");
		// Make the guessed letters string unique. http://stackoverflow.com/questions/2582940/how-do-i-remove-duplicate-characters-and-keep-the-unique-one-only-in-perl
		guessedLetters = guessedLetters.replaceAll("(.)(?=.*?\\1)", "");
		
		// Surround each group of "@" and "#" signs with grouping parentheses.
		pattern = pattern.replaceAll("(@+|#+)", "\\($1\\)");
		// Assemble the final pattern. The pattern represents a single word, so surround with ^$. Each "@" sign represents all possible remaining letters.
		String lettersAndVowelsPattern = "[^" + guessedLetters + notLetters + "]";
		String lettersPattern = "[^aeiou" + guessedLetters + notLetters + "]";
		if ((guessedLetters + notLetters).isEmpty()) {
			lettersAndVowelsPattern = ".";
			lettersPattern = "[^aeiou]";
		}
		pattern = "^" + pattern.replace("@", lettersAndVowelsPattern).replace("#", lettersPattern) + "$";

		Pattern p = Pattern.compile(pattern);
		
		BufferedReader in = new BufferedReader(new FileReader(wordsFile));
		String line;
		while (null != (line = in.readLine())) {
			Matcher matcher = p.matcher(line);
			if (!matcher.matches()) {
				continue;
			}

			words.add(line);
			for (int i = 1; i <= matcher.groupCount(); i++) {
				String group = line.substring(matcher.start(i), matcher.end(i));
				index(group);
			}
		}
		
		System.out.println("Most frequent letter: " + mostFrequentLetter);
		for (char c = 'a'; c <= 'z'; c++) {
			int count = (int)letterFrequencies[c - 'a'];
			System.out.println(c + ": " + (count == 0 ? '-' : Integer.toString(count)));
		}
		System.out.println("Words: " + words);
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("You must specify the word list to use as the first argument.");
			return;
		}
		
		String pattern = "", notLetters = "";
		if (args.length >= 2) {
			pattern = args[1];
		} else {
			System.err.println("You must specify the word pattern as the second argument. Use @ for letters before the last vowel and # for letters after the last vowel. e.g. @@a## for the word \"smash\".");
			return;
		}
		
		if (args.length >= 3) {
			notLetters = args[2];
		} else {
			System.err.println("You must specify the excluded letters as the third argument.");
			return;
		}
		
		// For debug purposes only:
		//pattern = "@@a##";
		pattern = "@@@@@";
		notLetters = "";
		// End debug.
		
		new WordsSolver().solve(args[0], pattern, notLetters);
	}
}
