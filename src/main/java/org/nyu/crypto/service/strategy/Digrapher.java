package org.nyu.crypto.service.strategy;

import org.nyu.crypto.service.DictionaryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class Digrapher {

    @Value("${space.value}")
    private int spaceval;

    @Value("${alphabet.length}")
    private int alphabet;

    @Value("${charset.length}")
    private int charset;

    @Autowired
    private DictionaryGenerator dictionaryGenerator;

    // we use this to calculate the initial dictionary digraph matrix
    public double[][] computeDictionaryDigraph() {

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

    // we use this method to compute the putative plaintext digraph
    public double[][] computeDigraph(String text) {

        double[][] putative = new double[charset][charset];

        // again, no need to check the last value
        for (int i = 0; i < text.length() - 1; i++) {
            putative[convert(text.charAt(i))][convert(text.charAt(i + 1))] += 1;
        }
        return putative;
    }

    private int convert(char c) {
        if (c == ' ') return spaceval;
        else return (c - 'a');
    }
}
