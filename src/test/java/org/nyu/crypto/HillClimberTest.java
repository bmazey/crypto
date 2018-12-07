package org.nyu.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.dto.Simulation;
import org.nyu.crypto.service.Simulator;
import org.nyu.crypto.service.strategy.Digrapher;
import org.nyu.crypto.service.strategy.HillClimber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=CryptoApplication.class)
public class HillClimberTest {

    @Value("${space.value}")
    private int spaceval;

    @Autowired
    private Simulator simulator;

    @Autowired
    private HillClimber hillClimber;

    @Autowired
    private Digrapher digrapher;

    private ObjectMapper mapper = new ObjectMapper();

    private Logger logger = LoggerFactory.getLogger(HillClimberTest.class);

    // TODO - change the digraph generation from dictionary to randomly generated messages!

    @Test
    @SuppressWarnings("unchecked")
    public void simulateHillClimbingPlaintextDigraph() {
        Simulation simulation = simulator.createSimulation();

        // now compute plaintext digraph for our experiment
        double[][] digraph = digrapher.computePutativeDigraph(simulation.getMessage());

        String plaintext = simulation.getMessage();
        String putative = hillClimber.climb(simulation.getCiphertext(), digraph);

        logger.info("putative : " + putative);
        logger.info("plaintext: " + simulation.getMessage());

        int score = 0;
        for (int i = 0; i < plaintext.length(); i++) {
            if (putative.charAt(i) == plaintext.charAt(i)) score++;
        }
        logger.info("score: " + score);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void simulateHillClimbingDictionaryDigraph() {
        Simulation simulation = simulator.createSimulation();

        // now compute plaintext digraph for our experiment
        double[][] digraph = digrapher.computeDictionaryDigraph();

        String plaintext = simulation.getMessage();
        String putative = hillClimber.climb(simulation.getCiphertext(), digraph);

        int score = 0;

        while (score < 100) {

            score = 0;

            for (int i = 0; i < plaintext.length(); i++) {
                if (putative.charAt(i) == plaintext.charAt(i)) score++;
            }

            logger.info("score: " + score);
        }

        logger.info("putative : " + putative);
        logger.info("plaintext: " + simulation.getMessage());
    }

}
