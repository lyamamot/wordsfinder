/* $Id$ */
/*
 * $Copyright$
 */
package lyamamot.trie;

/**
 * @author lyamamot
 */
public interface PartialTrie extends Trie {
	/** Returns the partial trie rooted at the given child of the current trie. */
	PartialTrie getChild(char value);
	
	/** Returns whether this partial trie represents the end of an actual word. */
	boolean isTerminal();
}
