package com.example.dividend;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;

public class AutoComplete {

    private Trie trie = new PatriciaTrie();

    public void add(String s) {
        trie.put(s, "world");
    }

    public Object get(String s) {
        return trie.get(s);
    }
}
