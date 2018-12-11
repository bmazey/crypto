package org.nyu.crypto.controller;

import org.nyu.crypto.service.KeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HillClimberPaperController {

    @Autowired
    private KeyGenerator keyGenerator;

    @RequestMapping(value = "/api/hillclimbing/perfectplaintext", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> performPerfectPlainText() {
        return ResponseEntity.ok(keyGenerator.generateKeyDto());
    }

    @RequestMapping(value = "/api/hillclimbing/frequencydigraph", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> decipheragainstfrequencydigraph() {
        return ResponseEntity.ok(keyGenerator.generateKeyDto());
    }
}
