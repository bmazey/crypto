package org.nyu.crypto.controller;

import org.nyu.crypto.service.DictionaryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class DicitionaryController {

    @Autowired
    private DictionaryGenerator dictionaryGenerator;

    @RequestMapping(value="/api/dictionary", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getDictionary() {
        return ResponseEntity.ok(dictionaryGenerator.generateDictionaryDto());
    }

    //TODO - need to validate
    @RequestMapping(value="/api/dictionary/{size}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getSubsetDictionary(@PathVariable("size") int size) {
        return ResponseEntity.ok(dictionaryGenerator.generateDictionaryDto(size));
    }

}
