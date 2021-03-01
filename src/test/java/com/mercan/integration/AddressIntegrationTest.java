package com.mercan.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercan.person.entity.Address;
import com.mercan.person.entity.Person;
import com.mercan.person.repository.AddressRepository;
import com.mercan.person.repository.PersonRepository;
import com.mercan.person.service.AddressService;
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

import static com.mercan.helper.TestHelper.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AddressIntegrationTest {

    private static final String ADDRESS_ENDPOINT = "/api/person/%s/";
    private static final String TEST_CITY = "test-city";
    private static final String TEST_POSTAL_CODE = "test-postal-code";
    private static final String TEST_STATE = "test-state";
    private static final String TEST_STREET = "test-street";
    private static final String TEST_FIRST_NAME = "test-first-name";
    private static final String TEST_LAST_NAME = "test-last-name";
    private Person storedPerson;
    private Address storedAddress;

    @Autowired
    PersonService personService;

    @Autowired
    AddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AddressRepository addressRepository;


    @BeforeEach
    public void setupTest() {
        storedPerson = createTestPerson(TEST_FIRST_NAME, TEST_LAST_NAME);
        storedPerson = personRepository.save(storedPerson);

        storedAddress = createTestAddress(TEST_CITY, TEST_POSTAL_CODE, TEST_STATE, TEST_STREET);
        storedAddress.setPerson(storedPerson);
        storedAddress = addressRepository.save(storedAddress);
    }
    @AfterEach
    public void cleanUp(){
        addressRepository.delete(storedAddress);
    }

    @Test
    public void get_all_address_for_valid_person_expect_success() throws Exception {
        this.mockMvc
                .perform(get(String.format(ADDRESS_ENDPOINT, storedPerson.getId()) + "/address")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("[0].state", is(TEST_STATE)))
                .andExpect(jsonPath("[0].city", is(TEST_CITY)))
                .andExpect(jsonPath("[0].postalCode", is(TEST_POSTAL_CODE)))
                .andExpect(jsonPath("[0].street", is(TEST_STREET)));
    }

    @Test
    public void save_address_valid_person_expect_success() throws Exception {
        Address newAddress = Address.builder().city("new-city").postalCode("new-postal-code").state("new-state").street("new-street").person(storedPerson).build();
        this.mockMvc
                .perform(post(String.format(ADDRESS_ENDPOINT, storedPerson.getId()) + "/address")
                        .content(asJsonString(objectMapper, newAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.state", is("new-state")))
                .andExpect(jsonPath("$.city", is("new-city")))
                .andExpect(jsonPath("$.postalCode", is("new-postal-code")))
                .andExpect(jsonPath("$.street", is("new-street")));
    }


    @Test
    public void update_address_expect_success() throws Exception {
        Address newAddress = Address.builder().city("new-city").postalCode("new-postal-code").state("new-state").street("new-street").build();
        this.mockMvc
                .perform(put(String.format(ADDRESS_ENDPOINT, storedPerson.getId()) + "/address/" + storedAddress.getId())
                        .content(asJsonString(objectMapper, newAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state", is("new-state")))
                .andExpect(jsonPath("$.city", is("new-city")))
                .andExpect(jsonPath("$.postalCode", is("new-postal-code")))
                .andExpect(jsonPath("$.street", is("new-street")));
    }


    @Test
    public void delete_address_expect_success() throws Exception {
        this.mockMvc
                .perform(delete(String.format(ADDRESS_ENDPOINT, storedPerson.getId()) + "/address/" + storedAddress.getId()))
                .andExpect(status().isNoContent());

        assertThat(addressRepository.count(), is(0L));

    }
}
