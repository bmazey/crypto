package org.nyu.crypto.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nyu.crypto.dto.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;


@Service
public class KeyGenerator {

    @Autowired
    private FrequencyGenerator frequencyGenerator;

    private HashMap<String, ArrayList<Integer>> key;

    private ObjectMapper mapper;

    @Value("${key.space}")
    private int keyspace;

    public void setKey(HashMap<String, ArrayList<Integer>> key) {
        this.key = key;
    }

    public Key generateKeyDto(){
        mapper = new ObjectMapper();
        HashMap<String, ArrayList<Integer>> map = generateKey();
        Key key = mapper.convertValue(map, Key.class);
        return key;
    }

    public HashMap<String, ArrayList<Integer>> generateKey() {

        HashMap<String, Integer> map = frequencyGenerator.generateFrequency();
        ArrayList<Integer> numbers = new ArrayList<>(IntStream.range(0, keyspace).boxed().collect(toSet()));
        HashMap<String, ArrayList<Integer>> result = new HashMap<>();
        int partition=0;

        Collections.shuffle(numbers);

        for(String key: map.keySet()) {
            result.put(key,new ArrayList<> (numbers.subList(partition, partition+map.get(key))));
            partition=partition+map.get(key);
        }
        return result;
    }
    public HashMap<String, ArrayList<Integer>> generatePutativeKey(int[] ciphertext) {

        HashMap<String, Integer> map = frequencyGenerator.generateFrequency();
        HashMap<String, ArrayList<Integer>> putativeKey = new HashMap<>();
        ArrayList<Integer> whitelist = new ArrayList<>();
        ArrayList<Integer> blacklist = new ArrayList<>();

        // possible spaces

        blacklist.add(ciphertext[0]); //first character
        blacklist.add(ciphertext[1]); //second character
        blacklist.add(ciphertext[2]); //third character
        blacklist.add(ciphertext[104]); //last character

        for(int i = 0; i < ciphertext.length; i++){
            if (ciphertext[i] == ciphertext[i+1] && !blacklist.contains(ciphertext[i])){
                blacklist.add(ciphertext[i]);
                i++;
            }
            else if (whitelist.contains(ciphertext[i])){
                if (!blacklist.contains(ciphertext[i+1])){
                    blacklist.add(ciphertext[i+1]);
                }
                if (!blacklist.contains(ciphertext[i+2])){
                    blacklist.add(ciphertext[i+2]);
                }
                if (!blacklist.contains(ciphertext[i+3])){
                    blacklist.add(ciphertext[i+3]);
                }
            }
            else
                whitelist.add(ciphertext[i]);
        }

        Collections.shuffle(whitelist);
        ArrayList<Integer> spaceValues = new ArrayList<>(whitelist.subList(0, 19));
        blacklist.addAll(whitelist.subList(19, whitelist.size() + 1));

        putativeKey.put("space", spaceValues);

        // possible b

        int counter = 0;
        ArrayList<Integer> bValue = new ArrayList<>();

        for (int i = 0; i < ciphertext.length; i++){
            if (ciphertext[i] == ciphertext[i+1] && blacklist.contains(ciphertext[i]) && counter == 0){
                bValue.add(ciphertext[i]);
                blacklist.remove(ciphertext[i]);
                counter++;
            }
        }
        putativeKey.put("b", bValue);


        return new HashMap<>();
    }

}
