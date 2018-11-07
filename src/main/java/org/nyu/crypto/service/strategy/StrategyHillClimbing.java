package org.nyu.crypto.service.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nyu.crypto.dto.Ciphertext;
import org.nyu.crypto.dto.Key;
import org.nyu.crypto.dto.Message;
import org.nyu.crypto.service.FrequencyGenerator;
import org.nyu.crypto.service.KeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class StrategyHillClimbing {

    @Autowired
    FrequencyGenerator frequencyGenerator;

    @Autowired
    KeyGenerator keyGenerator;

    public Message decrypt(Ciphertext ciphertext) {

        HashMap<String, Integer> frequencyMap = frequencyGenerator.generateFrequency();
        Message message = new Message();
        HashMap<String, ArrayList<Integer>> map = keyGenerator.generateKey();
        ObjectMapper objectMapper = new ObjectMapper();
        Key key = objectMapper.convertValue(map, Key.class);
        return message;
    }

    private void randomKeyLayer() {

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
