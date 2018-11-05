package org.nyu.crypto.dto;

public class Simulation {
    /**
     * should contain a Key, Plaintext, and Ciphertext
     * remember: no methods but setters and getters here
     */

    private Key key;
    private Message message;
    private Ciphertext ciphertext;

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Ciphertext getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(Ciphertext ciphertext) {
        this.ciphertext = ciphertext;
    }

}
