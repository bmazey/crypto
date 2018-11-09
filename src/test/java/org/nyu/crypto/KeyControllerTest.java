package org.nyu.crypto;


import org.json.JSONObject;
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

    @Autowired
    private FrequencyGenerator frequencyGenerator;

    private final int KEYSPACE = 106;


    @Test
    public void keyControllerGet() throws Exception {

        MvcResult result = this.mockMvc.perform(get("/api/key"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JSONObject json = new JSONObject(result.getResponse().getContentAsString());

        HashSet<Integer> set = new HashSet<>();

        // Checks the the size of total keyspace, should be 106
        while (json.keys().hasNext()) {

        }

        assert set.size() == KEYSPACE;
    }

    @Test
    public void keyControllerFrequencies() throws Exception {

        MvcResult result = this.mockMvc.perform(get("/api/key"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Object jsonObject = parser.parse(result.getResponse().getContentAsString());
        JSONObject responseJson = (JSONObject) jsonObject;

        HashMap<String, Integer> frequencies = frequencyGenerator.generateFrequency();

        // Checks the size of each key, "space" should be 19, etc.
        for (Object key : responseJson.keySet()){
            JSONArray temp = (JSONArray) responseJson.get(key);
            int freq = frequencies.get(key.toString());
            assert temp.size() == freq;
        }
    }

}
