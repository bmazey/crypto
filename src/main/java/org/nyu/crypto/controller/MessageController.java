package org.nyu.crypto.controller;

import org.nyu.crypto.service.MessageGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class MessageController {

    /**
     * Return a random 500 length plaintext message for encryption
     */

    @Autowired
    private MessageGenerator messageGenerator;

    @RequestMapping(value="/api/message", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getMessage() {
        return ResponseEntity.ok(messageGenerator.generateMessageDto());
    }

    @RequestMapping(value="/api/message/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getMessageById(@PathVariable UUID id) {
        return ResponseEntity.ok(messageGenerator.getMessageById(id));
    }
}
