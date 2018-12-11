package org.nyu.crypto.dto;

public class ClimbSample {

    private int[] ciphertext;
    private String message;

    public int[] getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(int[] ciphertext) {
        this.ciphertext = ciphertext;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
