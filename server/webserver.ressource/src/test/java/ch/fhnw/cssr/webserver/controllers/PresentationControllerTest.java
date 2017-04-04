package ch.fhnw.cssr.webserver.controllers;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import ch.fhnw.cssr.domain.Presentation;
import ch.fhnw.cssr.domain.PresentationRepository;

/**
 * Unit test for simple App.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(PresentationController.class)
public class PresentationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PresentationRepository presentationRepositoryMock;

    @Before
    public void setUp() {
        Mockito.reset(presentationRepositoryMock);
    }

    @Test
    public void findAll_QuestionnairesFound_ShouldReturnFound() throws Exception {
        Presentation p = new Presentation();
        p.setAbstract("test abstract");
        p.setDateTime(LocalDateTime.now().plusDays(3));
        p.setLocation("here");

        Presentation p2 = new Presentation();
        p2.setAbstract("test abstract yesterday");
        p2.setDateTime(LocalDateTime.now().minusDays(3));
        p2.setLocation("there");

        when(presentationRepositoryMock.findAll()).thenReturn(Arrays.asList(p, p2));
        when(presentationRepositoryMock.getAllFuture()).thenReturn(Arrays.asList(p));

        mockMvc.perform(get("/presentation?futureOnly=true").header("Accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].abstract", is("test abstract")))
                .andExpect(jsonPath("$[0].location", is("here")))
                .andExpect(jsonPath("$.length", is(1)));

        mockMvc.perform(get("/presentation?futureOnly=false").header("Accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].abstract", is("test abstract")))
                .andExpect(jsonPath("$[0].location", is("here")))
                .andExpect(jsonPath("$[1].abstract", is("test abstract yesterday")))
                .andExpect(jsonPath("$[1].location", is("there")))
                .andExpect(jsonPath("$.length", is(2)));
    }
}