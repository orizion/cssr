package ch.fhnw.cssr.webserver.controllers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isNull;
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
import java.util.Random;

import org.apache.http.entity.ContentType;
import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.fhnw.cssr.domain.EmailView;
import ch.fhnw.cssr.domain.Presentation;
import ch.fhnw.cssr.domain.PresentationFile;
import ch.fhnw.cssr.domain.PresentationFileMeta;
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
public class PresentationMailControllerTest {

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
    public void getMailTemplate() throws Exception {
        String header = TestUtils.getAuthValue(mockMvc, passwordEncoder,
                "testie2@students.fhnw.ch");

        Presentation p = presentationRepository.findAll().iterator().next();
        String templateUrl = "/presentation/" + p.getPresentationId() + "/invitation/template";
        
        
        mockMvc
                .perform(get(templateUrl).header("Accept", "application/json")
                        .header("Authorization", header)
                        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.subject", Matchers.notNullValue()))
                .andExpect(jsonPath("$.body", Matchers.notNullValue()))
                .andDo(r -> {
                    EmailView ev = TestUtils.fromJson(r.getResponse().getContentAsString(), 
                            EmailView.class);
                    System.out.println(ev.getBody());
                });
    }

    @Test
    public void saveMailTemplate() throws Exception {
        
        EmailView toSave = new EmailView();
        toSave.setBcc("hans@heiri.ch");
        toSave.setBody("aosdfijwoeifjoij");
        toSave.setCc("cc@cc.cc");
        toSave.setSubject("oijoij");
        toSave.setTo("asdofijweofij@oaisdjfaoij.oih");
        
        Presentation p = presentationRepository.findAll().iterator().next();
        String url = "/presentation/" + p.getPresentationId() + "/invitation/send";

        String header = TestUtils.getAuthValue(mockMvc, passwordEncoder,
                "testie2@students.fhnw.ch");

        
        mockMvc
                .perform(post(url).header("Accept", "application/json")
                        .header("Authorization", header)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.json(toSave))
                        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.emailId", Matchers.notNullValue()))
                .andExpect(jsonPath("$.subject", is("oijoij")))
                ;
    }

}
