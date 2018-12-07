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

    public String climb(int[] ciphertext, double[][] plaintext) {

        // TODO - apply optimal heuristic key guess strategy as well
        // start by generating a random key
        HashMap<String, ArrayList<Integer>> key = keyGenerator.generateKey();

        for (String keyval : key.keySet()) {
            ArrayList<Integer> list = key.get(keyval);
            System.out.println(keyval + " : " + Arrays.toString(list.toArray()));
        }

        // compute ciphertext digraph
        double[][] cipher = digrapher.computeCipherDigraph(ciphertext);

        key = climbHill(key, plaintext, cipher, ciphertext);

        return decryptor.decrypt(key, ciphertext);
    }

    private HashMap<String, ArrayList<Integer>> climbHill(HashMap<String, ArrayList<Integer>> key,
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
                // if(cipher[i][j] == 0) continue;

                // initialize an optimal compare score
                double subscore = Double.MAX_VALUE;
                int cipherrow = 0;
                int ciphercolumn = 0;

                int kval = 0;
                int nval = 0;

                String kletter = "";
                String nletter = "";

                // inner nested loop to iterate over plaintext digraph
                for (int k = 0; k < plaintext.length; k ++) {
                    for (int n = 0; n < plaintext[k].length; n++) {
                        // FIXME - this is probably oversimplified
                        double current = Math.abs(plaintext[k][n] - cipher[i][j]);
                        if (current < subscore) {
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
                    // FIXME - this might not be the best way to calculate the bad score
                    double current = Math.abs(Arrays.stream(cipher[w]).sum() - Arrays.stream(putative[kval]).sum());
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
                    // FIXME - this might not be the best way to calculate the bad score
                    double current = Math.abs(Arrays.stream(cipher[x]).sum() - Arrays.stream(putative[nval]).sum());
                    if (current >= subscore) {
                        subscore = current;
                        nswapval = x;
                    }
                }

                key = swap(key, nletter, sletter, nswapval, ciphercolumn);

                logger.info(kletter + " : " + kswapval + " <-> " + fletter + " : " + cipherrow);

                logger.info(nletter + " : " + nswapval + " <-> " + sletter + " : " + ciphercolumn);

                text = decryptor.decrypt(key, ciphertext);
                double[][] newputative = digrapher.computePutativeDigraph(text);

                double current = score(plaintext, newputative);

                // this is the bad case we want our matrices to be very similar - our swaps have moved us away
                // from the 'ideal' solution ... un-swap
                if (current >= score) {
                    key = swap(key, nletter, sletter, ciphercolumn, nswapval);
                    key = swap(key, kletter, fletter, cipherrow, kswapval);
                    continue;
                }

                putative = newputative;
                score = current;
                logger.info("updated putative score: " + score);
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

    private String convert(int i) {
        assert i <= alphabet.length;
        return alphabet[i];
    }

}
