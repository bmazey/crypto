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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${charset.length}")
    private int charsetLength;

    private Logger logger = LoggerFactory.getLogger(ChosenPlaintextAttack.class);

    public void ChosenPlaintextAttackGen(){

        HashMap<String, ArrayList<Integer>> actualMap = keyGenerator.generateKey();
        HashMap<String, ArrayList<Integer>> newMap = new HashMap<>();

        int totalValues = 0;
        int round = 0;

        // No 'q' in dictionary ->> therefore 106 - 1 = 105
        while (totalValues < 105) {

            totalValues = 0;
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

                // Other characters
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

        // Creating an ArrayList with all the values of the new key
        ArrayList<Integer> allValues = new ArrayList<>();
        for (String key: newMap.keySet())
            allValues.addAll(newMap.get(key));

        // Finding the value for the letter 'q'
        ArrayList<Integer> qValue = new ArrayList<>();
        for (int i = 0; i < 106; i++){
            if (!allValues.contains(i)){
                qValue.add(i);
                newMap.put("q", qValue);
            }
        }

        logger.info("Number of rounds needed to discover full key: " + round);

        String plaintext = messageGenerator.generateMessage();
        int[] ciphertext = encryptor.encrypt(actualMap, plaintext);
        String result = decryptor.decrypt(newMap, ciphertext);

        // Asserting whether the resulting plaintext using the new key is equal to the original plaintext

        logger.info("Original plaintext: " + plaintext);
        logger.info("Resulting plaintext: " + result);
        assert (result.equals(plaintext));

        // Asserting whether the number of keys in the new keymap is equal to 27

        assert (newMap.size() == charsetLength);

        // Asserting whether the keyspace for each character is correct
        for (String key: newMap.keySet()){
            assert (newMap.get(key).size() == actualMap.get(key).size());
        }
    }

    @Test
    public void CPASimulation(){

        for (int i = 0; i < 10; i++){
            ChosenPlaintextAttackGen();
        }
    }
}
