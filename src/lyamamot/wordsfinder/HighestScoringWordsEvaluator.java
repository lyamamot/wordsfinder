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
		this(null);
	}
	
	public HighestScoringWordsEvaluator(Map<Integer, BonusTile> bonuses) {
		this(WORDS_WITH_FRIENDS_POINTS, bonuses);
	}
	
	public HighestScoringWordsEvaluator(int[] scores, Map<Integer, BonusTile> bonuses) {
		super(scores, bonuses);

		_wordListLength = 6;
	}
	
	/* (non-Javadoc)
	 * @see scratch.trie.Hanging.WordEvaluator#evaluate(java.lang.String)
	 */
	@Override
	public void evaluate(String word) {
		int currentScore = score(word);

		if (_scoresToWords.size() >= _wordListLength && currentScore > _scoresToWords.lastKey()) {
			_scoresToWords.remove(_scoresToWords.lastKey());
		}

		SortedSet<String> words;
		if (_scoresToWords.containsKey(currentScore)) {
			words = _scoresToWords.get(currentScore);
		} else {
			words = new TreeSet<String>(new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					if (o1.length() != o2.length()) {
						if (o1.length() > o2.length()) {
							return -1;
						} else if (o1.length() < o2.length()) {
							return 1;
						} else {
							return 0;
						}
					} else {
						return o1.compareTo(o2);
					}
				}
			});
		}
		
		words.add(word);
		_scoresToWords.put(currentScore, words);
	}
	
	public String getWord() {
		return _scoresToWords.firstEntry().getValue().first();
	}
	
	public int getScore() {
		return _scoresToWords.firstKey();
	}
	
	public Map<Integer, SortedSet<String>> getWords() {
		return Collections.unmodifiableMap(_scoresToWords);
	}
}