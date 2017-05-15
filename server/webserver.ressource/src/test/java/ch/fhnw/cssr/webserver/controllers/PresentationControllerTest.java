package ch.fhnw.cssr.webserver.controllers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
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
import ch.fhnw.cssr.domain.Role;
import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.UserAddMeta;
import ch.fhnw.cssr.domain.repository.EmailRepository;
import ch.fhnw.cssr.domain.repository.PresentationFileRepository;
import ch.fhnw.cssr.domain.repository.PresentationRepository;
import ch.fhnw.cssr.domain.repository.SubscriptionRepository;
import ch.fhnw.cssr.domain.repository.UserRepository;
import ch.fhnw.cssr.security.CustomPasswordEncoder;
import ch.fhnw.cssr.security.EwsAuthenticator;
import ch.fhnw.cssr.security.jwt.AccountCredentials;
import ch.fhnw.cssr.security.jwt.TokenResult;
import ch.fhnw.cssr.test.TestUtils;
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
    private PresentationFileRepository presentationFileRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @MockBean
    private EmailRepository emailRepository;

    @MockBean
    private CustomPasswordEncoder passwordEncoder;

    
    /**
     * Sets up the mocks.
     */
    @Before
    public void setUp() {
        userRepository.deleteAll();
        presentationRepository.deleteAll();
        
        User testUser = new User(1000, "testie2@students.fhnw.ch", "Testie").copy();
        testUser.setRoleId(Role.ROLE_COORD);
        userRepository.save(testUser);
        User speaker = new User(0, "speakie@students.fhnw.ch", "Speakie").copy();
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
    

    @Test
    public void findAllPresentations() throws Exception {
        String header = TestUtils.getAuthValue(mockMvc, passwordEncoder, 
                "testie2@students.fhnw.ch");        

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
        String header = TestUtils.getAuthValue(mockMvc, passwordEncoder, 
                "testie2@students.fhnw.ch");
        
        mockMvc.perform(get("/presentation?futureOnly=true").header("Accept", "application/json")
                    .header("Authorization", header))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].abstract", is("test abstract")))
                .andExpect(jsonPath("$[0].location", is("here")))
                .andExpect(jsonPath("$.length()", is(1)));
    }
    

    @Test
    public void addPresentation() throws Exception {
        String header = TestUtils.getAuthValue(mockMvc, passwordEncoder, 
                "testie2@students.fhnw.ch");
        

        // Add user
        MvcResult result = mockMvc.perform(post("/admin/user").header("Accept", "application/json")
                .header("Authorization", header)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.json(
                        new UserAddMeta("speakie2@students.fhnw.ch", "2nd Speaker")))
                )
            .andExpect(status().isOk())
            .andReturn();
        
        // Get User id
        Long id = new Long(result.getResponse().getContentAsString());
        
        // Construct pres
        Presentation p2 = new Presentation();
        p2.setAbstract("test abstract 2");
        p2.setDateTime(LocalDateTime.now().plusDays(54));
        p2.setLocation("there 2");
        p2.setSpeakerId(id);
        p2.setTitle("Test title 222");
        
        // Save it 
        MvcResult presentationResult = mockMvc.perform(post("/presentation")
                .header("Accept", "application/json")
                    .header("Authorization", header)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.json(p2))
                    )
                .andDo(r -> {
                    System.out.println(r.getResponse().getContentAsString());
                })
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.abstract", is("test abstract 2")))
                .andExpect(jsonPath("$.location", is("there 2")))
                .andExpect(jsonPath("$.title", is("Test title 222")))
                .andReturn();

        Presentation existing = TestUtils.fromJson(
                presentationResult.getResponse().getContentAsString(),
                Presentation.class);
        existing.setTitle("test title 123");
        
        mockMvc.perform(put("/presentation")
                .header("Accept", "application/json")
                    .header("Authorization", header)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.json(existing))
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("test title 123")))
                .andExpect(jsonPath("$.presentationId", is(existing.getPresentationId())));   
    }
}
