package org.nyu.crypto.controller;

import org.nyu.crypto.service.DictionaryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
public class DictionaryController {

    @Autowired
    private DictionaryGenerator dictionaryGenerator;

    @RequestMapping(value="/api/dictionary", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getDictionary() {
        return ResponseEntity.ok(dictionaryGenerator.generateDictionaryDto());
    }

    //TODO- Smarter Regexp for this?
    @RequestMapping(value="/api/dictionary/{size:^[1-9]|[1-6][0-9]|70$}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getSubsetDictionary(@PathVariable("size") int size) {
        return ResponseEntity.ok(dictionaryGenerator.generateDictionaryDto(size));
    }


}
