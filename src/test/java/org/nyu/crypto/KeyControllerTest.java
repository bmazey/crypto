package org.nyu.crypto;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CryptoApplication.class)
@AutoConfigureMockMvc
public class KeyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private JSONParser parser = new JSONParser();

    @Test
    public void keyControllerGet() throws Exception {

        MvcResult result = this.mockMvc.perform(get("/api/key"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Object jsonObject = parser.parse(result.getResponse().getContentAsString());
        JSONObject responseJson = (JSONObject)jsonObject;

        // TODO assert that the keyspace for each letter in the generated key matches what's defined in frequency.json

        // TODO assert that every number from 0 - 105 is represented in the generated key
    }
}
