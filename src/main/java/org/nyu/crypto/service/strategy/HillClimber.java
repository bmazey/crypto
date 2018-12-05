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

        /**
         * ok - it's late, but i actually had a flash of insight and want to implement tomorrow, along with heuristic
         * optimal key (a separate method to pre-optimize key)
         *
         * 1. compute and iterate over the ciphertext digraph
         * 2. for each element in the ciphertext digraph, find the closest value in the perfect plaintext digraph
         * 3. take the row # and column # (0 - 105) of the ciphertext digraph element, find what character in the
         *      putative key the row / column values are currently assigned to, and swap the ciphertext digraph row /
         *      columns values into the respective putative keyspaces
         *          *how do you know which ones to remove?* for all combinations of a and b, look up cipher[a][b] and take
         *          the row / columns of the lowest scoring values(least frequent)
         * 4. recompute the putative digraph and score it against the perfect plaintext digraph
         * 5. the new score is lower, keep the key; if it's higher, unswap and continue
         *
         */

        HashMap<String, ArrayList<Integer>> result = new HashMap<>();

        // TODO - is there a way to optimize an initial guess?
        // initial random key guess before using hill climbing algorithm
        HashMap<String, ArrayList<Integer>> key = keyGenerator.generateKey();

        // we now calculate the digraph matrix of the ciphertext which we only need once
        // double[][] cipher = digrapher.computeCipherDigraph(ciphertext);

        // initialize the dictionary digraph which we will use to compare
        double[][] dictionary = digrapher.computeDictionaryDigraph();

        // generate an initial putative plaintext with the random key and ciphertext
        String putativeText = decryptor.decrypt(key, ciphertext);

        // next we need to calculate the digraph of the putative plaintext
        double[][] putative = digrapher.computePutativeDigraph(putativeText);

        // calculate an initial score
        double score = score(dictionary, putative);

        // FIXME - define distance
        // TODO - this needs to be fixed ... we need to try multiple random keys!
        for (int i = 0; i < keyspace; i++) {

            // guess a new random key
            key = keyGenerator.generateKey();

            // invoke the climbing method with varying distances
            for (int j = 0; j < keyspace - i; j++) {
                key = climbHill(key, dictionary, ciphertext, j);
            }

            // test the new score
            putativeText = decryptor.decrypt(key, ciphertext);
            putative = digrapher.computePutativeDigraph(putativeText);

            // calculate an initial score
            double tscore = score(dictionary, putative);

            if (tscore < score) {
                score = tscore;
                result = key;
            }
        }

        logger.info("final score: " + score);
        return decryptor.decrypt(result, ciphertext);
    }

    public String climbExperiment(int[] ciphertext, double[][] plaintext) {

        HashMap<String, ArrayList<Integer>> result = new HashMap<>();

        // TODO - is there a way to optimize an initial guess?
        // initial random key guess before using hill climbing algorithm
        HashMap<String, ArrayList<Integer>> key = keyGenerator.generateKey();

        // we now calculate the digraph matrix of the ciphertext which we only need once
        // double[][] cipher = digrapher.computeCipherDigraph(ciphertext);

        // generate an initial putative plaintext with the random key and ciphertext
        String putativeText = decryptor.decrypt(key, ciphertext);

        // next we need to calculate the digraph of the putative plaintext
        double[][] putative = digrapher.computePutativeDigraph(putativeText);

        // calculate an initial score
        double score = score(plaintext, putative);
        logger.info("initial score: " + score);

        // FIXME - define distance
        // TODO - this needs to be fixed ... we need to try multiple random keys!
        for (int i = 0; i < 12; i++) {

            // guess a new random key
            key = keyGenerator.generateKey();

            // invoke the climbing method with varying distances
            for (int j = 1; j < keyspace; j++) {
                key = climbHill(key, plaintext, ciphertext, j);
            }

            // test the new score
            putativeText = decryptor.decrypt(key, ciphertext);
            putative = digrapher.computePutativeDigraph(putativeText);
            double tscore = score(plaintext, putative);

            if (tscore < score) {
                score = tscore;
                logger.info("updated score: " + score);
                result = key;
            }
            else {
                logger.info("retained score: " + score);
            }
        }

        logger.info("final score: " + score);
        return decryptor.decrypt(result, ciphertext);
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

        // if the two letters are the same, swapping will not affect the result
        if (a.equals(b)) return map;

        // assert that the lists contain the expected values
        ArrayList<Integer> alist = map.get(a);
        assert alist.contains(x);

        ArrayList<Integer> blist = map.get(b);
        assert blist.contains(y);

        // perform the swap ...
        alist.remove(x);
        alist.add(y);
        map.put(a, alist);

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
