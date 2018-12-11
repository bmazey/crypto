package org.nyu.crypto.controller;

import org.nyu.crypto.dto.Digraph;
import org.nyu.crypto.dto.Message;
import org.nyu.crypto.service.DigraphService;
import org.nyu.crypto.service.MessageGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DigraphController {

    @Autowired
    private DigraphService digraphService;

    @Autowired
    private MessageGenerator messageGenerator;

    @RequestMapping(value = "/api/digraph", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> generateDigraph() {
        Digraph digraph  = new Digraph();
        String text = messageGenerator.generateMessage();
        digraph.setPlaintext(text);
        digraph.setDigraph(digraphService.getDigraphArray(text));
        return ResponseEntity.ok(digraph);
    }
}
