package ch.fhnw.cssr.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.fhnw.cssr.security.jwt.AccountCredentials;
import ch.fhnw.cssr.security.jwt.TokenResult;

public class TestUtils {

    public static String json(Object value) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(value);
    }
    
    public static <T> T fromJson(String content, Class<T> contentClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(content, contentClass);
    }
    
    
    /**
     * Gets the Authorization header to be used on requests.
     * @param mockMvc The MockMvc of the test
     * @param email The email of the user
     * @return The Value to be put in the Authorization header
     * @throws Exception Throws a test exception.
     */
    public static String getAuthValue(MockMvc mockMvc, String email) throws Exception {
        String password = email + "PASSWORD"; // Just a convention 4 testing
        
        String json = json(new AccountCredentials(email, password));
        MvcResult res = mockMvc.perform(post("/login").content(json))
            .andExpect(status().isOk())
            .andReturn();
        String content = res.getResponse().getContentAsString();
        
        TokenResult tokenRes = fromJson(content, TokenResult.class);
        return "Bearer " + tokenRes.getToken();
    }
}
