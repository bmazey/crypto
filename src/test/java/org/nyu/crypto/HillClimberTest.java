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

    @Autowired
    private Simulator simulator;

    @Autowired
    private Decryptor decryptor;

    @Autowired
    private KeyGenerator keyGenerator;

    @Value("${key.space}")
    private int keyspace;

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

    private void climbHill(int[] ciphertext) {
        // generate a random key
        HashMap<String, ArrayList<Integer>> key = keyGenerator.generateKey();

        int[][] encrypted = new int[keyspace][keyspace];
        int[][] putative = new int[keyspace][keyspace];

        int[][] result = calculateAdjacency(encrypted, ciphertext);

        Stream.of(result).map(Arrays::toString).forEach(System.out::println);

    }

    // this method calculates the adjacency of sets of numbers
    private int[][] calculateAdjacency(int[][] encrypted, int[] ciphertext) {
        // we don't have to check the last value, so we stop at length - 1
        for (int i = 0; i < ciphertext.length - 1; i++) {
            encrypted[ciphertext[i]][ciphertext[i + 1]] = 1;
        }
        return encrypted;
    }
}
