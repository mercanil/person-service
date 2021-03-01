package com.mercan.person.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercan.person.entity.Person;
import com.mercan.person.exception.ObjectNotFound;
import com.mercan.person.service.PersonService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static com.mercan.helper.TestHelper.asJsonString;
import static com.mercan.helper.TestHelper.createTestPerson;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
@AutoConfigureMockMvc
class PersonControllerTest {
    private static final String PERSON_ENDPOINT = "/api/person";
    public static final String TEST_FIRSTNAME = "test-first-name";
    public static final String TEST_LASTNAME = "test-last-name";
    public static Person storedPerson;

    @MockBean
    PersonService personService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeAll
    public static void setup() {
        storedPerson = createTestPerson(TEST_FIRSTNAME, TEST_LASTNAME);
    }


    @Test
    public void get_all_persons_expect_success() throws Exception {
        when(personService.getPeople()).thenReturn(Arrays.asList(storedPerson));
        this.mockMvc
                .perform(get(PERSON_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("[0].firstName", is(TEST_FIRSTNAME)))
                .andExpect(jsonPath("[0].lastName", is(TEST_LASTNAME)));
        verify(personService, times(1)).getPeople();

    }

    @Test
    public void get_all_person_by_id_expect_success() throws Exception {
        long validPersonId = 1L;
        when(personService.getPerson(validPersonId)).thenReturn(storedPerson);
        this.mockMvc
                .perform(get(PERSON_ENDPOINT + "/" + validPersonId)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(TEST_FIRSTNAME)))
                .andExpect(jsonPath("$.lastName", is(TEST_LASTNAME)));
        verify(personService, times(1)).getPerson(validPersonId);
    }

    @Test
    public void get_all_person_by_id_expect_exception() throws Exception {
        long invalidPersonId = 1L;
        when(personService.getPerson(invalidPersonId)).thenThrow(new ObjectNotFound("person", invalidPersonId));
        this.mockMvc
                .perform(get(PERSON_ENDPOINT + "/" + invalidPersonId)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.errors[0]", is("Resource person with id " + invalidPersonId + " does not exist")));
        verify(personService, times(1)).getPerson(invalidPersonId);

    }

    @Test
    public void save_person_expect_success() throws Exception {
        Person createdPerson = Person.builder().lastName(TEST_LASTNAME).firstName(TEST_FIRSTNAME).build();

        when(personService.createPerson(any())).thenReturn(createdPerson);
        this.mockMvc
                .perform(post(PERSON_ENDPOINT)
                        .content(asJsonString(objectMapper, createdPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(TEST_FIRSTNAME)))
                .andExpect(jsonPath("$.lastName", is(TEST_LASTNAME)));
        verify(personService, times(1)).createPerson(any());

    }

    @Test
    public void save_person_expect_validation_error_for_firstName() throws Exception {

        Person createdPerson = Person.builder().lastName(TEST_LASTNAME).build();

        when(personService.createPerson(any())).thenReturn(createdPerson);
        this.mockMvc
                .perform(post(PERSON_ENDPOINT)
                        .content(asJsonString(objectMapper, createdPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.errors[0]", is("firstName: is mandatory")));
    }


    @Test
    public void save_person_expect_validation_error_for_lastName() throws Exception {

        Person createdPerson = Person.builder().firstName(TEST_FIRSTNAME).build();

        when(personService.createPerson(any())).thenReturn(createdPerson);
        this.mockMvc
                .perform(post(PERSON_ENDPOINT)
                        .content(asJsonString(objectMapper, createdPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.errors[0]", is("lastName: is mandatory")));
    }


    @Test
    public void update_person_expect_success() throws Exception {
        Person updatedPerson = Person.builder().lastName(TEST_LASTNAME).firstName(TEST_FIRSTNAME).build();
        long validPersonId = 1L;

        when(personService.updatePerson(eq(validPersonId), any())).thenReturn(updatedPerson);
        this.mockMvc
                .perform(put(PERSON_ENDPOINT + "/" + validPersonId)
                        .content(asJsonString(objectMapper, updatedPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(TEST_FIRSTNAME)))
                .andExpect(jsonPath("$.lastName", is(TEST_LASTNAME)));

        verify(personService, times(1)).updatePerson(eq(validPersonId), any());

    }


    @Test
    public void update_person_expect_validation_error_for_firstName() throws Exception {
        Person updatedPerson = Person.builder().lastName(TEST_LASTNAME).build();
        long validPersonId = 1L;

        when(personService.updatePerson(eq(validPersonId), any())).thenReturn(updatedPerson);
        this.mockMvc
                .perform(put(PERSON_ENDPOINT + "/" + validPersonId)
                        .content(asJsonString(objectMapper, updatedPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.errors[0]", is("firstName: is mandatory")));
    }

    @Test
    public void update_person_expect_validation_error_for_lastName() throws Exception {
        Person updatedPerson = Person.builder().firstName(TEST_FIRSTNAME).build();
        long validPersonId = 1L;

        when(personService.updatePerson(eq(validPersonId), any())).thenReturn(updatedPerson);
        this.mockMvc
                .perform(put(PERSON_ENDPOINT + "/" + validPersonId)
                        .content(asJsonString(objectMapper, updatedPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.errors[0]", is("lastName: is mandatory")));
    }


    @Test
    public void update_person_expect_exception() throws Exception {
        Person updatedPerson = Person.builder().firstName(TEST_FIRSTNAME).lastName(TEST_LASTNAME).build();
        long invalidPersonId = 1L;

        when(personService.updatePerson(eq(invalidPersonId), any())).thenThrow(new ObjectNotFound("person", invalidPersonId));
        this.mockMvc
                .perform(put(PERSON_ENDPOINT + "/" + invalidPersonId)
                        .content(asJsonString(objectMapper, updatedPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.errors[0]", is("Resource person with id " + invalidPersonId + " does not exist")));
        verify(personService, times(1)).updatePerson(eq(invalidPersonId), any());

    }


    @Test
    public void delete_person_expect_success() throws Exception {
        long validPersonId = 1L;
        doNothing().when(personService).delete(validPersonId);
        this.mockMvc
                .perform(delete(PERSON_ENDPOINT + "/" + validPersonId))
                .andExpect(status().isNoContent());

        verify(personService, times(1)).delete(validPersonId);
    }


    @Test
    public void delete_person_expect_exception() throws Exception {
        long invalidPersonId = 1L;
        doThrow(new ObjectNotFound("person", invalidPersonId)).when(personService).delete(invalidPersonId);
        this.mockMvc
                .perform(delete(PERSON_ENDPOINT + "/" + invalidPersonId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.errors[0]", is("Resource person with id " + invalidPersonId + " does not exist")));
        verify(personService, times(1)).delete(invalidPersonId);
    }
}