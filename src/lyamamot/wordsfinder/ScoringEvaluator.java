/* $Id$ */
/*
 * $Copyright$
 */
package lyamamot.wordsfinder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract base class for an evaluator that scores words based on letter point value and bonus tiles.
 * @author lyamamot
 */
public abstract class ScoringEvaluator implements WordEvaluator {
	/** Enum representing the possible bonus tile types, used for scoring. */
	public static enum BonusTile {
		doubleLetter, tripleLetter, doubleWord, tripleWord;
	}
	
	/** Letter point values for Scrabble. */
	protected static final int[] SCRABBLE_POINTS = new int[] {
			1, 3, 3, 2, 1,
			4, 2, 4, 1, 8,
			5, 1, 3, 1, 1,
			3, 10, 1, 1, 1,
			1, 4, 4, 8, 4, 10
		};
	
	/** Letter point values for Words With Friends/Hanging With Friends. */
	protected static final int[] WORDS_WITH_FRIENDS_POINTS = new int[] {
			1, 4, 4, 2, 1,
			4, 3, 3, 1, 10,
			5, 2, 4, 2, 1,
			4, 10, 1, 1, 1,
			2, 5, 4, 8, 3, 10
		};
	
	/** Map of character to letter point value. */
	protected Map<Character, Integer> _letterToPoint = new HashMap<Character, Integer>();
	
	/** Map of letter index to bonus tile type. */
	protected Map<Integer, BonusTile> _bonuses = new HashMap<Integer, BonusTile>();
	
	public ScoringEvaluator(Map<Integer, BonusTile> bonuses) {
		this(WORDS_WITH_FRIENDS_POINTS, bonuses);
	}
	
	public ScoringEvaluator(int[] scores, Map<Integer, BonusTile> bonuses) {
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		if (null == scores || scores.length < alphabet.length()) {
			throw new IllegalArgumentException("Not enough point values for the letters in the alphabet.");
		}
		
		char[] letters = alphabet.toCharArray();
		for (int i = 0; i < letters.length; i++) {
			_letterToPoint.put(letters[i], scores[i]);
		}
		
		if (null != bonuses) {
			_bonuses.putAll(bonuses);
		}
	}
	
	/* (non-Javadoc)
	 * @see scratch.trie.WordEvaluator#evaluate(java.lang.String)
	 */
	@Override
	public abstract void evaluate(String word);
	
	/** Scores the given word according to the current letter point values and bonus tile placement. */
	protected int score(String word) {
		char[] letters = word.toCharArray();
		int score = 0;
		Set<BonusTile> bonuses = new HashSet<BonusTile>();
		
		// Determine if any of the letters in the word fall on a bonus tile.
		for (int i = 0; i < letters.length; i++) {
			if (_bonuses.containsKey(i)) {
				BonusTile bonus = _bonuses.get(i);
				switch (bonus) {
					case doubleLetter: score += _letterToPoint.get(letters[i]); break;
					case tripleLetter: score += _letterToPoint.get(letters[i]) * 2; break;
					case doubleWord: // fall through
					case tripleWord: bonuses.add(bonus); break;
					default:
						System.err.println("Unknown bonus type: " + bonus);
				}
			}
			score += _letterToPoint.get(letters[i]);
		}
		
		for (BonusTile bonus : bonuses) {
			switch (bonus) {
				case doubleWord: score *= 2; break;
				case tripleWord: score *= 3; break;
				default:
					System.err.println("Unknown bonus type: " + bonus);
			}
		}
		
		return score;
	}
}
