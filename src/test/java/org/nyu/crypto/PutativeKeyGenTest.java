package org.nyu.crypto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.service.FrequencyGenerator;
import org.nyu.crypto.service.KeyGenerator;
import org.nyu.crypto.service.Simulator;
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

    @Autowired
    private KeyGenerator keyGenerator;

    @Autowired
    private Simulator simulator;

    @Autowired
    private FrequencyGenerator frequencyGenerator;

    @Test
    public void putativeKeyTest() {

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
}
