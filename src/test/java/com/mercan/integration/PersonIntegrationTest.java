package com.mercan.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercan.person.entity.Person;
import com.mercan.person.repository.PersonRepository;
import com.mercan.person.service.PersonService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.mercan.helper.TestHelper.asJsonString;
import static com.mercan.helper.TestHelper.createTestPerson;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PersonIntegrationTest {
    private static final String PERSON_ENDPOINT = "/api/person";
    private static final String TEST_FIRST_NAME = "test-first-name";
    private static final String TEST_LAST_NAME = "test-last-name";
    private Person storedPerson;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    public void setupTest() {
        storedPerson = createTestPerson(TEST_FIRST_NAME, TEST_LAST_NAME);
        personRepository.save(storedPerson);
    }

    @AfterEach
    public void cleanUp(){
        personRepository.delete(storedPerson);
    }

    @Test
    public void get_all_persons_expect_success() throws Exception {

        this.mockMvc
                .perform(get(PERSON_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].firstName", is(TEST_FIRST_NAME)))
                .andExpect(jsonPath("[0].lastName", is(TEST_LAST_NAME)));
    }


    @Test
    public void save_person_expect_success() throws Exception {
        Person newPerson = Person.builder().firstName("new-person-first-name").lastName("new-person-last-name").build();
        this.mockMvc
                .perform(post(PERSON_ENDPOINT)
                        .content(asJsonString(objectMapper, newPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("new-person-first-name")))
                .andExpect(jsonPath("$.lastName", is("new-person-last-name")));
    }

    @Test
    public void update_person_expect_success() throws Exception {
        //given
        Person updatedPerson = Person.builder().firstName("updated-first-name").lastName("updated-last-name").build();

        this.mockMvc
                .perform(put(PERSON_ENDPOINT + "/" + storedPerson.getId())
                        .content(asJsonString(objectMapper, updatedPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("updated-first-name")))
                .andExpect(jsonPath("$.lastName", is("updated-last-name")));
    }

    @Test
    public void delete_person_expect_success() throws Exception {
        this.mockMvc
                .perform(delete(PERSON_ENDPOINT + "/" + storedPerson.getId()))
                .andExpect(status().isNoContent());
    }
}
