package org.nyu.crypto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class Digrapher {

    @Value("${space.value}")
    private int spaceval;

    @Value("${alphabet.length}")
    private int alphabet;

    @Autowired
    private DictionaryGenerator dictionaryGenerator;

    // FIXME - this might be wrong!
    // TODO - include space?
    public double[][] computeDigraph() {

        double[][] digraph = new double[alphabet][alphabet];

        String[] words = dictionaryGenerator.generateDictionaryDto().getWords();

        int digraphs = 0;
        for (String word : words) {
            digraphs += word.length() - 1;
            for (int i = 0; i < word.length() - 1; i++) {
                digraph[convert(word.charAt(i))][convert(word.charAt(i + 1))] += 1;
            }
        }

        for (int i = 0; i < digraph.length; i++) {
            for (int j = 0; j < digraph[i].length; j++) {
                digraph[i][j] = (digraph[i][j] / digraphs) * 100;
            }
        }

        return digraph;
    }

    private int convert(char c) {
        if (c == ' ') return spaceval;
        else return (c - 'a');
    }
}
