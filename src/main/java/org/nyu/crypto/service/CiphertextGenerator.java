package org.nyu.crypto.service;

import org.nyu.crypto.dto.Ciphertext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CiphertextGenerator {

    // TODO - check if there is redundancy between this and Encryptor.

    @Autowired
    private Encryptor encryptor;

    @Autowired
    private KeyGenerator keyGenerator;

    @Autowired
    private MessageGenerator messageGenerator;

    public Ciphertext getCipherMod() {

        //TODO: persist the key with an ID which can be retrieved for random ciphers
        Ciphertext ciphertext = new Ciphertext();
        ciphertext.setCiphertext(encryptor.encryptMod(keyGenerator.generateKey(), messageGenerator.generateMessage()));
        return ciphertext;
    }
}
