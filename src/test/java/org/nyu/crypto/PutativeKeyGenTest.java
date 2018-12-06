package org.nyu.crypto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.service.KeyGenerator;
import org.nyu.crypto.service.Simulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CryptoApplication.class)
public class PutativeKeyGenTest {

    @Autowired
    private KeyGenerator keyGenerator;

    @Autowired
    private Simulator simulator;

    @Test
    public void putativeKeyTest(){

        int[] ciphertext = simulator.createSimulation().getCiphertext();

        HashMap<String, ArrayList<Integer>> putativeKey = keyGenerator.generatePutativeKey(ciphertext);

        System.out.println(putativeKey);

    }
}
