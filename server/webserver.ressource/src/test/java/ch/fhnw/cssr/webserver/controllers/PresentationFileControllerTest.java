package ch.fhnw.cssr.webserver.controllers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Random;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ch.fhnw.cssr.domain.Presentation;
import ch.fhnw.cssr.domain.PresentationFile;
import ch.fhnw.cssr.domain.PresentationFileMeta;
import ch.fhnw.cssr.domain.Role;
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
public class PresentationFileControllerTest {

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

        presentationRepository.save(p);
    }

    @Test
    public void contexLoads() throws Exception {
        assertNotNull(presentationRepository);
    }

    @Test
    public void addFileByLink() throws Exception {
        String header = TestUtils.getAuthValue(mockMvc, passwordEncoder,
                "testie2@students.fhnw.ch");

        Presentation p = presentationRepository.findAll().iterator().next();
        String baseUrl = "/presentation/" + p.getPresentationId() + "/file";
        PresentationFileMeta file1 = new PresentationFileMeta(0, p.getPresentationId().intValue(),
                PresentationFile.TYPE_PRESENTATION, "http://www.google.ch", "Google", null);

        // Adds a file link
        MvcResult result = mockMvc
                .perform(post(baseUrl + "/link").header("Accept", "application/json")
                        .header("Authorization", header).contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.json(file1)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.contentLink", is("http://www.google.ch"))).andReturn();

        // Delete again
        PresentationFileMeta existing = TestUtils.fromJson(result, PresentationFileMeta.class);
        mockMvc.perform(delete(baseUrl + "/" + existing.getPresentationFileId())
                .header("Accept", "application/json").header("Authorization", header))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void addFileBinary() throws Exception {
        String header = TestUtils.getAuthValue(mockMvc, passwordEncoder,
                "testie2@students.fhnw.ch");

        Presentation p = presentationRepository.findAll().iterator().next();
        String baseUrl = "/presentation/" + p.getPresentationId() + "/file";

        String fullUrl = baseUrl + "/binary?type=" + PresentationFile.TYPE_RESSOURCEN
                + "&contentType=application/bla&displayName=somefilename";
        byte[] b = new byte[20];
        new Random().nextBytes(b);

        // Adds a file binary
        MvcResult result = mockMvc
                .perform(post(fullUrl).header("Accept", "application/json")
                        .header("Authorization", header)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM).content(b))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.contentLink", is((String) null))).andReturn();

        PresentationFileMeta existing = TestUtils.fromJson(result, PresentationFileMeta.class);

        

        // Get temptoken
        MvcResult tokenResult = mockMvc.perform(
                get("/user/me/tempToken")
                .header("Accept", "application/json")
                .header("Authorization", header))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        String tempTokenStr = TestUtils.fromJson(tokenResult, String.class);
        
        String fullUrlGet = "/presentation/" + p.getPresentationId() + "/file/"
                + existing.getPresentationFileId() 
                + "?tempToken=" + tempTokenStr;
        
        // Get it 
        mockMvc.perform(
                get(fullUrlGet).header("Accept", "application/json"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType("application/bla"))
                .andExpect(MockMvcResultMatchers.content().bytes(b));

        // Delete again
        mockMvc.perform(delete(baseUrl + "/" + existing.getPresentationFileId())
                .header("Accept", "application/json").header("Authorization", header))
                .andExpect(status().is2xxSuccessful());
    }
}
