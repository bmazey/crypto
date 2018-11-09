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
        Dictionary dictionary = dictionaryGenerator.generateDictionaryDto();

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
        Dictionary dictionary = dictionaryGenerator.generateDictionaryDto();

        // TODO - make this more customizable
        // TODO - use shuffling
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
