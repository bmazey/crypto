package org.nyu.crypto.controller;

import org.nyu.crypto.service.MessageGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    /**
     * Return a random 500 length plaintext message for encryption
     */

    @Autowired
    private MessageGenerator messageGenerator;

    @RequestMapping(value="/api/message", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getMessage() {
        return ResponseEntity.ok(messageGenerator.generateMessageDto());
    }
}
