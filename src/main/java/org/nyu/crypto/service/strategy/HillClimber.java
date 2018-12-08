package org.nyu.crypto.service.strategy;


import org.apache.commons.lang3.SerializationUtils;
import org.nyu.crypto.dto.Climb;
import org.nyu.crypto.service.Decryptor;
import org.nyu.crypto.service.FrequencyGenerator;
import org.nyu.crypto.service.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class HillClimber {

    @Value("${key.space}")
    private int keyspace;

    @Value("${charset.length}")
    private int charset;

    @Autowired
    private Decryptor decryptor;

    @Autowired
    private Digrapher digrapher;

    @Autowired
    private FrequencyGenerator frequencyGenerator;

    @Autowired
    private KeyGenerator keyGenerator;

    @Autowired
    private Levenshteiner levenshteiner;

    private final String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
                                        "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "space"};

    private Random random = new Random();

    private Logger logger = LoggerFactory.getLogger(HillClimber.class);

    /**
     * ok - it's late, but i actually had a flash of insight and want to implement tomorrow, along with heuristic
     * optimal key (a separate method to pre-optimize key)
     *
     * 1. compute and iterate over the ciphertext digraph
     * 2. for each element in the ciphertext digraph, find the closest element in the perfect dictionary digraph
     * 3. take the row # and column # (0 - 105) of the ciphertext digraph element, find what characters in the
     *      putative key the row / column values are currently assigned to, and swap the ciphertext digraph row /
     *      columns values into the respective putative keyspaces
     *          *how do you know which ones to give up?* for all combinations of a.list and b.list, look up
     *          cipher[a][b] and take the row / column # of the lowest scoring value (least frequent)
     * 4. recompute the putative digraph and score it against the perfect dictionary digraph
     * 5. the new score is lower, keep the key; if it's higher, unswap and continue
     *
     */

    public Climb climb(int[] ciphertext, double[][] dictionary) {

        Climb climb = new Climb();
        climb.setCiphertext(ciphertext);

        // TODO - apply optimal heuristic key guess strategy as well
        // start by generating a random key
        HashMap<String, ArrayList<Integer>> key = keyGenerator.generateKey();
        climb.setInitialKey(key);

        // logger.info("initial key: ");
        // keyGenerator.printKey(key);

        // compute ciphertext digraph
        // double[][] cipher = digrapher.computeCipherDigraph(ciphertext);

        // create a deep copy
        HashMap<String, ArrayList<Integer>> result = SerializationUtils.clone(key);


        result = climbHill(result, dictionary, ciphertext);

        // TODO - finish this!
        levenshteiner.distanceSwap(result, ciphertext);

        // build Climb dto
        climb.setPutativeKey(result);
        climb.setPutative(decryptor.decrypt(result, ciphertext));
        return climb;
    }

    private HashMap<String, ArrayList<Integer>> climbHill(HashMap<String, ArrayList<Integer>> key,
                                                          double[][] dictionary, int[] ciphertext) {

        // we start by computing the initial putative digraph
        String putativeText = decryptor.decrypt(key, ciphertext);
        double[][] putative = digrapher.computePutativeDigraph(putativeText);

        //compute our initial score
        double score = score(dictionary, putative);

        // set current to zero
        double current = 0;

        // common letters and numbers
        String commonletter = "";
        Integer common = 0;

        // rare letters and numbers
        String rareletter = "";
        Integer rare = 0;

        while (current < score) {

            double max = Double.MIN_VALUE;
            int maxrow = 0;
            int maxcolumn = 0;

            double min = Double.MAX_VALUE;
            int minrow = 0;
            int mincolumn = 0;

            // TODO - store additional state here! :)

            // next we iterate over the putative digraph to find the biggest % match diff to the dictionary digraph
            for (int i = 0; i < putative.length; i++) {
                for (int j = 0; j < putative[i].length; j++) {
                    double value = dictionary[i][j] - putative[i][j];

                    // this putative digraph element is the most over-represented compared to the dictionary digraph
                    if (value > max) {
                        max = value;
                        maxrow = i;
                        maxcolumn = j;
                    }

                    // this putative digraph element is the most under-represented compared to the dictionary digraph
                    if (value < min) {
                        min = value;
                        minrow = i;
                        mincolumn = j;
                    }

                }
            }

            // now we need to find an element to swap - take the abs diff of most commonly occurring row & column in the
            // putative and dictionary respectively
            double maxrowdiff = Math.abs(DoubleStream.of(dictionary[maxrow]).sum() - DoubleStream.of(putative[maxrow]).sum());
            double maxcolumndiff = Math.abs(DoubleStream.of(dictionary[maxcolumn]).sum() - DoubleStream.of(putative[maxcolumn]).sum());

            // this means that, on average, the first letter in the putative digraph is causing the most deviance
            if (maxrowdiff > maxcolumndiff) {
                commonletter = convert(maxrow);
                // find the most commonly occurring number in the keyspace as compared to the ciphertext
                int maxcount = 0;
                ArrayList<Integer> list = key.get(commonletter);
                for (Integer x : list) {
                    int count = 0;
                    for (int y : ciphertext) {
                        if (x == y) count++;
                    }
                    if (count > maxcount) {
                        maxcount = count;
                        common = x;
                    }
                }
                // now we have the most commonly appearing integer is the most over-represented (first) letter of the putative digraph
            }

            // this must mean that the second letter is over-represented
            else {
                commonletter = convert(maxcolumn);
                // find the most commonly occurring number in the keyspace as compared to the ciphertext
                int maxcount = 0;
                ArrayList<Integer> list = key.get(commonletter);
                for (Integer x : list) {
                    int count = 0;
                    for (int y : ciphertext) {
                        if (x == y) count++;
                    }
                    if (count > maxcount) {
                        maxcount = count;
                        common = x;
                    }
                }
                // now we have the most commonly appearing integer is the most over-represented (second) letter of the putative digraph
            }

            // now we do a similar strategy for the min
            double minrowdiff = Math.abs(DoubleStream.of(dictionary[minrow]).sum() - DoubleStream.of(putative[minrow]).sum());
            double mincolumndiff = Math.abs(DoubleStream.of(dictionary[mincolumn]).sum() - DoubleStream.of(putative[mincolumn]).sum());



            // this means that, on average, the first letter in the putative digraph is causing the most deviance
            if (minrowdiff > mincolumndiff) {
                rareletter = convert(minrow);
                // find the least commonly occurring number in the keyspace as compared to the ciphertext
                int mincount = Integer.MAX_VALUE;
                ArrayList<Integer> list = key.get(rareletter);
                for (Integer x : list) {
                    int count = 0;
                    for (int y : ciphertext) {
                        if (x == y) count++;
                    }
                    if (count < mincount) {
                        mincount = count;
                        rare = x;
                    }
                }
                // now we have the least commonly appearing integer is the most under-represented (first) letter of the putative digraph
            }

            // this must mean that the second letter is under-represented
            else {
                rareletter = convert(mincolumn);
                // find the least commonly occurring number in the keyspace as compared to the ciphertext
                int mincount = Integer.MAX_VALUE;
                ArrayList<Integer> list = key.get(rareletter);
                for (Integer x : list) {
                    int count = 0;
                    for (int y : ciphertext) {
                        if (x == y) count++;
                    }
                    if (count < mincount) {
                        mincount = count;
                        rare = x;
                    }
                }
                // now we have the least commonly appearing integer is the most under-represented (second) letter of the putative digraph
            }

            // swap the most commonly occurring key in the max digraph element keyspaces with the least occurring element
            key = swap(key, commonletter, rareletter, common, rare);

            // compute the new score
            putativeText = decryptor.decrypt(key, ciphertext);
            putative = digrapher.computePutativeDigraph(putativeText);
            current = score(dictionary, putative);

            logger.info("current: " + current + " | score: " + score);
            if (current < score) {
                score = current;
            }

            logger.info("successful swap!");
        }

        logger.info("un-swap!");
        key = swap(key, commonletter, rareletter, rare, common);

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

    private String convert(int i) {
        assert i <= alphabet.length;
        return alphabet[i];
    }

}
