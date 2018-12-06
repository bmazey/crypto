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

    //@Test
    @SuppressWarnings("unchecked")
    public void simulateHillClimbing() {
        Simulation simulation = simulator.createSimulation();
        logger.info("plaintext: " + simulation.getMessage());
        logger.info("putative : " + hillClimber.climb(simulation.getCiphertext()));
    }

    //@Test
    @SuppressWarnings("unchecked")
    public void simulateExperimentalHillClimbing() {
        Simulation simulation = simulator.createSimulation();
        logger.info("plaintext: " + simulation.getMessage());

        // now compute plaintext digraph for our experiment
        double[][] plaintext = digrapher.computePutativeDigraph(simulation.getMessage());

        logger.info("putative : " + hillClimber.climbExperiment(simulation.getCiphertext(), plaintext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void simulateHillClimbing2() {
        Simulation simulation = simulator.createSimulation();
        logger.info("plaintext: " + simulation.getMessage());

        // now compute plaintext digraph for our experiment
        double[][] plaintext = digrapher.computePutativeDigraph(simulation.getMessage());
        logger.info("putative : " + hillClimber.climb2(simulation.getCiphertext(), plaintext));
    }

}
