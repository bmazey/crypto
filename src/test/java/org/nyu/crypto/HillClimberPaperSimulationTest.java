package org.nyu.crypto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.dto.PutativeKey;
import org.nyu.crypto.service.*;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CryptoApplication.class)
public class HillClimberPaperSimulationTest {

    @Autowired
    private MessageGenerator messageGenerator;

    @Autowired
    private Encryptor encryptor;

    @Autowired
    private Decryptor decrypt;

    @Autowired
    private KeyGenerator keyGenerator;

    @Autowired
    private GuessKey guessKey;

    @Autowired
    private DigraphService digraphService;

    @Test
    public void validatePerfectPlaintextFrequencyMatrix() {

        try {
            String message = messageGenerator.generateMessage();
            int[] cipher = encryptor.encrypt(keyGenerator.generateKey(), message);
            PutativeKey[] keyGuess = guessKey.getKey(cipher);
            System.out.print(cipher);
            for (int i = 0; i < 50; i++) {
                System.out.println("Guess " + (i + 1));
                System.out.println("Key Guess Before");
                guessKey.printKey(keyGuess);
                int guessvalue = guessKey.calculateScore(digraphService.getDigraphArray(decrypt.decrypt(cipher, keyGuess)),
                        digraphService.getDigraphArray(message));
                System.out.println(guessvalue);
                // Changed the distance value from 26 to 106
                for (int distance = 1; distance < 106; distance++) {
                    guessKey.swapKey(cipher, keyGuess, distance, digraphService.getDigraphArray(message));
                }
                System.out.println("Key Guess After ");
                guessKey.printKey(keyGuess);
                guessvalue = guessKey.calculateScore(digraphService.getDigraphArray(decrypt.decrypt(cipher, keyGuess)),
                        digraphService.getDigraphArray(message));
                System.out.println(decrypt.decrypt(cipher, keyGuess));
                System.out.println(guessvalue + "\n=====================================");
                if (guessvalue == 0)
                    break;
            }
            System.out.println(decrypt.decrypt(cipher, keyGuess));
            System.out.println(message);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void validateFrequencyDigraphMatrix() {

        try {
            String message = messageGenerator.generateMessage();
            int[] cipher = encryptor.encrypt(keyGenerator.generateKey(), message);
            PutativeKey[] keyGuess = guessKey.getKey(cipher);
            System.out.print(cipher);
            double d2=0.0;
            double d1=0.0;
            for (int i = 0; i < 100; i++) {
                System.out.println("Guess " + (i + 1));
                System.out.println("Key Guess Before");
                guessKey.printKey(keyGuess);
                String[] carry = new String[1];
                carry[0] = decrypt.decrypt(cipher, keyGuess);
                double guessvalue = guessKey.calculateScore(digraphService.createFrequencyDigraph(carry),
                        digraphService.getFrequencyDigraph());
                System.out.println(guessvalue);
                // Changed the distance value from 26 to 106
                for (int distance = 1; distance < 106; distance++) {

                    guessKey.swapKey(cipher, keyGuess, distance, digraphService.getFrequencyDigraph(),0,0);
                }
                System.out.println("Key Guess After ");
                guessKey.printKey(keyGuess);
                carry[0] = decrypt.decrypt(cipher, keyGuess);
                guessvalue = guessKey.calculateScore(digraphService.createFrequencyDigraph(carry),
                        digraphService.getFrequencyDigraph());
                if(guessvalue<=85 && guessvalue>=60)
                {
                    d1=0.1;
                    System.out.println("CASE:1>>>> "+guessvalue);
                }
                else if(guessvalue<=60 && guessvalue>=55)
                {
                    d1=0.45;
                    System.out.println("STUCk");
                    d2=0.0;
                    System.out.println("CASE:2>>>> "+guessvalue);
                }
                else if(guessvalue<=55 && guessvalue>=45)
                {
                    d1=0.35;
                    d2=0.0;
                    System.out.println("CASE:3>>>> "+guessvalue);
                }
                else if(guessvalue<=45)
                {
                    d1=0.3;
                    d2=0.0;
                    System.out.println("CASE:4>>>> "+guessvalue);
                }
                else{
                    System.out.println("STRICT "+guessvalue);
                    d1=0;d2=0;
                }
                System.out.println(decrypt.decrypt(cipher, keyGuess));
                System.out.println(guessvalue + "\n=====================================");
            }
            System.out.println(decrypt.decrypt(cipher, keyGuess));
            System.out.println(message);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
