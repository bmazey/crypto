package org.nyu.crypto;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.service.FrequencyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

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
        Boolean duplicate = false;

        for(Iterator iterator = responseJson.keySet().iterator(); iterator.hasNext();){
            String key = (String) iterator.next();
            JSONArray temp = (JSONArray) responseJson.get(key);
            int freq = frequencies.get(key);
            for (int i = 0; i<temp.size(); i++){
                if (!list_values.contains((int) (long) temp.get(i))) {
                    list_values.add((int) (long) temp.get(i));
                }
                else {
                    duplicate = true;
                }
            }
            assert temp.size() == freq;
            temp.clear();
        }
        assert list_values.size() == 106 && !duplicate;
        Collections.sort(list_values);
        for (int i = 0; i<106; i++){
            assert list_values.get(i) == i;
        }
    }
}
