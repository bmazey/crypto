package org.nyu.crypto.service;

import org.nyu.crypto.dto.Digraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class FrequencyDiGraphGenerator {

    @Autowired
    private MessageGenerator messageGenerator;

    @Autowired
    private DigraphService digraphService;

    @Value("${charset.length}")
    private int dimension;

    /**
     * This function needs the number of messages to calculate the digraph
     * @param val
     * @return
     */
    public double[][] generateFrequencyDigraph(int val) {

        double[][] freq = new double[dimension][dimension];
        String[] messages = new String[val];
        return digraphService.createFrequencyDigraph(messages);
    }
}
