package org.nyu.crypto.service.strategy;


import org.nyu.crypto.service.Decryptor;
import org.nyu.crypto.service.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

@Service
public class HillClimber {

    @Value("${key.space}")
    private int keyspace;

    @Autowired
    private Decryptor decryptor;

    @Autowired
    private Digrapher digrapher;

    @Autowired
    private KeyGenerator keyGenerator;

    private Logger logger = LoggerFactory.getLogger(HillClimber.class);


    public String climb(int[] ciphertext) {

        // TODO - is there a way to optimize an initial guess?
        // initial random key guess before using hill climbing algorithm
        HashMap<String, ArrayList<Integer>> key = keyGenerator.generateKey();

        // for testing
        for (String tkey: key.keySet()) {
            ArrayList<Integer> list = key.get(tkey);
            logger.info(tkey + " : " + Arrays.toString(list.toArray()));
        }

        // we now calculate the digraph matrix of the ciphertext which we only need once
        double[][] cipher = digrapher.computeCipherDigraph(ciphertext);

        // initialize the dictionary digraph which we will use to compare
        double[][] dictionary = digrapher.computeDictionaryDigraph();

        // FIXME - define distance
        // TODO - this needs to be fixed
        for (int i = 0; i < keyspace; i++) {
            for (int j = 0; j < keyspace - i; j++) {
                key = climbHill(key, dictionary, ciphertext, j);
            }
        }

        // for testing
        for (String tkey: key.keySet()) {
            ArrayList<Integer> list = key.get(tkey);
            logger.info(tkey + " : " + Arrays.toString(list.toArray()));
        }

        return decryptor.decrypt(key, ciphertext);
    }

    private HashMap<String, ArrayList<Integer>> climbHill(HashMap<String, ArrayList<Integer>> pkey, double[][] dictionary,
                             int[] ciphertext, int distance) {

        // first we need to get the putative plaintext by decrypting the ciphertext with a random key
        String putativeText = decryptor.decrypt(pkey, ciphertext);

        // next we need to calculate the digraph of the putative plaintext
        double[][] putative = digrapher.computePutativeDigraph(putativeText);

        // now we need to score the putative digraph matrix and compare it to the dictionary digraph matrix
        double score = score(dictionary, putative);
        //logger.info("score: " + score);

        // FIXME - this isn't right!
        for (int i = 0; i < keyspace - distance; i++) {
            String firstLetter = getLetterAssociation(pkey, i).get();
            String secondLetter = getLetterAssociation(pkey, i + distance).get();
            pkey = swap(pkey, firstLetter, secondLetter, i, i + distance);

            // now we need to recompute the new putative digraph score
            String tPutativeText = decryptor.decrypt(pkey, ciphertext);
            double[][] tputative = digrapher.computePutativeDigraph(tPutativeText);
            double tscore = score(dictionary, tputative);

            // if our new score is greater, we've moved away from the solution ... unswap and continue
            if (tscore > score) {
                pkey = swap(pkey, secondLetter, firstLetter, i, i + distance);
                continue;
            }
            score = tscore;
        }

        return pkey;
    }

    // we use a key to track associations in the digraph matrix
    private Optional<String> getLetterAssociation(HashMap<String, ArrayList<Integer>> map, Integer x) {
        for (String key : map.keySet()) {
            ArrayList<Integer> list = map.get(key);
            if (list.contains(x)) return Optional.of(key);
        }
        return Optional.empty();
    }

    // given two numbers and two letters, swap the keyspace a <-> x and b <-> y
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

    // method to score the abs val difference
    // public for testing purposes
    public double score(double[][] dictionary, double[][] putative) {
        double score = 0;
        for(int i = 0; i < dictionary.length; i++) {
            for (int j = 0; j < dictionary[i].length; j++) {
                score += Math.abs(dictionary[i][j] - putative[i][j]);
            }
        }
        return score;
    }

}
