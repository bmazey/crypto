package org.nyu.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.service.Decryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CryptoApplication.class)
@AutoConfigureMockMvc
public class SimulationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Decryptor decryptor;

    private ObjectMapper mapper = new ObjectMapper();

    private final int SPACE = 500;

    @Test
    @SuppressWarnings("unchecked")
    public void simulationControllerGet() throws Exception {

        MvcResult result = this.mockMvc.perform(get("/api/simulation"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Object jsonObject = parser.parse(result.getResponse().getContentAsString());
        JSONObject responseJson = (JSONObject)jsonObject;

        // Creates Map of the key from responseJson
        HashMap<String, ArrayList<Integer>> map  = mapper.convertValue(responseJson.get("key"), HashMap.class);

        // Generates the message as string
        String message = responseJson.get("message").toString();

        int[] cipher = mapper.convertValue(responseJson.get("ciphertext"), int[].class);

        for (int i : cipher) {
            if (i instanceof int) {

            }
        }

        String plaintext = decryptor.decrypt(map, cipher);

        Assert.assertEquals(message, plaintext, message);

    }
}
