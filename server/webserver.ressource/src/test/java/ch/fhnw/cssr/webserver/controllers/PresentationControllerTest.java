package ch.fhnw.cssr.webserver.controllers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.fhnw.cssr.domain.Presentation;
import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.repository.EmailRepository;
import ch.fhnw.cssr.domain.repository.PresentationFileRepository;
import ch.fhnw.cssr.domain.repository.PresentationRepository;
import ch.fhnw.cssr.domain.repository.SubscriptionRepository;
import ch.fhnw.cssr.domain.repository.UserRepository;
import ch.fhnw.cssr.domain.repository.UserRolesRepository;
import ch.fhnw.cssr.security.StudentUserDetails;
import ch.fhnw.cssr.security.jwt.AccountCredentials;
import ch.fhnw.cssr.security.jwt.TokenResult;
import ch.fhnw.cssr.webserver.App;

/**
 * Unit test for simple App.
 */
@RunWith(SpringRunner.class)
// @WebMvcTest(PresentationController.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(replace = Replace.ANY)
@AutoConfigureMockMvc
public class PresentationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRolesRepository userRolesRepository;

    @Autowired
    private PresentationFileRepository presentationFileRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @MockBean
    private EmailRepository emailRepository;

    /**
     * Sets up the mocks.
     */
    @Before
    public void setUp() {
        userRepository.deleteAll();
        presentationRepository.deleteAll();
        
        User testUser = new StudentUserDetails(1000, "testie2@students.fhnw.ch").copy();
        
        userRepository.save(testUser);
        User speaker = new StudentUserDetails(0, "speakie@students.fhnw.ch").copy();
        userRepository.save(speaker);

        Presentation p = new Presentation();
        p.setAbstract("test abstract");
        p.setDateTime(LocalDateTime.now().plusDays(3));
        p.setLocation("here");
        p.setSpeakerId(speaker.getUserId());
        p.setTitle("Test title 2");
        
        Presentation p2 = new Presentation();
        p2.setAbstract("test abstract yesterday");
        p2.setDateTime(LocalDateTime.now().minusDays(3));
        p2.setLocation("there");
        p2.setSpeakerId(speaker.getUserId());
        p2.setTitle("Test title 2");

        presentationRepository.save(p);
        presentationRepository.save(p2);

    }

    @Test
    public void contexLoads() throws Exception {
        assertNotNull(presentationRepository);
    }
    
    private String getAuthValue(String email) throws Exception {
        String password = email + "PASSWORD"; // Just a convention
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(new AccountCredentials(email, password));
        MvcResult res = mockMvc.perform(post("/login").content(json))
            .andExpect(status().isOk())
            .andReturn();
        String content = res.getResponse().getContentAsString();
        TokenResult tokenRes = mapper.readValue(content, TokenResult.class);
        return "Bearer " + tokenRes.getToken();
    }

    @Test
    public void findAllPresentations() throws Exception {
        String header = getAuthValue("testie2@students.fhnw.ch");        

        mockMvc.perform(get("/presentation?futureOnly=false").header("Accept", "application/json")
                .header("Authorization", header))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].abstract", is("test abstract")))
                .andExpect(jsonPath("$[0].location", is("here")))
                .andExpect(jsonPath("$[1].abstract", is("test abstract yesterday")))
                .andExpect(jsonPath("$[1].location", is("there")))
                .andExpect(jsonPath("$.length()", is(2)));
        
    }
    
    @Test
    public void findFuturePresentations() throws Exception {
        String header = getAuthValue("testie2@students.fhnw.ch");
        
        mockMvc.perform(get("/presentation?futureOnly=true").header("Accept", "application/json")
                    .header("Authorization", header))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].abstract", is("test abstract")))
                .andExpect(jsonPath("$[0].location", is("here")))
                .andExpect(jsonPath("$.length()", is(1)));
    }
}
