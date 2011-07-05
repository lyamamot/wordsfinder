/* $Id$ */
/*
 * $Copyright$
 */
package lyamamot.wordsfinder;

/** Stores the first longest word encountered. */
public class LongestWordEvaluator implements WordEvaluator {
	private String _longest = "";
	
	/* (non-Javadoc)
	 * @see scratch.trie.Hanging.WordEvaluator#evaluate(java.lang.String)
	 */
	@Override
	public void evaluate(String word) {
		if (word.length() > _longest.length()) {
			_longest = word;
		}
	}
	
	/** Returns the first longest word. */
	public String getLongest() {
		return _longest;
	}
}