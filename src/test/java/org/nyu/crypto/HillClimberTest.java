package org.nyu.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.dto.Simulation;
import org.nyu.crypto.service.Simulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=CryptoApplication.class)
public class HillClimberTest {

    @Autowired
    private Simulator simulator;

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

    }
}
