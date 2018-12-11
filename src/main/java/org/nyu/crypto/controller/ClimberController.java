package org.nyu.crypto.controller;

import org.nyu.crypto.dto.Ciphertext;
import org.nyu.crypto.dto.ClimbSample;
import org.nyu.crypto.dto.Simulation;
import org.nyu.crypto.service.KeyGenerator;
import org.nyu.crypto.service.Simulator;
import org.nyu.crypto.service.strategy.HillClimberPaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ClimberController {

    @Autowired
    private KeyGenerator keyGenerator;

    @Autowired
    private Simulator simulator;

    @Autowired
    private HillClimberPaper hillClimberPaper;

    @RequestMapping(value = "/api/climb", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> generateClimbSample() {

        ClimbSample sample = new ClimbSample();
        Simulation simulation = simulator.createSimulation();
        sample.setCiphertext(simulation.getCiphertext());
        sample.setMessage(simulation.getMessage());

        return ResponseEntity.ok(sample);
    }

    @RequestMapping(value = "/api/climb/plaintext", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> performPerfectPlainText(@RequestBody ClimbSample sample) {
        return ResponseEntity.ok(hillClimberPaper.climbPlaintextDigraph(sample.getCiphertext(), sample.getMessage()));
    }

    @RequestMapping(value = "/api/climb/frequency", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> decipheragainstfrequencydigraph(@RequestBody Ciphertext ciphertext) {

        return ResponseEntity.ok(hillClimberPaper.climbDictionaryFrequencyDigraph(ciphertext.getCiphertext()));
    }


}
