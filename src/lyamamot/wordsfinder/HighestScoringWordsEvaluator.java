/* $Id$ */
/*
 * $Copyright$
 */
package lyamamot.wordsfinder;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/** Stores the words with the highest scores encountered. */
public class HighestScoringWordsEvaluator extends HighestScoringWordEvaluator implements WordEvaluator {
	/** Map of a word score to a set of words, ordered from highest score to lowest score. */
	private TreeMap<Integer, SortedSet<String>> _scoresToWords = new TreeMap<Integer, SortedSet<String>>(new Comparator<Integer>() {
		@Override
		public int compare(Integer o1, Integer o2) {
			return -1 * o1.compareTo(o2);
		}
	});
	
	/** Number of scores, at most, to keep in the map. */
	private int _wordListLength;
	
	/** Create evaluator with default letter scores and no bonus tiles. */
	public HighestScoringWordsEvaluator() {
		this(WORDS_WITH_FRIENDS_POINTS);
	}
	
	public HighestScoringWordsEvaluator(Map<Integer, BonusTile> bonuses) {
		this(WORDS_WITH_FRIENDS_POINTS, bonuses);
	}
	
	public HighestScoringWordsEvaluator(int[] scores) {
		this(scores, null);
	}
	
	public HighestScoringWordsEvaluator(int[] scores, Map<Integer, BonusTile> bonuses) {
		super(scores, bonuses);

		_wordListLength = 8;
	}
	
	/* (non-Javadoc)
	 * @see scratch.trie.Hanging.WordEvaluator#evaluate(java.lang.String)
	 */
	@Override
	public void evaluate(String word) {
		int currentScore = score(word);

		SortedSet<String> words;
		if (_scoresToWords.containsKey(currentScore)) {
			words = _scoresToWords.get(currentScore);
		} else {
			words = new TreeSet<String>(new DescendingLengthComparator());
		}
		
		boolean addWord = false;
		if (_scoresToWords.size() < _wordListLength) {
			addWord = true;
		} else if (currentScore > _scoresToWords.lastKey()) {
			if (!_scoresToWords.containsKey(currentScore)) {
				_scoresToWords.remove(_scoresToWords.lastKey());
			}
			addWord = true;
		}
		
		if (addWord) {
			words.add(word);
			_scoresToWords.put(currentScore, words);
		}
	}
	
	@Override
	public String getWord() {
		return _scoresToWords.firstEntry().getValue().first();
	}
	
	@Override
	public int getScore() {
		return _scoresToWords.firstKey();
	}
	
	/**
	 * Returns the list of words, keyed by score, returned in descending order. The word lists themselves are stored in descending length and natural order.
	 * 
	 * @return word list
	 */
	public SortedMap<Integer, SortedSet<String>> getWords() {
		return Collections.unmodifiableSortedMap(_scoresToWords);
	}
}