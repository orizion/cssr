package ch.fhnw.cssr.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.security.jwt.AccountCredentials;
import ch.fhnw.cssr.security.jwt.TokenResult;

public class TestUtils {

    /**
     * Serializes an object as JSON.
     * @param value The object to be serializes.
     * @return The Json
     * @throws JsonProcessingException Error if not possible.
     */
    public static String json(Object value) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writeValueAsString(value);
    }
    
    /**
     * Deserializes an object from JSON.  
     * @param content The content of an MvcRequest.
     * @param contentClass The class of the result
     * @return The deserialized object.
     * @throws IOException Invalid Json.
     */
    public static <T> T fromJson(MvcResult content, Class<T> contentClass) throws IOException {
        return fromJson(content.getResponse().getContentAsString(), contentClass);
    }

    /**
     * Deserializes an object from JSON.  
     * @param content The content itself.
     * @param contentClass The class of the result
     * @return The deserialized object.
     * @throws IOException Invalid Json.
     */
    public static <T> T fromJson(String content, Class<T> contentClass) throws IOException {
        if (contentClass == String.class && !content.startsWith("\"")) {
            return (T)content; // Hack for jackson?? 
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.readValue(content, contentClass);
    }

    /**
     * Gets the Authorization header to be used on requests.
     * 
     * @param mockMvc
     *            The MockMvc of the test
     * @param email
     *            The email of the user
     * @return The Value to be put in the Authorization header
     * @throws Exception
     *             Throws a test exception.
     */
    public static String getAuthValue(MockMvc mockMvc, PasswordEncoder passwordEncoderMock,
            String email) throws Exception {
        String password = email + "PASSWORD"; // Just a convention 4 testing
        Mockito.when(
                passwordEncoderMock.matches(password, User.ACTIVE_DIRECTORY_LOOKUP_PREFIX + email))
                .thenReturn(true);

        String json = json(new AccountCredentials(email, password));
        MvcResult res = mockMvc.perform(post("/login").content(json)).andExpect(status().isOk())
                .andReturn();
        String content = res.getResponse().getContentAsString();

        TokenResult tokenRes = fromJson(content, TokenResult.class);
        return "Bearer " + tokenRes.getToken();
    }
}
