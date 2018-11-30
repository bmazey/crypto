package org.nyu.crypto.service.strategy;


import org.nyu.crypto.service.Decryptor;
import org.nyu.crypto.service.KeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Service
public class HillClimber {

    @Autowired
    private Decryptor decryptor;

    @Autowired
    private Digrapher digrapher;

    @Autowired
    private KeyGenerator keyGenerator;


    public String climb(int[] ciphertext) {

        // initial random key guess before using hill climbing algorithm
        HashMap<String, ArrayList<Integer>> key = keyGenerator.generateKey();

        // initialize the dictionary digraph which we will use to compare
        double[][] dictionaryDigraph = digrapher.computeDictionaryDigraph();

        // we only need to compute the score of the dictionary digraph once
        double dictionaryScore = score(dictionaryDigraph);

        return climbHill(key, ciphertext, dictionaryScore);
    }

    private String climbHill(HashMap<String, ArrayList<Integer>> key, int[] ciphertext, double dictionaryScore) {

        // first we need to get the putative plaintext by decrypting the ciphertext with a random key
        String putativeText = decryptor.decrypt(key, ciphertext);

        // next we need to calculate the digraph of the putative plaintext
        double[][] putative = digrapher.computeDigraph(putativeText);

        // now we need to score the putative digraph matrix and compare it to the dictionary digraph matrix
        double putativeScore = score(putative);

        // if we are inside this loop, that means our putative key is not accurate
        while(Math.abs(dictionaryScore - putativeScore) > 1) {

        }

        return "";
    }


    // we calculate the score by taking the absolute value of the difference between the two matrices
    private double score(double[][] vector) {
        return Arrays.stream(vector)
                .flatMapToDouble(Arrays::stream)
                .sum();
    }

}
