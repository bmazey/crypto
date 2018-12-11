package org.nyu.crypto.service;

import org.nyu.crypto.dto.Digraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.io.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Service
public class FrequencyDiGraphGenerator {

    @Autowired
    private MessageGenerator messageGenerator;

    @Autowired
    private DigraphService digraphService;

    @Value("${charset.length}")
    private int dimension;

    /**
     * This function needs the number of messages to calculate the digraph
     * @param length
     * @return
     */
    public void generateFrequencyDigraph(int length) {

        String[] messages = new String[length];
        for (int i =0; i<length; i++) {
            messages[i] = messageGenerator.generateMessage();
        }
        double[][] freq = digraphService.createFrequencyDigraph(messages);
        /*ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("resources").getFile() + "/frequency_digraph.txt");*/
        try{
            DecimalFormat df = new DecimalFormat("#.#####");
            df.setRoundingMode(RoundingMode.CEILING);
            /*File file = new File("resources/frequency_digraph1.txt");
            if (file.createNewFile()) {
                System.out.println("File is created!");
            } else {
                System.out.println("File already exists.");
            }
            FileWriter fw = new FileWriter(file);
            */for (int row = 0; row < 27; row++) {
                for (int col = 0; col < 27; col++) {
                    double val = freq[row][col];
                    System.out.print(df.format(freq[row][col]) + " ");
                }
                System.out.println();
            }
            //fw.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        //return digraphService.createFrequencyDigraph(messages);
    }
}
