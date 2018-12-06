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
        ArrayList<Integer> numbers = new ArrayList<>(IntStream.range(0, keyspace).boxed().collect(toSet()));
        HashSet<Integer> whitelist = new HashSet<>();
        HashSet<Integer> blacklist = new HashSet<>();
        HashSet<Integer> bValue = new HashSet<>();

        // possible spaces and possible b

        blacklist.add(ciphertext[0]); //first character
        blacklist.add(ciphertext[1]); //second character
        blacklist.add(ciphertext[2]); //third character
        blacklist.add(ciphertext[104]); //last character

        for(int i = 0; i < ciphertext.length - 1; i++){
            if (ciphertext[i] == ciphertext[i+1]){
                blacklist.add(ciphertext[i]);
                bValue.add(ciphertext[i]);
            }
            else if (whitelist.contains(ciphertext[i])){
                if (i < ciphertext.length - 1)
                        blacklist.add(ciphertext[i + 1]);
                if (i < ciphertext.length - 2)
                        blacklist.add(ciphertext[i + 2]);
                if (i < ciphertext.length - 3)
                        blacklist.add(ciphertext[i + 3]);
            }
            else
                whitelist.add(ciphertext[i]);
        }

        System.out.println(blacklist);
        System.out.println(whitelist);
        System.out.println(bValue);

        if (bValue.size() > 1){
            Random random = new Random();
            int b = random.nextInt(bValue.size());
            bValue.clear();
            bValue.add(b);
            numbers.remove(b);
        }

        ArrayList<Integer> bNum = new ArrayList<>(bValue);
        putativeKey.put("b", bNum);

        ArrayList<Integer> spaceValues = new ArrayList<>(whitelist);

        if (spaceValues.size() < 19){
            Collections.shuffle(numbers);
            for(int i = 0; i < spaceValues.size(); i++){
                spaceValues.add(numbers.get(i));
                numbers.remove(numbers.get(i));
            }
        }

        putativeKey.put("space", spaceValues);

        blacklist.addAll(numbers);

        ArrayList<Integer> leftNum = new ArrayList<>(blacklist);

        int partition = 0;

        for(String key: map.keySet()) {
            if (key.equals("space")){
                partition = partition + map.get(key);
            }
            else if (key.equals("b")){
                partition = partition + map.get(key);
            }
            else {
                putativeKey.put(key, new ArrayList<>(leftNum.subList(partition, partition + map.get(key))));
                partition = partition + map.get(key);
            }
        }
        return putativeKey;
    }

}
