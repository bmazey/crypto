package org.nyu.crypto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.dto.Key;
import org.nyu.crypto.dto.Simulation;
import org.nyu.crypto.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CryptoApplication.class)
public class PutativeKeyGenTest {

    @Value("${key.space}")
    private int keyspace;

    @Value("${message.space}")
    private int messageSpace;

    @Autowired
    private KeyGenerator keyGenerator;

    @Autowired
    private Simulator simulator;

    @Autowired
    private FrequencyGenerator frequencyGenerator;

    @Autowired
    private MessageGenerator messageGenerator;

    @Autowired
    private Decryptor decryptor;

    @Autowired
    private Encryptor encryptor;

    private Logger logger = LoggerFactory.getLogger(PutativeKeyGenTest.class);

    @Test
    public void putativeKeyspaceTest() {

        int[] ciphertext = simulator.createSimulation().getCiphertext();

        HashMap<String, ArrayList<Integer>> putativeKey = keyGenerator.generatePutativeKey(ciphertext);

        HashSet<Integer> set = new HashSet<>();

        for (String key : putativeKey.keySet()) {
            ArrayList<Integer> list = putativeKey.get(key);
            set.addAll(list);
        }

        // Checks the the size of total keyspace, should be 106
        assert set.size() == keyspace;

    }

    @Test
    public void putativeKeyFreq(){

        int[] ciphertext = simulator.createSimulation().getCiphertext();

        HashMap<String, ArrayList<Integer>> putativeKey = keyGenerator.generatePutativeKey(ciphertext);

        HashMap<String, Integer> frequencies = frequencyGenerator.generateFrequency();

        // Checks the size of each key, "space" should be 19, etc.
        for (String key : putativeKey.keySet()){
            ArrayList<Integer> list = putativeKey.get(key);
            int freq = frequencies.get(key);
            assert list.size() == freq;
        }
    }

    @Test
    public void KeySimulation(){

        // Generating a message, a key, and encrypting that ciphertext using that key
        String plaintext = messageGenerator.generateMessage();
        HashMap<String, ArrayList<Integer>> actualKey = keyGenerator.generateKey();
        int[] ciphertext = encryptor.encrypt(actualKey, plaintext);

        // Generating a putative key using the ciphertext
        HashMap<String, ArrayList<Integer>> putativeKey = keyGenerator.generatePutativeKey(ciphertext);

        // Decrypting ciphertext with putative key
        String putativePlaintext = decryptor.decrypt(putativeKey, ciphertext);

        HashMap<String, Integer> commonValues = new HashMap<>();

        int score = 0;

        // Common chars between actual plaintext and putative plaintext
        for (int i = 0; i < messageSpace; i++){
            if (putativePlaintext.charAt(i) == plaintext.charAt(i))
                score++;
        }

        // Common key values between actual key and putative key
        for (String key : actualKey.keySet()){
            ArrayList<Integer> actual = actualKey.get(key);
            ArrayList<Integer> putative = putativeKey.get(key);
            actual.retainAll(putative);
            int common = actual.size();
            commonValues.put(key, common);
        }

        logger.info("Total of common chars: " + score);
        logger.info("Common key values: " + commonValues);

//        logger.info("Actual key: " + actualKey);
//        logger.info("Putative key: " + putativeKey);
//
//        logger.info("Actual plaintext: " + plaintext);
//        logger.info("Putative plaintext: " + putativePlaintext);
    }

    @Test
    public void KeySimulations(){
        for (int i = 0; i < 10; i++){
            KeySimulation();
        }
    }
}
