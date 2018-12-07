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

        for (int i = 0; i<ciphertext.length; i++){
            if(!blacklist.contains(ciphertext[i])){
                whitelist.add(ciphertext[i]);
            }
        }

        for(int i = 0; i < ciphertext.length - 1; i++) {
            if (ciphertext[i] == ciphertext[i + 1]) {
                bValue.add(ciphertext[i]);
                blacklist.add(ciphertext[i]);
                whitelist.remove(ciphertext[i]);
            }
        }

        ArrayList<Integer> possibleSpaceValues = new ArrayList<>(whitelist);
        ArrayList<Integer> spaceValues = new ArrayList<>();

        int space = 0;
        while(space != 19) {
            Collections.shuffle(possibleSpaceValues);
            spaceValues = new ArrayList<>(possibleSpaceValues.subList(0, 19));
            space = 0;
            for (int i = 3; i < ciphertext.length - 3; i ++){
                if (spaceValues.contains(ciphertext[i])){
                    if (spaceValues.contains(ciphertext[i+1]) ||
                            spaceValues.contains(ciphertext[i+2]) ||
                            spaceValues.contains(ciphertext[i+3])){
                        break;
                    }
                    if (spaceValues.contains(ciphertext[i-1]) ||
                            spaceValues.contains(ciphertext[i-2]) ||
                            spaceValues.contains(ciphertext[i-3])){
                        break;
                    }
                    else
                        space++;

                }
                if (space == 19)
                    break;
            }
        }

        numbers.removeAll(spaceValues);

        ArrayList<Integer> bNum = new ArrayList<>(bValue);

        if (bValue.size() > 1){
            ArrayList<Integer> bTemp = new ArrayList<>(bValue);
            Collections.shuffle(bTemp);
            bNum.clear();
            bNum.addAll(bTemp.subList(0, 1));
            numbers.removeAll(bNum);
        }

        if (bValue.size() == 0){
            ArrayList<Integer> bTemp = new ArrayList<>(numbers);
            Collections.shuffle(bTemp);
            bNum.clear();
            bNum.addAll(bTemp.subList(0, 1));
            numbers.removeAll(bNum);
        }

        if (bValue.size() == 1)
            numbers.removeAll(bNum);

        ArrayList<Integer> leftNum = new ArrayList<>(numbers);
        Collections.shuffle(leftNum);

        int partition = 0;

        for(String key: map.keySet()) {
            if (!key.equals("space") && !key.equals("b")) {

                putativeKey.put(key, new ArrayList<>(leftNum.subList(partition, partition + map.get(key))));
                partition = partition + map.get(key);
            }
        }

        putativeKey.put("space", spaceValues);

        putativeKey.put("b", bNum);

        return putativeKey;

//
//            else if (whitelist.contains(ciphertext[i]) && !bValue.contains(ciphertext[i])){
//                if (i < ciphertext.length - 1) {
//                    blacklist.add(ciphertext[i + 1]);
//                    whitelist.remove(ciphertext[i + 1]);
//                }
//                if (i < ciphertext.length - 2) {
//                    blacklist.add(ciphertext[i + 2]);
//                    whitelist.remove(ciphertext[i + 2]);
//                }
//                if (i < ciphertext.length - 3) {
//                    blacklist.add(ciphertext[i + 3]);
//                    whitelist.remove(ciphertext[i + 3]);
//                }
//            }
//            else if (!blacklist.contains(ciphertext[i]) && !bValue.contains(ciphertext[i]))
//                whitelist.add(ciphertext[i]);
//        }
//
//        if (bValue.size() > 1){
//            ArrayList<Integer> bTemp = new ArrayList<>(bValue);
//            Collections.shuffle(bTemp);
//            bValue.clear();
//            bValue.addAll(bTemp.subList(0, 1));
//            blacklist.removeAll(bValue);
//        }
//
//        if (bValue.size() == 0){
//            Collections.shuffle(numbers);
//            ArrayList<Integer> bTemp = new ArrayList<>(numbers.subList(0, 1));
//            blacklist.removeAll(bTemp);
//            bValue.addAll(bTemp);
//        }
//
//        int limit = keyspace - 20;
//
//        while (blacklist.size() < limit){
//            Collections.shuffle(numbers);
//            blacklist.addAll(numbers.subList(0, 1));
//        }
//
//        ArrayList<Integer> spaceValues = new ArrayList<>(whitelist);
//        ArrayList<Integer> otherChars = new ArrayList<>(blacklist);
//
//        if (spaceValues.size() < 19){
//            int currentSize = spaceValues.size();
//            Collections.shuffle(otherChars);
//            spaceValues.addAll(otherChars.subList(0, currentSize));
//            otherChars.removeAll(otherChars.subList(0, currentSize));
//        }
//
//        if (whitelist.size() > 19){
//            Collections.shuffle(spaceValues);
//            ArrayList<Integer> spaceTemp = new ArrayList<>(spaceValues.subList(0, 19));
//            spaceValues = spaceTemp;
//            otherChars.removeAll(spaceValues.subList(19, spaceValues.size()));
//        }
//
//        putativeKey.put("space", spaceValues);
//
//        ArrayList<Integer> bNum = new ArrayList<>(bValue);
//
//        putativeKey.put("b", bNum);
//
//        blacklist.addAll(numbers);
//
//
    }

}
