package org.nyu.crypto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.service.Decryptor;
import org.nyu.crypto.service.Encryptor;
import org.nyu.crypto.service.KeyGenerator;
import org.nyu.crypto.service.MessageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CryptoApplication.class)
public class ChosenPlaintextAttack {

    @Autowired
    private KeyGenerator keyGenerator;

    @Autowired
    private MessageGenerator messageGenerator;

    @Autowired
    private Encryptor encryptor;

    @Autowired
    private Decryptor decryptor;

    private Logger logger = LoggerFactory.getLogger(ChosenPlaintextAttack.class);

    @Test
    public void ChosenPlaintextAttackGen(){

        HashMap<String, ArrayList<Integer>> actualMap = keyGenerator.generateKey();
        HashMap<String, ArrayList<Integer>> newMap = new HashMap<>();

        int totalValues = 0;
        int round = 0;

        while (totalValues < 106) {

            String plaintext = messageGenerator.generateMessage();
            int[] ciphertext = encryptor.encrypt(actualMap, plaintext);

            for (int i = 0; i < ciphertext.length; i++) {
                String currentLetter = Character.toString(plaintext.charAt(i));
                int currentNum = ciphertext[i];

                // SPACE!!
                if (currentLetter.equals(" ")){
                    if (newMap.containsKey("space")) {
                        HashSet<Integer> temp = new HashSet<>(newMap.get("space"));
                        temp.add(currentNum);
                        newMap.put("space", new ArrayList<>(temp));
                    }
                    else{
                        ArrayList<Integer> temp = new ArrayList<>();
                        temp.add(currentNum);
                        newMap.put("space", temp);
                    }
                }

                // Other letters
                else {
                    if (newMap.containsKey(currentLetter)) {
                        HashSet<Integer> temp = new HashSet<>(newMap.get(currentLetter));
                        temp.add(currentNum);
                        newMap.put(currentLetter, new ArrayList<>(temp));
                    }
                    else {
                        ArrayList<Integer> temp = new ArrayList<>();
                        temp.add(currentNum);
                        newMap.put(currentLetter, temp);
                    }
                }
            }
            for (String key: newMap.keySet()){
                totalValues += newMap.get(key).size();
            }
            round++;
        }

        logger.info("Number of rounds needed to discover full key: " + round);

        String plaintext = messageGenerator.generateMessage();
        int[] ciphertext = encryptor.encrypt(actualMap, plaintext);
        String result = decryptor.decrypt(newMap, ciphertext);

        assert (result.equals(plaintext));
    }
}
