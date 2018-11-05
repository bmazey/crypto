package org.nyu.crypto.controller;

import org.nyu.crypto.dto.Ciphertext;
import org.nyu.crypto.service.CiphertextGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CiphertextController {

    /**
     * Generate a cipher text
     */
    @Autowired
    CiphertextGenerator ciphertextGenerator;

    @RequestMapping(value="/api/cipher/mod",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getCipherTextGenerator() {

        return ResponseEntity.ok(ciphertextGenerator.getCipherMod());
    }

    @RequestMapping(value = "/api/cipher/random", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getCipherTextGeneratorRandom(){

        return ResponseEntity.ok(ciphertextGenerator.getCipherMod());
    }
}
