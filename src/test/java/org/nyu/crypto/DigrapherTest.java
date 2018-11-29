package org.nyu.crypto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.service.Digrapher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CryptoApplication.class)
public class DigrapherTest {

    @Autowired
    private Digrapher digrapher;

    private final double TOTAL = 100.0;

    @Test
    public void generateDigraph() {
        double[][] digraph = digrapher.computeDigraph();
        Stream.of(digraph).map(Arrays::toString).forEach(System.out::println);

        // TODO - make sure result adds up to 100%.
        double sum = 0;
        for (int i = 0; i < digraph.length; i++) {
            for (int j = 0; j < digraph[i].length; j++) {
                sum += digraph[i][j];
            }
        }

        // sum should add up to 100
        assert sum == TOTAL;
    }
}
