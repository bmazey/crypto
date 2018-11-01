package org.nyu.crypto.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.nyu.crypto.dto.Dictionary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;


@Service
public class DictionaryGenerator {

    private ObjectMapper objectMapper;

    public Dictionary generateDictionary() {
        Dictionary dictionary = new Dictionary();
        objectMapper = new ObjectMapper();
        try {
            File dictionaryFile = new ClassPathResource("dictionary.json").getFile();
            dictionary = objectMapper.readValue(dictionaryFile, Dictionary.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dictionary;
    }
}
