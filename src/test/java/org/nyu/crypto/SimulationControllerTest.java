package org.nyu.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
    Decryptor decryptor;

    private JSONParser parser = new JSONParser();

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
        HashMap<String, ArrayList<Integer>> map  = (JSONObject)responseJson.get("key");

        // Generates the message as string
        String message = responseJson.get("message").toString();

        // Converts ciphertext into int[]
        JSONArray temp = (JSONArray) responseJson.get("ciphertext");
        int[] cipher_int = new int[SPACE];
        for (int i = 0; i<cipher_int.length; i++) {
            cipher_int[i] = (int) (long) temp.get(i);
        }

        String plaintext = decryptor.decrypt(map, cipher_int);

        Assert.assertEquals(message, plaintext, message);

    }
}
