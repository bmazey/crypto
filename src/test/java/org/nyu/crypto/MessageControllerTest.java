package org.nyu.crypto;


import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CryptoApplication.class)
@AutoConfigureMockMvc
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void messageControllerTest() throws Exception {

        MvcResult result = this.mockMvc.perform(get("/api/message"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JSONObject json = new JSONObject(result.getResponse().getContentAsString());

        String messageGenerated = (String)json.get("message");
        assertEquals(500, messageGenerated.length());
        assertNotEquals(499, messageGenerated.length());
    }


}
