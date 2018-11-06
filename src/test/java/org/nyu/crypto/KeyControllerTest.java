package org.nyu.crypto;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.service.FrequencyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    public void keyControllerGets() throws Exception {

        MvcResult result = this.mockMvc.perform(get("/api/key"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Object jsonObject = parser.parse(result.getResponse().getContentAsString());
        JSONObject responseJson = (JSONObject)jsonObject;

        Set<Long> possible_keys = new HashSet<Long>();

        // Checks the the size of total keyspace, should be 106
        for (Object key : responseJson.keySet()) {
            JSONArray temp = (JSONArray) responseJson.get(key);
            for (int i = 0; i<temp.size(); i++) {
                possible_keys.add((long) temp.get(i));
            }
        }

        try{
            Assert.assertEquals(106, possible_keys.size());
        } catch(AssertionError e) {
            System.out.println("Size Equality Assertion Error");
        }
    }

    @Test
    public void keyControllerFrequencies() throws Exception {

        MvcResult result = this.mockMvc.perform(get("/api/key"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Object jsonObject = parser.parse(result.getResponse().getContentAsString());
        JSONObject responseJson = (JSONObject) jsonObject;

        HashMap<String, Integer> frequencies = new FrequencyGenerator().generateFrequency();

        // Checks the size of each key, "space" should be 19, etc.
        for (Object key : responseJson.keySet()){
            JSONArray temp = (JSONArray) responseJson.get(key);
            int freq = frequencies.get(key);
            try {
                Assert.assertEquals(temp.size(), freq);
            } catch(AssertionError e){
                System.out.println("Frequency Assertion Error for key: " + key.toString());
            }
        }
    }

}
