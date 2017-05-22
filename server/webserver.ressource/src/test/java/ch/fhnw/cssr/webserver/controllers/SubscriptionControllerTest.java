package ch.fhnw.cssr.webserver.controllers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import ch.fhnw.cssr.domain.Presentation;
import ch.fhnw.cssr.domain.Role;
import ch.fhnw.cssr.domain.Subscription;
import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.repository.EmailRepository;
import ch.fhnw.cssr.domain.repository.PresentationFileRepository;
import ch.fhnw.cssr.domain.repository.PresentationRepository;
import ch.fhnw.cssr.domain.repository.SubscriptionRepository;
import ch.fhnw.cssr.domain.repository.UserRepository;
import ch.fhnw.cssr.security.CustomPasswordEncoder;
import ch.fhnw.cssr.test.TestUtils;

/**
 * Unit test for simple App.
 */
@RunWith(SpringRunner.class)
// @WebMvcTest(PresentationController.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(replace = Replace.ANY)
@AutoConfigureMockMvc
@SuppressWarnings("unused")
public class SubscriptionControllerTest {

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

        Presentation p = new Presentation();
        p.setAbstract("test abstract");
        p.setDateTime(LocalDateTime.now().plusDays(3));
        p.setLocation("here");
        p.setSpeakerId(testUser.getUserId());
        p.setTitle("Test title 2");

        presentationRepository.save(p);
    }

    @Test
    public void contexLoads() throws Exception {
        assertNotNull(presentationRepository);
    }

    @Test
    public void addSubscription() throws Exception {

        Presentation p = presentationRepository.findAll().iterator().next();
        
        Subscription subs = new Subscription();
        subs.setDrink((byte) 1);
        subs.setPresentationId(p.getPresentationId());
        subs.setSandwichType(Subscription.TYPE_SANDWICH_MEAT);
        subs.setUser(null);
        
        String header = TestUtils.getAuthValue(mockMvc, passwordEncoder,
                "testie2@students.fhnw.ch");

        
        String baseUrl = "/presentation/" + p.getPresentationId() + "/subscription";

        mockMvc
                .perform(post(baseUrl).header("Accept", "application/json")
                        .header("Authorization", header).contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.json(subs)))
                .andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.drink", is(1)))
                .andExpect(jsonPath("$.sandwichType", is(Subscription.TYPE_SANDWICH_MEAT)))
                ;

        List<Subscription> subscriptionsDb =
                subscriptionRepository.findByPresentationId(p.getPresentationId());
        assertTrue(subscriptionsDb.size() > 0);
        
        Subscription existing = subscriptionsDb.get(0);
        existing.setSandwichType(Subscription.TYPE_SANDWICH_VEGI);
        mockMvc
                .perform(put(baseUrl).header("Accept", "application/json")
                        .header("Authorization", header).contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.json(existing)))
                .andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.drink", is(1)))
                .andExpect(jsonPath("$.sandwichType", is(Subscription.TYPE_SANDWICH_VEGI)));
        
        
        mockMvc
        .perform(delete(baseUrl + "/" + existing.getSubscriptionId())
                .header("Accept", "application/json")
                .header("Authorization", header))
                .andExpect(status().is2xxSuccessful());
    }

}
