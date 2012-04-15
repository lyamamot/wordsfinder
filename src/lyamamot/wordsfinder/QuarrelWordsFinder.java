/* $Id$ */
/*
 * $Copyright$
 */
package lyamamot.wordsfinder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import lyamamot.trie.LinkedStringTrie;
import lyamamot.trie.PartialTrie;
import lyamamot.trie.Trie;

/**
 * Searches a word list for words based on the given evaluation criteria.
 * 
 * @author lyamamot
 */
public class QuarrelWordsFinder {
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
		
		String letters = args.length >= 2 ? args[1] : "wiffymokhaqe";
		HighestScoringWordsEvaluator highestScoringWordsEvaluator = new HighestScoringWordsEvaluator(ScoringEvaluator.QUARREL_POINTS);
		
		int maxWordLength = args.length >= 3 ? Integer.parseInt(args[2]) : 8;
		
		List<WordEvaluator> evals = new ArrayList<WordEvaluator>();
		evals.add(highestScoringWordsEvaluator);
		
		QuarrelWordsFinder h = new QuarrelWordsFinder(args[0], evals);
		long start = System.currentTimeMillis();
		h.findWord(letters);
		System.out.println("Elapsed time: " + (System.currentTimeMillis() - start) + " ms.");

		SortedMap<Integer, SortedSet<String>> wordMap = highestScoringWordsEvaluator.getWords();
		if (wordMap.isEmpty()) {
			System.err.println("No words found.");
			return;
		}
		
		SortedSet<String> sortedSet = wordMap.get(wordMap.firstKey());
		NavigableSet<String> words = null;
		if (sortedSet instanceof NavigableSet) {
			words = (NavigableSet<String>)sortedSet;
		} else {
			return;
		}
		
		System.out.println("Highest scoring words: " + words.descendingSet());
		
		// Find the highest scoring words that are no longer than the maximum length.
		SortedSet<String> highestScoringWords = new TreeSet<String>(new DescendingLengthComparator());
		for (Entry<Integer, SortedSet<String>> entry : wordMap.entrySet()) {
			for (String word : entry.getValue()) {
				if (word.length() > maxWordLength) {
					continue;
				}
				
				highestScoringWords.add(word);
			}
			
			if (!highestScoringWords.isEmpty()) {
				break;
			}
		}
		
		System.out.println("Highest scoring words less than length " + maxWordLength + ": " + highestScoringWords);
	}
	
	public QuarrelWordsFinder(String wordsFile, List<WordEvaluator> evaluators) throws IOException {
		MIN_WORD_LENGTH = 1;
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
	
	/**
	 * Recursively finds candidate words from the given letters, maintaining the current partial word during our evaluation.
	 * 
	 * @param letters available letters for forming the rest of the word
	 * @param prefix partial word prefix we have discovered so far
	 */
	@SuppressWarnings("unused")
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

	/**
	 * Recursively finds candidate words from the given letters, maintaining the current partial word and trie during our evaluation.
	 * 
	 * This implementation is more efficient than {@link #findWord(char[], String)} since it does not repeat the work of determining whether the trie
	 * contains the prefix part of the word, nor does it need to test whether a prefix is valid since we are sure that it is.
	 * 
	 * @param letters available letters for forming the rest of the word
	 * @param prefix partial word prefix we have discovered so far
	 * @param trie remaining trie to evaluate
	 */
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
	
	/**
	 * Finds words from the stored word list using the given letters.
	 * 
	 * @param letters string containing letters to use in forming words
	 */
	public void findWord(String letters) {
		if (null == letters) {
			return;
		}
		
		//findWord(letters.toCharArray(), "");
		findWord(letters.toCharArray(), "", (PartialTrie)_trie);
	}
}
