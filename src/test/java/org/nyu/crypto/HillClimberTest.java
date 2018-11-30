package org.nyu.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.dto.Simulation;
import org.nyu.crypto.service.Decryptor;
import org.nyu.crypto.service.KeyGenerator;
import org.nyu.crypto.service.Simulator;
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
public class HillClimberTest {

    @Value("${key.space}")
    private int keyspace;

    @Value("${charset.length}")
    private int charset;

    @Value("${space.value}")
    private int spaceval;

    @Autowired
    private Simulator simulator;

    @Autowired
    private Decryptor decryptor;

    @Autowired
    private KeyGenerator keyGenerator;

    private ObjectMapper mapper = new ObjectMapper();

    private Logger logger = LoggerFactory.getLogger(HillClimberTest.class);

    @Test
    @SuppressWarnings("unchecked")
    public void simulateHillClimbing() {

        Simulation simulation = simulator.createSimulation();

        // unpack the contents into key, plaintext, ciphertext
        HashMap<String, ArrayList<Integer>> key = mapper.convertValue(simulation.getKey(), HashMap.class);
        String plaintext = simulation.getMessage();
        int[] ciphertext = simulation.getCiphertext();

        logger.info(plaintext);
        logger.info(Arrays.toString(ciphertext));

        // let's invoke the hill climbing function
        climbHill(ciphertext);


    }

    // TODO - move this to service!
    private void climbHill(int[] ciphertext) {
        // generate a random key
        HashMap<String, ArrayList<Integer>> key = keyGenerator.generateKey();

        int[][] encrypted = new int[keyspace][keyspace];
        int[][] putative = new int[charset][charset];

        encrypted = calculateCipherAdjacency(encrypted, ciphertext);
        //Stream.of(encrypted).map(Arrays::toString).forEach(System.out::println);

        // attempt to decrypt the ciphertext with a random key to get a putative plaintext
        String text = decryptor.decrypt(key, ciphertext);
        logger.info(text);

        putative = calculatePutativeAdjacency(putative, text);
        Stream.of(putative).map(Arrays::toString).forEach(System.out::println);

        logger.info(score(encrypted) + " / " + score(putative));

    }

    // TODO - do we even need this?
    // this method calculates the adjacency of numbers within ciphertext
    private int[][] calculateCipherAdjacency(int[][] encrypted, int[] ciphertext) {
        // we don't have to check the last value, so we stop at length - 1
        for (int i = 0; i < ciphertext.length - 1; i++) {
            encrypted[ciphertext[i]][ciphertext[i + 1]] += 1;
        }
        return encrypted;
    }

    // this method calculates the adjacency of letters in a putative plaintext
    private int[][] calculatePutativeAdjacency(int[][] putative, String text) {
        // again, no need to check the last value
        for (int i = 0; i < text.length() - 1; i++) {
            putative[convert(text.charAt(i))][convert(text.charAt(i + 1))] += 1;
        }
        return putative;
    }

    // this method converts chars to ints so we can use character-based indexing in the putative array
    private int convert(char c) {
        if (c == ' ') return spaceval;
        else return (c - 'a');
    }

    private int score(int[][] vector) {
        return Arrays.stream(vector)
                .flatMapToInt(Arrays::stream)
                .sum();
    }
}
