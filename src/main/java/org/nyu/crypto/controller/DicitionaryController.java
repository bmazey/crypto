package org.nyu.crypto.controller;

import org.nyu.crypto.service.DictionaryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;

@RestController
public class DicitionaryController {
    @Autowired
    DictionaryGenerator dictionaryGenerator;




    @RequestMapping(value="/api/dictionary",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getDictionaryGenerator() {

        return ResponseEntity.ok(dictionaryGenerator.generateDictionaryDto());
    }

    //TODO- Not working properly, need to read up on how to solve this without using a validation Model
    @RequestMapping(value="/api/dictionary",method = RequestMethod.GET,params = {"size"})
    @ResponseBody
    public ResponseEntity<?> getDictionaryGenerator(@RequestParam("size") @Size(min=1,max=70,message="Invalid Size")int size) {

        return ResponseEntity.ok(dictionaryGenerator.generateDictionaryDto(size));
    }
}
