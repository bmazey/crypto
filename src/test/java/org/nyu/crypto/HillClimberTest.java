package org.nyu.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.dto.Simulation;
import org.nyu.crypto.service.Decryptor;
import org.nyu.crypto.service.KeyGenerator;
import org.nyu.crypto.service.Simulator;
import org.nyu.crypto.service.strategy.HillClimber;
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

    @Value("${space.value}")
    private int spaceval;

    @Autowired
    private Simulator simulator;

    @Autowired
    private HillClimber hillClimber;

    private ObjectMapper mapper = new ObjectMapper();

    private Logger logger = LoggerFactory.getLogger(HillClimberTest.class);

    @Test
    @SuppressWarnings("unchecked")
    public void simulateHillClimbing() {

        Simulation simulation = simulator.createSimulation();
        logger.info("plaintext: " + simulation.getMessage());
        logger.info("putative : " + hillClimber.climb(simulation.getCiphertext()));
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
