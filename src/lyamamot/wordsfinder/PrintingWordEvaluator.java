/* $Id$ */
/*
 * $Copyright$
 */
package lyamamot.wordsfinder;

/** Prints every evaluated word without any additional computation. */
public class PrintingWordEvaluator implements WordEvaluator {
	/* (non-Javadoc)
	 * @see scratch.trie.Hanging.WordEvaluator#evaluate(java.lang.String)
	 */
	@Override
	public void evaluate(String word) {
		System.out.println(word);
	}
}