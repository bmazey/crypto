package org.nyu.crypto.controller;

import org.nyu.crypto.service.KeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;


@RestController
public class KeyController {

    @Autowired
    KeyGenerator keyGenerator;

    @RequestMapping(value = "/api/key", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getKey() {
        HashMap<String, ArrayList<Integer>> key = keyGenerator.generateKey();
        return ResponseEntity.ok(keyGenerator.generateKeyDto());
    }
}
