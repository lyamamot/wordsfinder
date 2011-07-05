/* $Id$ */
/*
 * $Copyright$
 */
package lyamamot.wordsfinder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lyamamot.trie.LinkedStringTrie;
import lyamamot.trie.PartialTrie;
import lyamamot.trie.Trie;
import lyamamot.wordsfinder.ScoringEvaluator.BonusTile;

/**
 * Searches a word list for words based on the given evaluation criteria.
 * 
 * @author lyamamot
 */
public class WordsFinder {
	private final int MIN_WORD_LENGTH;
	private final int MAX_WORD_LENGTH;
	
	private Trie _trie = new LinkedStringTrie();
	private List<WordEvaluator> _evaluators;
	
	/**
	 * Usage: <word list> [<letters>]
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("You must specify the word list to use as the first argument.");
			return;
		}
		
		long start = System.currentTimeMillis();
		String letters = args.length >= 2 ? args[1] : "yadefemsbupl";
		Map<Integer, BonusTile> bonuses = Collections.singletonMap(6, BonusTile.tripleLetter);
		
		LongestWordEvaluator longestWordEvaluator = new LongestWordEvaluator();
		HighestScoringWordEvaluator highestScoringWordEvaluator = new HighestScoringWordEvaluator(bonuses);
		HighestScoringWordsEvaluator highestScoringWordsEvaluator = new HighestScoringWordsEvaluator(bonuses);
		LongestHighestScoringWordsEvaluator longestHighestScoringWordsEvaluator = new LongestHighestScoringWordsEvaluator(bonuses);
		
		List<WordEvaluator> evals = new ArrayList<WordEvaluator>();
		evals.add(longestWordEvaluator);
		evals.add(highestScoringWordEvaluator);
		evals.add(highestScoringWordsEvaluator);
		evals.add(longestHighestScoringWordsEvaluator);
		
		WordsFinder h = new WordsFinder(args[0], evals);
		h.findWord(letters);
		System.out.println("Elapsed time: " + (System.currentTimeMillis() - start) + " ms.");
		
		System.out.println("Longest word: " + longestWordEvaluator.getLongest());
		System.out.println("Highest scoring word: " + highestScoringWordEvaluator.getWord() + ", " + highestScoringWordEvaluator.getScore() + " points");
		System.out.println("Highest scoring words: " + highestScoringWordsEvaluator.getWords());
		System.out.println("Longest, highest scoring words: " + longestHighestScoringWordsEvaluator.getWords());
	}
	
	public WordsFinder(String wordsFile, List<WordEvaluator> evaluators) throws IOException {
		MIN_WORD_LENGTH = 4;
		MAX_WORD_LENGTH = 8;
		
		initializeTrie(wordsFile);
		_evaluators = evaluators;
	}
	
	private void initializeTrie(String wordsFile) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(wordsFile));
		String line;
		while (null != (line = in.readLine())) {
			_trie.add(line.trim().toLowerCase());
		}
	}
	
	private void findWord(char[] letters, String prefix) {
		if (prefix.length() >= MAX_WORD_LENGTH) {
			return;
		}
		
		for (int i = 0; i < letters.length; i++) {
			String word = prefix + letters[i];
			
			if (word.length() >= MIN_WORD_LENGTH && _trie.contains(word)) {
				for (WordEvaluator evaluator : _evaluators) {
					evaluator.evaluate(word);
				}
			}
			
			if (_trie.isValidPrefix(word)) {
				char[] remainingLetters = new char[letters.length - 1];
				
				System.arraycopy(letters, 0, remainingLetters, 0, i);
				System.arraycopy(letters, i + 1, remainingLetters, i, letters.length - i - 1);
				
				findWord(remainingLetters, word);
			}
		}
	}

	@SuppressWarnings("unused")
	private void findWord(char[] letters, String prefix, PartialTrie trie) {
		if (prefix.length() >= MAX_WORD_LENGTH) {
			return;
		}
		
		for (int i = 0; i < letters.length; i++) {
			
			PartialTrie partialTrie = trie.getChild(letters[i]);
			if (null == partialTrie) {
				continue;
			}
			
			String word = prefix + letters[i];
			if (word.length() >= MIN_WORD_LENGTH && partialTrie.isTerminal()) {
				for (WordEvaluator evaluator : _evaluators) {
					evaluator.evaluate(word);
				}
			}
			
			// Remove current letter.
			char[] remainingLetters = new char[letters.length - 1];
			System.arraycopy(letters, 0, remainingLetters, 0, i);
			System.arraycopy(letters, i + 1, remainingLetters, i, letters.length - i - 1);
			
			findWord(remainingLetters, word, partialTrie);
		}
	}
	
	public void findWord(String letters) {
		if (null == letters) {
			return;
		}
		
		findWord(letters.toCharArray(), "");
	}
}
