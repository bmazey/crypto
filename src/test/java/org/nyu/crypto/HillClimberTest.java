package org.nyu.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.dto.Climb;
import org.nyu.crypto.dto.Simulation;
import org.nyu.crypto.service.KeyGenerator;
import org.nyu.crypto.service.Simulator;
import org.nyu.crypto.service.strategy.Digrapher;
import org.nyu.crypto.service.strategy.HillClimber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=CryptoApplication.class)
public class HillClimberTest {

    @Value("${space.value}")
    private int spaceval;

    @Autowired
    KeyGenerator keyGenerator;

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

        Climb climb = hillClimber.climb(simulation.getCiphertext(), digraph);
        String putative = climb.getPutative();

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

        Climb climb = hillClimber.climb(simulation.getCiphertext(), digraph);
        String putative = climb.getPutative();

        logger.info("putative : " + putative);
        logger.info("plaintext: " + simulation.getMessage());

        int score = 0;
        for (int i = 0; i < plaintext.length(); i++) {
            if (putative.charAt(i) == plaintext.charAt(i)) score++;
        }
        logger.info("score: " + score);
    }

    /**
     * TODO - write tests to compare key and putative key similarity!
     */
    @Test
    @SuppressWarnings("unchecked")
    public void validateClimbingKeyScorePlaintextDigraph() {

        int positive = 0;
        int negative = 0;

        for (int j = 0; j < 3; j++) {
            Simulation simulation = simulator.createSimulation();
            String plaintext = simulation.getMessage();

            // now compute plaintext digraph for our experiment
            double[][] digraph = digrapher.computeDictionaryDigraph();

            Climb climb = hillClimber.climb(simulation.getCiphertext(), digraph);
            String putative = climb.getPutative();

            HashMap<String, ArrayList<Integer>> key = mapper.convertValue(simulation.getKey(), HashMap.class);
            HashMap<String, ArrayList<Integer>> ikey = climb.getInitialKey();
            HashMap<String, ArrayList<Integer>> pkey = climb.getPutativeKey();

            //logger.info("initial key: ");
            //keyGenerator.printKey(ikey);

            //logger.info("putative key: ");
            //keyGenerator.printKey(pkey);


            // score of initial key guess against actual key
            int iscore = 0;
            for (String keyval : ikey.keySet()) {
                ArrayList<Integer> ilist = ikey.get(keyval);
                ArrayList<Integer> list = key.get(keyval);
                for (Integer i : ilist) {
                    if (list.contains(i)) {
                        //logger.info("matched <" + keyval + " : " + i + "> in initial");
                        iscore++;
                    }
                }
            }

            // score of putative key against actual key
            int pscore = 0;
            for (String keyval : pkey.keySet()) {
                ArrayList<Integer> plist = pkey.get(keyval);
                ArrayList<Integer> list = key.get(keyval);
                for (Integer i : plist) {
                    if (list.contains(i)) {
                        //logger.info("matched <" + keyval + " : " + i + "> in putative");
                        pscore++;
                    }
                }
            }

            // score of putative key should always be greater or equal to the initial key
            //logger.info("initial key score: " + iscore);
            //logger.info("putative key score: " + pscore);

            //logger.info("putative : " + putative);
            //logger.info("plaintext: " + plaintext);

            if (pscore > iscore) positive++;
            else negative++;
        }

        logger.info(positive + " | " + negative);
        assert positive > negative;
    }

    // TODO - write a test to measure which portions of the key are the most accurate.

}
