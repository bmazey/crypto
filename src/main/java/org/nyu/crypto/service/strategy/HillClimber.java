package org.nyu.crypto.service.strategy;


import org.nyu.crypto.service.Decryptor;
import org.nyu.crypto.service.KeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

@Service
public class HillClimber {

    @Autowired
    private Decryptor decryptor;

    @Autowired
    private Digrapher digrapher;

    @Autowired
    private KeyGenerator keyGenerator;


    public String climb(int[] ciphertext) {

        // TODO - is there a way to optimize an initial guess?
        // initial random key guess before using hill climbing algorithm
        HashMap<String, ArrayList<Integer>> key = keyGenerator.generateKey();

        // we now calculate the digraph matrix of the ciphertext which we only need once
        double[][] cipher = digrapher.computeCipherDigraph(ciphertext);

        // initialize the dictionary digraph which we will use to compare
        double[][] dictionary = digrapher.computeDictionaryDigraph();

        // we only need to compute the score of the dictionary digraph once
        double dictionaryScore = score(dictionary);

        return climbHill(key, cipher, ciphertext, dictionaryScore);
    }

    private String climbHill(HashMap<String, ArrayList<Integer>> key, double[][] cipher,
                             int[] ciphertext, double dictionaryScore) {

        // first we need to get the putative plaintext by decrypting the ciphertext with a random key
        String putativeText = decryptor.decrypt(key, ciphertext);

        // next we need to calculate the digraph of the putative plaintext
        double[][] putative = digrapher.computePutativeDigraph(putativeText);

        // now we need to score the putative digraph matrix and compare it to the dictionary digraph matrix
        double putativeScore = score(putative);

        // if we are inside this loop, that means our putative key is not accurate enough
        while(Math.abs(dictionaryScore - putativeScore) > 1) {

        }

        return "";
    }

    // we use a key to track associations in the digraph matrix
    private Optional<String> getLetterAssociation(HashMap<String, ArrayList<Integer>> map, Integer x) {
        for (String key : map.keySet()) {
            ArrayList<Integer> list = map.get(key);
            if (list.contains(x)) return Optional.of(key);
        }
        return Optional.empty();
    }

    // given two numbers and two letters, swap the keyspace a <-> and b <-> y
    private HashMap<String, ArrayList<Integer>> swap(HashMap<String, ArrayList<Integer>> map,
                                                     String a, String b, Integer x, Integer y) {
        // assert that the lists contain the expected values
        ArrayList<Integer> alist = map.get(a);
        assert alist.contains(x);

        alist.remove(x);
        alist.add(y);
        map.put(a, alist);

        ArrayList<Integer> blist = map.get(b);
        assert blist.contains(y);

        blist.remove(y);
        blist.add(x);
        map.put(b, blist);

        return map;
    }

    // helper method to sum a matrix
    private double score(double[][] vector) {
        return Arrays.stream(vector)
                .flatMapToDouble(Arrays::stream)
                .sum();
    }

}
