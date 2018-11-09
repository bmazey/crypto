package org.nyu.crypto.controller;


import org.nyu.crypto.service.Simulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SimulationController {

    /**
     * OK ambitious ideas for this controller.
     *      - we want to make a simulation via GET '/api/simulation'
     *      - we should get a Simulation dto response which contains the key, plaintext, and resulting ciphertext
     *      - if we stored them by IDs and made GET '/api/key/{id}' and GET 'api/ciphertext/{id}' we can use HATEOAS
     */

    @Autowired
    private Simulator simulator;

    @RequestMapping(value="/api/simulation", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getSimulation() throws  Exception{
        return  ResponseEntity.ok(simulator.createSimulation());
    }

}
