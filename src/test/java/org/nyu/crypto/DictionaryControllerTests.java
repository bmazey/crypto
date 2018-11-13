package org.nyu.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyu.crypto.dto.Dictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CryptoApplication.class)
@AutoConfigureMockMvc
public class DictionaryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${dictionary.defaultLength}")
    private int dictionaryLength;

    @Test
    public void dictionaryControllerGet() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/api/dictionary"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Dictionary dictionary = objectMapper.readValue(result.getResponse().getContentAsString(), Dictionary.class);

        Assert.assertEquals(dictionaryLength, dictionary.getWords().length);
    }

    @Test
    public void dictionaryControllerCheckSize() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/api/dictionary/69"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Dictionary dictionary = objectMapper.readValue(result.getResponse().getContentAsString(), Dictionary.class);

        Assert.assertEquals(69, dictionary.getWords().length);
    }

    @Test
    public void dictionaryControllerCheckInvalidSize() throws Exception {
        MvcResult underflowSizeResult = this.mockMvc.perform(get("/api/dictionary/0"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        MvcResult overflowSizeResult = this.mockMvc.perform(get("/api/dictionary/71"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

    }
    @Test
    public void dictionaryControllerInvalidType() throws Exception {
        MvcResult invalidTypeResult = this.mockMvc.perform(get("/api/dictionary/!@#%^"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

}
