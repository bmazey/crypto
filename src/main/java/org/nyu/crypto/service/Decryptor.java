package org.nyu.crypto.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;


@Service
public class Decryptor {

    /**
     * This is a simple decryption strategy which requires the original key
     * @param map
     * @param ciphertext
     * @return
     */
    public String decrypt(HashMap<String, ArrayList<Integer>> map, int[] ciphertext) {
        StringBuilder plaintext = new StringBuilder();

        for(int i = 0; i < ciphertext.length; i++) {

            for(String key : map.keySet()) {
                ArrayList values = map.get(key);
                if (values.contains(ciphertext[i])) {

                    if (key.equals("space")) plaintext.append(" ");
                    else plaintext.append(key);
                }
            }
        }

        return plaintext.toString();
    }

}
