package org.nyu.crypto.service.strategy;

import org.apache.commons.lang3.ArrayUtils;
import org.nyu.crypto.service.Decryptor;
import org.nyu.crypto.service.DictionaryGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class Levenshteiner {

    @Autowired
    private DictionaryGenerator dictionaryGenerator;

    @Autowired
    private HillClimber hillClimber;

    @Autowired
    private Decryptor decryptor;

    private Random random = new Random();

    private Logger logger = LoggerFactory.getLogger(Levenshteiner.class);

    public HashMap<String, ArrayList<Integer>> distanceSwap(HashMap<String, ArrayList<Integer>> key,
                                                            int[] ciphertext) {
        // get the putative plaintext
        String text = decryptor.decrypt(key, ciphertext);

        // generate a list of words from the dictionary
        String[] words = dictionaryGenerator.generateDictionaryDto().getWords();

        // split the putative into "words"
        String[] putatives = text.split(" ");

        int min = Integer.MAX_VALUE;
        int size = 0;
        String original = "";
        String swap = "";

        for (String putative: putatives) {
            int currentSize = putative.length();
            for(String word: words) {
                if (word.length() != putative.length()) {
                    continue;
                }

                int i = calculate(putative, word);

                // FIXME - do we actually care about taking the largest all the time?
                if (i < min && currentSize > size) {
                    min = i;
                    size = currentSize;
                    swap = word;
                    original = putative;
                }
            }
        }

        logger.info("original putative: " + original + " | swap word: " + swap);

        int[] originalCiphers = Arrays.copyOfRange(ciphertext, text.indexOf(original), text.indexOf(original) + original.length());
        logger.info(text);
        logger.info(Arrays.toString(ciphertext));
        logger.info(original);
        logger.info(String.valueOf(text.indexOf(original)));
        logger.info(String.valueOf(text.lastIndexOf(original)));
        logger.info(Arrays.toString(originalCiphers));

        assert originalCiphers.length == original.length();


        // TODO - align by LCS and swap! don't forget to add space swap at beginning and end ...
        // crazy case here: don't give up keyspace values that are being mapped correctly!
        // ignore the space case, it's correct as far as we know
        for (int i = 0; i < original.length(); i++) {
            if (original.charAt(i) != swap.charAt(i)) {
                String originalLetter = Character.toString(original.charAt(i));
                String swapLetter = Character.toString(swap.charAt(i));

                // originalCiphers[i] is the value we need to give up, we want to find a value to trade for, but it can't
                // be mapped 'correctly' already
                Integer swapval = 0;
                ArrayList<Integer> plist = key.get(swapLetter);

                swapval = plist.get(random.nextInt(plist.size()));

                logger.info("trying to swap " + originalCiphers[i] + " from " + originalLetter + " and " + swapval + " from " + swapLetter);

                key = hillClimber.swap(key, originalLetter, swapLetter, originalCiphers[i], swapval);


                // FIXME!
                // duplicates in the original ciphers array could mess things up after swap, so replace them
                for(int n = 0; n < originalCiphers.length; n++) {
                    if (originalCiphers[n] == originalCiphers[i]) {
                        logger.info("updating ciphers at position " + n + ": " + originalCiphers[n] + " becoming " + swapval);
                        originalCiphers[n] = swapval;
                    }
                }
                logger.info("new ciphers: " + Arrays.toString(originalCiphers));
            }
        }

        String newputative = decryptor.decrypt(key, ciphertext);
        logger.info("new putative: " + newputative);
        // assert newputative.contains(swap);

        return key;
    }

    // call this at very end - find and replace all close matches
    public String generatePlaintext(String putative) {

        StringBuilder builder = new StringBuilder();
        String[] putatives = putative.split(" ");
        String[] words = dictionaryGenerator.generateDictionaryDto().getWords();
        String plaintext;

        int score;
        String chosenWord = "";
        String space = " ";

        for (int i = 0; i < putatives.length; i++){
            score = Integer.MAX_VALUE;
            for (int j = 0; j < words.length; j++){
                int temp = calculate(putatives[i], words[j]);
                if (temp < score){
                    score = temp;
                    chosenWord = words[j];
                }
            }
            builder.append(chosenWord);
            builder.append(space);
        }

        plaintext = builder.toString();

        return plaintext;
    }


    private int calculate(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }

    private int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }

}
