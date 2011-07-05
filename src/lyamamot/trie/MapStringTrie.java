/* $Id$ */
/*
 * $Copyright$
 */
package lyamamot.trie;

import java.util.HashMap;
import java.util.Map;

/**
 * A trie is a structure for storing a set of sequences, where common prefixes
 * are only stored once. This makes storage more efficient.
 */
public class MapStringTrie implements Trie, PartialTrie {
    protected Map<Character, MapStringTrie> _children = new HashMap<Character, MapStringTrie>();
 
    /** Individual character from this sequence. */
    protected char _value;

    /** Whether this node represents the last character of a word. */
    protected boolean _isTerminal = true;
 
    /** Total number of words added to this trie. */
    protected static int _size = 0;
 
    @Override
    public void add(String word) {
        if (null == word) {
            return;
        }
 
        if (word.isEmpty()) {
            _isTerminal = true;
            _size++;
            return;
        }
 
        Trie child = getChild(word.charAt(0));
        if (null == child) {
            MapStringTrie parent = this;
            MapStringTrie newChild = null;
            for (char value : word.toCharArray()) {
                newChild = new MapStringTrie();
                newChild._value = value;
                newChild._isTerminal = false;
 
                parent._children.put(value, newChild);
 
                parent = newChild;
            }
 
            newChild._isTerminal = true;
            _size++;
        } else {
            child.add(word.substring(1));
        }
    }
    
    /**
     * Returns the child at the end of the given sequence or <tt>null</tt> if it does not exist.
     * @param word sequence to test
     * @return child
     */
    private MapStringTrie getChild(String word) {
        MapStringTrie current = this;
        for (char value : word.toCharArray()) {
        	MapStringTrie child = current.getChild(value);
            if (null == child) {
                return null;
            }
 
            current = child;
        }
 
        return current;
    }
 
    /** Returns the node has the given value as a child. Null if it does not exist. */
	public MapStringTrie getChild(char value) {
		return _children.get(value);
    }
 
    @Override
    public boolean contains(String word) {
    	MapStringTrie child = getChild(word);
        if (null == child) {
            return false;
        }
 
        return child._isTerminal;
    }
    
    @Override
    public boolean isValidPrefix(String word) {
        Trie child = getChild(word);
        return null != child;
    }
 
    @Override
    public boolean isTerminal() {
    	return _isTerminal;
    }
    
    @Override
    public int size() {
        return _size;
    }
 
    @Override
    public String toString() {
        return "Trie[" + _value + "]";
    }
}