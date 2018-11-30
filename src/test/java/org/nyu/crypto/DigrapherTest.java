package org.nyu.crypto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.service.Decryptor;
import org.nyu.crypto.service.KeyGenerator;
import org.nyu.crypto.service.Simulator;
import org.nyu.crypto.service.strategy.Digrapher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CryptoApplication.class)
public class DigrapherTest {

    @Value("${key.space}")
    private int keyspace;

    @Autowired
    private Digrapher digrapher;

    @Autowired
    private Decryptor decryptor;

    @Autowired
    private KeyGenerator keyGenerator;

    @Autowired
    private Simulator simulator;

    private Logger logger = LoggerFactory.getLogger(DigrapherTest.class);

    private final double TOTAL = 100.0;

    @Test
    public void generateDigraph() {
        double[][] digraph = digrapher.computeDictionaryDigraph();
        Stream.of(digraph).map(Arrays::toString).forEach(System.out::println);

        // TODO - make sure result adds up to 100%.
        double sum = 0;
        for (int i = 0; i < digraph.length; i++) {
            for (int j = 0; j < digraph[i].length; j++) {
                sum += digraph[i][j];
            }
        }

        // sum should add up to 100
        assert sum == TOTAL;
    }

    @Test
    public void compareDictionaryDigraphToPutativeDigraph() {

        /**
         * our dictionary digraph matrix should be 26 x 26 and our putative digraph matrix should be 27 x 27
         * this is because we include the space values in the putative digraph computation but IGNORE them
         * in the dictionary digraph computation.
         *
         * this test asserts that the dimensions of the putative matrix are one greater than the dictionary matrix
         */

        // let's start by computing the dictionary digraph
        double[][] dictionary = digrapher.computeDictionaryDigraph();
        Stream.of(dictionary).map(Arrays::toString).forEach(System.out::println);

        // now we'll create some putative plaintext and compute the digraph
        // start by generating a random key
        HashMap<String, ArrayList<Integer>> key = keyGenerator.generateKey();

        // now we'll get ciphertext from a simulation
        int[] ciphertext = simulator.createSimulation().getCiphertext();

        // we get putative plaintext by decrypting the ciphertext with our random mis-matched key
        String putativePlaintext = decryptor.decrypt(key, ciphertext);

        // finally we compute the putative digraph
        double[][] putative = digrapher.computePutativeDigraph(putativePlaintext);

        logger.info("dictionary: " + dictionary.length + " | " + "putative: " + putative.length);

        // assert that their dimensions are within 1
        assert dictionary.length == putative.length - 1;

    }

    @Test
    public void assertCiphertextDigraphLength(){

        // generating a new ciphertext
        int[] ciphertext = simulator.createSimulation().getCiphertext();

        // computing ciphertext digraph
        double[][] cipherMatrix = digrapher.computeCipherDigraph(ciphertext);

        logger.info("Ciphertext Matrix Length: " + cipherMatrix.length);

        // asserting that its dimensions are 106 x 106
        assert cipherMatrix.length == keyspace;
    }
}
