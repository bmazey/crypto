package org.nyu.crypto.service;


import org.nyu.crypto.dto.Dictionary;
import org.nyu.crypto.dto.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;


@Service
public class MessageGenerator {

    @Autowired
    private DictionaryGenerator dictionaryGenerator;

    private final int MESSAGE_SPACE = 500;

    private Random r;

    public String generateMessage() {
        r = new Random();
        StringBuilder messageBuilder = new StringBuilder();
        Dictionary dictionary = dictionaryGenerator.generateDictionary();

        while(messageBuilder.length() < 500) {
            messageBuilder.append(dictionary.getWords()[r.nextInt(dictionary.getWords().length)]);
            messageBuilder.append(" ");
        }

        return messageBuilder.subSequence(0, 500).toString();
    }

    public Message generateMessageDto() {
        Message messageDto = new Message();
        messageDto.setMessage(generateMessage());
        return messageDto;
    }

    public String generateSubsetMessage() {
        r = new Random();
        StringBuilder messageBuilder = new StringBuilder();
        Dictionary dictionary = dictionaryGenerator.generateDictionary();

        /**
         * I really like the idea of having a method that can shorten the size of the dictionary as I believe there
         * will be many testing scenarios where we will want to measure performance based on this.
         *
         * b.mazey@nyu.edu
         */

        // TODO - make this more customizable
        // here we assert the first word comes from the first ten entries in the dictionary
        messageBuilder.append(dictionary.getWords()[10]);
        messageBuilder.append(" ");

        while(messageBuilder.length() < 500) {
            messageBuilder.append(dictionary.getWords()[r.nextInt(dictionary.getWords().length)]);
            messageBuilder.append(" ");
        }

        return messageBuilder.subSequence(0, 500).toString();
    }

}
