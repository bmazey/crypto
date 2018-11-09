package org.nyu.crypto;


import com.fasterxml.jackson.databind.ObjectMapper;
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

    ObjectMapper mapper = new ObjectMapper();

    private final int KEYSPACE = 106;


    @Test
    @SuppressWarnings("unchecked")
    public void keyControllerGet() throws Exception {

        MvcResult result = this.mockMvc.perform(get("/api/key"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        HashMap<String, ArrayList<Integer>> map = mapper.readValue(result.getResponse().getContentAsString(), HashMap.class);

        HashSet<Integer> set = new HashSet<>();

        for (String key : map.keySet()) {
            ArrayList<Integer> list = map.get(key);
            set.addAll(list);
        }

        // Checks the the size of total keyspace, should be 106
        assert set.size() == KEYSPACE;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void keyControllerFrequencies() throws Exception {

        MvcResult result = this.mockMvc.perform(get("/api/key"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        HashMap<String, Integer> frequencies = frequencyGenerator.generateFrequency();

        HashMap<String, ArrayList<Integer>> map = mapper.readValue(result.getResponse().getContentAsString(), HashMap.class);

        // Checks the size of each key, "space" should be 19, etc.
        for (String key : map.keySet()){
            ArrayList<Integer> list = map.get(key);
            int freq = frequencies.get(key);
            assert list.size() == freq;
        }
    }
}
