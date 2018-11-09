package org.nyu.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
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

        JSONObject json = new JSONObject(result.getResponse().getContentAsString());

        HashMap<String, ArrayList<Integer>> key = mapper.readValue(json.get("key").toString(), HashMap.class);

        String message = json.get("message").toString();

        int[] ciphertext = mapper.readValue(json.get("ciphertext").toString(), int[].class);

        String plaintext = decryptor.decrypt(key, ciphertext);

        Assert.assertEquals(message, plaintext);

    }
}
