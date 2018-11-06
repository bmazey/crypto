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
    public void keyControllerGet() throws Exception {

        MvcResult result = this.mockMvc.perform(get("/api/key"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Object jsonObject = parser.parse(result.getResponse().getContentAsString());
        JSONObject responseJson = (JSONObject)jsonObject;

        HashMap<String, Integer> frequencies = new FrequencyGenerator().generateFrequency();
        ArrayList<Integer> list_values = new ArrayList<>();
        int count= 0;
        Boolean duplicate = false;
        Set<Long> possible_keys = new HashSet<Long>();

        for (Object key : responseJson.keySet()) {
            JSONArray temp = (JSONArray) responseJson.get(key);
            for (int i = 0; i<temp.size() - 1 ; i++) {
                possible_keys.add((long) temp.get(i));
            }
        }

        possible_keys.add(119L);

        Assert.assertNotEquals(106, possible_keys.size());
    }

    @Test
    public void keyControllerGetEquals() throws Exception {

        MvcResult result = this.mockMvc.perform(get("/api/key"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Object jsonObject = parser.parse(result.getResponse().getContentAsString());
        JSONObject responseJson = (JSONObject)jsonObject;

        HashMap<String, Integer> frequencies = new FrequencyGenerator().generateFrequency();
        ArrayList<Integer> list_values = new ArrayList<>();
        int count= 0;
        Boolean duplicate = false;
        Set<Long> possible_keys = new HashSet<Long>();

        for (Object key : responseJson.keySet()) {
            JSONArray temp = (JSONArray) responseJson.get(key);
            for (int i = 0; i<temp.size(); i++) {
                possible_keys.add((long) temp.get(i));
            }
        }

        Assert.assertEquals(106, possible_keys.size());
    }
}
