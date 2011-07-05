/* $Id$ */
/*
 * $Copyright$
 */
package lyamamot.wordsfinder;

import java.util.Map;

/** Stores the word with the highest score encountered. */
public class HighestScoringWordEvaluator extends ScoringEvaluator implements WordEvaluator {
	
	private String _word = "";
	private int _wordScore = Integer.MIN_VALUE;
	
	public HighestScoringWordEvaluator() {
		this(null);
	}
	
	public HighestScoringWordEvaluator(Map<Integer, BonusTile> bonuses) {
		this(WORDS_WITH_FRIENDS_POINTS, bonuses);
	}
	
	public HighestScoringWordEvaluator(int[] scores, Map<Integer, BonusTile> bonuses) {
		super(scores, bonuses);
	}
	
	/* (non-Javadoc)
	 * @see scratch.trie.Hanging.WordEvaluator#evaluate(java.lang.String)
	 */
	@Override
	public void evaluate(String word) {
		int currentScore = score(word);
		if (currentScore > _wordScore) {
			_word = word;
			_wordScore = currentScore; 
		}
	}
	
	public String getWord() {
		return _word;
	}
	
	public int getScore() {
		return _wordScore;
	}
}