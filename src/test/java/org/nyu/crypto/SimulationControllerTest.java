package org.nyu.crypto;

import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CryptoApplication.class)
@AutoConfigureMockMvc
public class SimulationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private JSONParser parser = new JSONParser();

    @Test
    public void simulationControllerGet() throws Exception {

        // TODO - let's do BDD! define this test!

    }
}
