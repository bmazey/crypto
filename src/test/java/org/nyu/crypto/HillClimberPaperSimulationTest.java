package org.nyu.crypto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.dto.PutativeKey;
import org.nyu.crypto.service.*;
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
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
