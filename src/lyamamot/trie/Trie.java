/* $Id$ */
/*
 * $Copyright$
 */
package lyamamot.trie;

public interface Trie
{
    /** Adds the given sequence to the trie. */
    void add(String word);
 
    /**
     * Returns whether or not the trie contains the given sequence.
     * 
     * @param sequence sequence to test
     * @return <tt>true</tt> if the sequence is a prefix of an item in the trie.
     */
    boolean contains(String word);
 
    /**
     * Returns whether or not the given sequence is a valid prefix of an item in the trie.
     * 
     * @param sequence sequence to test
     * @return <tt>true</tt> if the sequence is a prefix of an item in the trie.
     */
    boolean isValidPrefix(String word);
 
    /**
     * Returns the number of items contained in this trie.
     * 
     * @return size
     */
    int size();
    
    // Partial Trie methods
    
}