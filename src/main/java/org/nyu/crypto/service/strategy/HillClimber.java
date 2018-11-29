package org.nyu.crypto.service.strategy;


import org.nyu.crypto.service.FrequencyGenerator;
import org.nyu.crypto.service.KeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class HillClimber {

    @Autowired
    private FrequencyGenerator frequencyGenerator;

    @Autowired
    private KeyGenerator keyGenerator;


    public String decrypt(int[] ciphertext) {

        HashMap<String, Integer> frequencyMap = frequencyGenerator.generateFrequency();

        //Initial Random Key guess before using greedy algorithm
        HashMap<String, ArrayList<Integer>> key = keyGenerator.generateKey();

        return "";
    }

    private void randomInitialKeyLayer() {

        //TODO: Implement key generation
        //After a key is rejected the key must be changed by exchanging the values
    }

    private void calculateScore() {

        //TODO: Calculate score from the key using the digram logic
        // If score is 0 then it is a perfect match.

    }

    private void getPutativePlainText() {

        //TODO: Create a putative plain text and call for score calculation
    }

}
