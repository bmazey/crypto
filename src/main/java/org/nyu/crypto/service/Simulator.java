package org.nyu.crypto.service;

import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.nyu.crypto.dto.Key;
import org.nyu.crypto.dto.Simulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Service
public class Simulator {

    /**
     * we need a service for GET '/api/simulation'
     * this service should have a method that returns a Simulation dto.
     * we need to generate a key, generate a plaintext message, encrypt it to get the ciphertext, then build and return
     * our Simluation dto.
     */

    @Autowired
    private KeyGenerator keyGenerator;

    @Autowired
    private CiphertextGenerator ciphertextGenerator;

    @Autowired
    private MessageGenerator messageGenerator;

    public Simulation[] createSimulationTexts() throws Exception{

        // Simulation of an array of 10
        Simulation[] simulations = new Simulation[10];

        // Use reflection to get the assign the key
        Class cls = Class.forName("org.nyu.crypto.dto.Key");
        Object keyObject = cls.newInstance();
        HashMap<String, ArrayList<Integer>> map = keyGenerator.generateKey();
        for (String s: map.keySet()) {
            int length = map.get(s).size();
            if (s.equalsIgnoreCase("space")) {
                s = "Space";
            } else {
                s = s.toUpperCase();
            }
            try{
                Method method = cls.getDeclaredMethod("set"+s, int[].class);
                method.invoke(keyObject, map.get(s).toArray(new Integer[length]));
            } catch(Exception e) {
                e.printStackTrace();
                System.out.print(s);
            }
        }
        // For every simulation set the same key
        for (Simulation simulation : simulations) {
            simulation = new Simulation();
            simulation.setKey((Key) keyObject);
            simulation.setMessage(messageGenerator.generateMessageDto());
            //simulation.setCiphertext();
        }
        return simulations;
    }
}
