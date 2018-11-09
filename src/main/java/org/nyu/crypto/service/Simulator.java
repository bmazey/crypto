package org.nyu.crypto.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.nyu.crypto.dto.Ciphertext;
import org.nyu.crypto.dto.Key;
import org.nyu.crypto.dto.Message;
import org.nyu.crypto.dto.Simulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.springframework.cache.interceptor.SimpleKeyGenerator.generateKey;

@Service
public class Simulator {

    /**
     * we need a service for GET '/api/simulation'
     * this service should have a method that returns a Simulation dto.
     * we need to generate a key, generate a plaintext message, encrypt it to get the ciphertext, then build and return
     * our Simulation dto.
     */

    @Autowired
    private KeyGenerator keyGenerator;

    @Autowired
    private CiphertextGenerator ciphertextGenerator;

    @Autowired
    private MessageGenerator messageGenerator;

    @Autowired
    Encryptor encryptor;

    private ObjectMapper mapper;

    public Simulation[] createSimulationTexts() throws Exception{

        // Simulation of an array of 10
        Simulation[] simulations = new Simulation[10];

        // Use reflection to get the assign the key
        HashMap<String, ArrayList<Integer>> map = keyGenerator.generateKey();
        ObjectMapper objectMapper = new ObjectMapper();
        Key key = objectMapper.convertValue(map, Key.class);
        // For every simulation set the same key
        for (int loop = 0; loop < simulations.length;loop++) {
            simulations[loop] = new Simulation();
            simulations[loop].setKey(key);
            simulations[loop].setMessage(messageGenerator.generateMessage());
            //simulation.setCiphertext();
        }
        return simulations;
    }

    public Simulation createSimulation() {

        /**
         * We need to generate a key, generate a message, and then use both of those to create a ciphertext.
         * Then we build a Simulation DTO and return it.
         */

        // set up the base classes for our final Simulation DTO
        Simulation simulation = new Simulation();

        // create a new randomly generated key
        Key key = keyGenerator.generateKeyDto();
        simulation.setKey(key);

        // create a new randomly generated plaintext message
        String plaintext = messageGenerator.generateMessage();
        simulation.setMessage(plaintext);

        // use the key to encrypt the plaintext, generating ciphertext.
        int[] cipher = encryptor.encrypt(key, plaintext);
        simulation.setCiphertext(cipher);

        return simulation;
    }
}
