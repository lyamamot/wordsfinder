/* $Id$ */
/*
 * $Copyright$
 */
package lyamamot.wordsfinder;

/** Strategy for processing candidate words. */
public interface WordEvaluator {
	/**
	 * Evaluates the given word.
	 * @param word candidate
	 */
	void evaluate(String word);
}