package org.nyu.crypto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.service.FrequencyDiGraphGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CryptoApplication.class)
public class FrequencyDigraphGeneratorTest {

    @Autowired
    private FrequencyDiGraphGenerator frequencyDiGraphGenerator;

    @Test
    public void generateFrequencyDigraphMatrix() {

        frequencyDiGraphGenerator.generateFrequencyDigraph(100000);
    }
}
