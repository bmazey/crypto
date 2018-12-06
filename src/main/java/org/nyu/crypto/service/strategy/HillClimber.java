package org.nyu.crypto.service.strategy;


import org.nyu.crypto.service.Decryptor;
import org.nyu.crypto.service.FrequencyGenerator;
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
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

@Service
public class HillClimber {

    @Value("${key.space}")
    private int keyspace;

    @Autowired
    private Decryptor decryptor;

    @Autowired
    private Digrapher digrapher;

    @Autowired
    private FrequencyGenerator frequencyGenerator;

    @Autowired
    private KeyGenerator keyGenerator;

    private final String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
                                        "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "space"};

    private Logger logger = LoggerFactory.getLogger(HillClimber.class);


    public String climb(int[] ciphertext) {

        /**
         * ok - it's late, but i actually had a flash of insight and want to implement tomorrow, along with heuristic
         * optimal key (a separate method to pre-optimize key)
         *
         * 1. compute and iterate over the ciphertext digraph
         * 2. for each element in the ciphertext digraph, find the closest element in the perfect plaintext digraph
         * 3. take the row # and column # (0 - 105) of the ciphertext digraph element, find what characters in the
         *      putative key the row / column values are currently assigned to, and swap the ciphertext digraph row /
         *      columns values into the respective putative keyspaces
         *          *how do you know which ones to give up?* for all combinations of a.list and b.list, look up
         *          cipher[a][b] and take the row / column # of the lowest scoring value (least frequent)
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

    public String climb2(int[] ciphertext, double[][] plaintext) {

        // TODO - apply optimal heuristic key guess strategy as well
        // start by generating a random key
        HashMap<String, ArrayList<Integer>> key = keyGenerator.generateKey();

        for (String keyval : key.keySet()) {
            ArrayList<Integer> list = key.get(keyval);
            System.out.println(keyval + " : " + Arrays.toString(list.toArray()));
        }

        // compute ciphertext digraph
        double[][] cipher = digrapher.computeCipherDigraph(ciphertext);

        key = climbHill2(key, plaintext, cipher, ciphertext);

        return decryptor.decrypt(key, ciphertext);
    }

    private HashMap<String, ArrayList<Integer>> climbHill2(HashMap<String, ArrayList<Integer>> key,
                                                           double[][] plaintext, double[][] cipher, int[] ciphertext) {
        // we start by computing the putative digraph
        String text = decryptor.decrypt(key, ciphertext);
        double[][] putative = digrapher.computePutativeDigraph(text);

        //compute our initial score
        double score = score(plaintext, putative);

        // next we iterate over the ciphertext digraph to find the closest % match to the plaintext digraph
        for (int i = 0; i < cipher.length; i++) {
            for (int j = 0; j < cipher[i].length; j++) {

                // if the cell is 0, that means these two numbers never show up next to each other,
                // there's nothing we can do
                if(cipher[i][j] == 0) continue;

                // initialize an optimal compare score
                double subscore = Double.MAX_VALUE;
                int cipherrow = 0;
                int ciphercolumn = 0;

                int kval = 0;
                int nval = 0;

                String kletter = "a";
                String nletter = "a";

                // inner nested loop to iterate over plaintext digraph
                for (int k = 0; k < plaintext.length; k ++) {
                    for (int n = 0; n < plaintext[k].length; n++) {
                        double current = Math.abs(plaintext[k][n] - cipher[i][j]);
                        if (current <= subscore) {
                            subscore = current;
                            cipherrow = i;
                            ciphercolumn = j;

                            kval = k;
                            nval = n;

                            kletter = convert(k);
                            nletter = convert(n);
                        }
                    }
                }

                // now we have a cipher digraph row # and column # of the most similar plaintext digraph occurrence
                // k letter and n letter taken from the perfect digraph 'should' share these keys in their corresponding keyspaces
                // get the current corresponding letter values in the putative key - these are the letters that are
                // currently holding the numbers (cipherrow and ciphercolumn) we want to swap with k and n
                String fletter = getLetterAssociation(key, cipherrow).get();

                // now we need to find the numbers in the k and n letters' keyspaces which are causing the most
                // inaccurate scores ... we want to give those numbers up in exchange
                // kletter number <-> fletter number / nletter number <-> sletter number

                // reset subscore to 0
                subscore = 0;
                Integer kswapval = 0;

                ArrayList<Integer> klist = key.get(kletter);
                for (int w : klist) {
                    // TODO - this might not be the best way to calculate the bad score
                    double current = Math.abs(Arrays.stream(cipher[w]).sum() - Arrays.stream(plaintext[kval]).sum());
                    if (current >= subscore) {
                        subscore = current;
                        kswapval = w;
                    }
                }

                key = swap(key, kletter, fletter, kswapval, cipherrow);

                String sletter = getLetterAssociation(key, ciphercolumn).get();

                // reset subscore to 0
                subscore = 0;
                Integer nswapval = 0;

                ArrayList<Integer> nlist = key.get(nletter);
                for (int x : nlist) {
                    // TODO - this might not be the best way to calculate the bad score
                    double current = Math.abs(Arrays.stream(cipher[x]).sum() - Arrays.stream(plaintext[nval]).sum());
                    if (current >= subscore) {
                        subscore = current;
                        nswapval = x;
                    }
                }

                logger.info("kletter: " + kletter + " kswapval: " + kswapval + " | fletter: " + fletter + " cipherrow: "
                        + cipherrow);

                logger.info("nletter: " + nletter + " nswapval: " + nswapval + " | sletter: " + sletter + " ciphercolumn: "
                        + ciphercolumn);

                key = swap(key, nletter, sletter, nswapval, ciphercolumn);

                text = decryptor.decrypt(key, ciphertext);
                double[][] nputative = digrapher.computePutativeDigraph(text);

                double current = score(plaintext, nputative);

                // this is the bad case - our swaps have moved us away from the 'ideal' solution
                if (current > score) {
                    key = swap(key, nletter, sletter, ciphercolumn, nswapval);
                    key = swap(key, kletter, fletter, cipherrow, kswapval);
                    continue;
                }

                // FIXME - update putative?

                score = current;
            }
        }

        // TODO - move this to keygenerator as printKey() method;
        for (String keyval : key.keySet()) {
            ArrayList<Integer> list = key.get(keyval);
            System.out.println(keyval + " : " + Arrays.toString(list.toArray()));
        }

        return key;
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

        // can't swap a number with itself
        if (x.intValue() == y.intValue()) return map;

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

    // TODO - check this to make sure it's converting correctly
    private String convert(int i) {
        assert i <= alphabet.length;
        return alphabet[i];
    }

}
