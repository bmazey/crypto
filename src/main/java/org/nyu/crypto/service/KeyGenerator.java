package org.nyu.crypto.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nyu.crypto.dto.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;


@Service
public class KeyGenerator {

    @Autowired
    private FrequencyGenerator frequencyGenerator;

    private HashMap<String, ArrayList<Integer>> key;

    private ObjectMapper mapper;

    // this is set to 106 because Random.nextInt(inclusive, exclusive) ...
    private final int KEYSPACE = 106;

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

        Random r = new Random();
        HashMap<String, Integer> map = frequencyGenerator.generateFrequency();
        ArrayList<Integer> numbers = new ArrayList<>(IntStream.range(0, KEYSPACE).boxed().collect(toSet()));

        HashMap<String, ArrayList<Integer>> result = new HashMap<>();

        // TODO - clean this up; is there a better way to do this?
        //TODO - Yes we can use shuffling to clean up the code
        for (String key : map.keySet()) {
            result.put(key, new ArrayList<>());
        }

        for (String key : map.keySet()){
            for (int i = 0; i < map.get(key); i++) {
                Integer number = numbers.get(r.nextInt(numbers.size()));
                ArrayList<Integer> keyList = result.get(key);
                keyList.add(number);
                result.put(key, keyList);

                // remove the number because we've used it ...
                numbers.remove(number);

            }
        }

        return result;
    }

}
