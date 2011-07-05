/* $Id$ */
/*
 * $Copyright$
 */
package lyamamot.wordsfinder;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Stores the words with the longest, highest scoring words encountered.
 * @author lyamamot
 */
public class LongestHighestScoringWordsEvaluator extends ScoringEvaluator implements WordEvaluator {
	private Map<Integer, SortedSet<String>> _scoresToWords = new TreeMap<Integer, SortedSet<String>>(new Comparator<Integer>() {
		@Override
		public int compare(Integer o1, Integer o2) {
			return -1 * o1.compareTo(o2);
		}
	});
	private int _currentWordLength = Integer.MIN_VALUE;
	
	public LongestHighestScoringWordsEvaluator(Map<Integer, BonusTile> bonuses) {
		this(WORDS_WITH_FRIENDS_POINTS, bonuses);
	}
	
	public LongestHighestScoringWordsEvaluator(int[] scores, Map<Integer, BonusTile> bonuses) {
		super(scores, bonuses);
	}
	
	/* (non-Javadoc)
	 * @see scratch.trie.ScoringEvaluator#evaluate(java.lang.String)
	 */
	@Override
	public void evaluate(String word) {
		int currentScore = score(word);
		
		if (word.length() < _currentWordLength) {
			return;
		} else if (word.length() > _currentWordLength) {
			_currentWordLength = word.length();
			_scoresToWords.clear();
		}
		
		SortedSet<String> words;
		if (_scoresToWords.containsKey(currentScore)) {
			words = _scoresToWords.get(currentScore);
		} else {
			words = new TreeSet<String>();
		}
		
		words.add(word);
		_scoresToWords.put(currentScore, words);
	}
	
	public Map<Integer, SortedSet<String>> getWords() {
		return Collections.unmodifiableMap(_scoresToWords);
	}
}
