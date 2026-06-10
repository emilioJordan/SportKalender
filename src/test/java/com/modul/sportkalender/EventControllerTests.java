package com.modul.sportkalender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modul.sportkalender.event.Event;
import com.modul.sportkalender.event.EventRepository;
import com.modul.sportkalender.event.EventService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventControllerTests {

    @Autowired
    private MockMvc api;

    @Autowired
    private EventRepository eventRepository;

    private String accessToken;

    private Long createdEventId;

    @BeforeAll
    void setup() {
        accessToken = obtainAccessToken();
    }

    @Test
    @Order(1)
    void testGetEvents() throws Exception {
        api.perform(get("/api/event")
                        .header("Authorization", "Bearer " + accessToken)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Init Event")));
    }

    @Test
    @Order(2)
    void testCreateEvent() throws Exception {
        Event event = new Event();
        event.setTitle("Test Event");
        event.setLocation("Testort");
        event.setDateTime(LocalDateTime.now().plusDays(1));

        String body = new ObjectMapper().writeValueAsString(event);

        String response = api.perform(post("/api/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + accessToken)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Test Event")))
                .andReturn().getResponse().getContentAsString();

        Event saved = new ObjectMapper().readValue(response, Event.class);
        createdEventId = saved.getId();
    }

    @Test
    @Order(3)
    void testUpdateEvent() throws Exception {
        Event event = new Event();
        event.setTitle("Updated Event");
        event.setLocation("Neuer Ort");
        event.setDateTime(LocalDateTime.now().plusDays(2));

        String body = new ObjectMapper().writeValueAsString(event);

        api.perform(put("/api/event/" + createdEventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + accessToken)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Updated Event")));
    }

    @Test
    @Order(4)
    void testDeleteEvent() throws Exception {
        api.perform(delete("/api/event/" + createdEventId)
                        .header("Authorization", "Bearer " + accessToken)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    private String obtainAccessToken() {
        RestTemplate rest = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=sportkalender&" +
                "grant_type=password&" +
                "scope=openid profile roles offline_access&" +
                "username=admin&" +
                "password=admin";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = rest.postForEntity(
                "http://localhost:8080/realms/Sportkalender/protocol/openid-connect/token",
                entity,
                String.class
        );

        JacksonJsonParser parser = new JacksonJsonParser();
        return parser.parseMap(response.getBody()).get("access_token").toString();
    }
}
