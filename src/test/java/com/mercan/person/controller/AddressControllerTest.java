package com.mercan.person.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercan.person.entity.Address;
import com.mercan.person.exception.ObjectNotFound;
import com.mercan.person.service.AddressService;
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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc
class AddressControllerTest {

    private static final String ADDRESS_ENDPOINT = "/api/person/%s/";
    private static final String TEST_CITY = "test-city";
    private static final String TEST_POSTAL_CODE = "test-postal-code";
    private static final String TEST_STATE = "test-state";
    private static final String TEST_STREET = "test-street";
    public static Address expectedAddress;

    @MockBean
    PersonService personService;

    @MockBean
    AddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setup() {
        expectedAddress = Address.builder().city(TEST_CITY).postalCode(TEST_POSTAL_CODE).state(TEST_STATE).street(TEST_STREET).build();
    }

    @Test
    public void get_all_address_for_valid_person_expect_success() throws Exception {
        Long validPersonId = 1L;
        when(addressService.getAddress(validPersonId)).thenReturn(Arrays.asList(expectedAddress));
        this.mockMvc
                .perform(get(String.format(ADDRESS_ENDPOINT, validPersonId) + "/address")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("[0].state", is(TEST_STATE)))
                .andExpect(jsonPath("[0].city", is(TEST_CITY)))
                .andExpect(jsonPath("[0].postalCode", is(TEST_POSTAL_CODE)))
                .andExpect(jsonPath("[0].street", is(TEST_STREET)));
        verify(addressService, times(1)).getAddress(validPersonId);
    }


    @Test
    public void get_all_address_for_invalid_person_expect_exception() throws Exception {
        Long invalidPersonId = 1L;
        when(addressService.getAddress(invalidPersonId)).thenThrow(new ObjectNotFound("person", invalidPersonId));
        this.mockMvc
                .perform(get(String.format(ADDRESS_ENDPOINT, invalidPersonId) + "/address")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.errors[0]", is("Resource person with id " + invalidPersonId + " does not exist")));
        verify(addressService, times(1)).getAddress(invalidPersonId);

    }


    @Test
    public void save_address_valid_person_expect_success() throws Exception {
        Long validPersonId = 1L;
        Address requestedAddress = Address.builder().city(TEST_CITY).postalCode(TEST_POSTAL_CODE).state(TEST_STATE).street(TEST_STREET).build();

        when(addressService.createAddress(eq(validPersonId), any())).thenReturn(expectedAddress);
        this.mockMvc
                .perform(post(String.format(ADDRESS_ENDPOINT, validPersonId) + "/address")
                        .content(asJsonString(objectMapper, requestedAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.state", is(TEST_STATE)))
                .andExpect(jsonPath("$.city", is(TEST_CITY)))
                .andExpect(jsonPath("$.postalCode", is(TEST_POSTAL_CODE)))
                .andExpect(jsonPath("$.street", is(TEST_STREET)));
        verify(addressService, times(1)).createAddress(eq(validPersonId), any());

    }


    @Test
    public void save_address_invalid_person_expect_exception() throws Exception {
        Long invalidPersonId = 1L;
        Address requestedAddress = Address.builder().city(TEST_CITY).postalCode(TEST_POSTAL_CODE).state(TEST_STATE).street(TEST_STREET).build();

        when(addressService.createAddress(eq(invalidPersonId), any())).thenThrow(new ObjectNotFound("person", invalidPersonId));
        this.mockMvc
                .perform(post(String.format(ADDRESS_ENDPOINT, invalidPersonId) + "/address")
                        .content(asJsonString(objectMapper, requestedAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.errors[0]", is("Resource person with id " + invalidPersonId + " does not exist")));
        verify(addressService, times(1)).createAddress(eq(invalidPersonId), any());

    }

    @Test
    public void save_address_expect_validation_error_for_state() throws Exception {
        Long validPersonId = 1L;
        Address requestedAddress = Address.builder().city(TEST_CITY).postalCode(TEST_POSTAL_CODE).street(TEST_STREET).build();

        when(addressService.createAddress(eq(validPersonId), any())).thenReturn(expectedAddress);
        this.mockMvc
                .perform(post(String.format(ADDRESS_ENDPOINT, validPersonId) + "/address")
                        .content(asJsonString(objectMapper, requestedAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.errors[0]", is("state: is mandatory")));

    }

    @Test
    public void save_address_expect_validation_error_for_city() throws Exception {
        Long validPersonId = 1L;
        Address requestedAddress = Address.builder().state(TEST_STATE).postalCode(TEST_POSTAL_CODE).street(TEST_STREET).build();

        when(addressService.createAddress(eq(validPersonId), any())).thenReturn(expectedAddress);
        this.mockMvc
                .perform(post(String.format(ADDRESS_ENDPOINT, validPersonId) + "/address")
                        .content(asJsonString(objectMapper, requestedAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.errors[0]", is("city: is mandatory")));

    }

    @Test
    public void save_address_expect_validation_error_for_postal_code() throws Exception {
        Long validPersonId = 1L;
        Address requestedAddress = Address.builder().state(TEST_STATE).city(TEST_CITY).street(TEST_STREET).build();

        when(addressService.createAddress(eq(validPersonId), any())).thenReturn(expectedAddress);
        this.mockMvc
                .perform(post(String.format(ADDRESS_ENDPOINT, validPersonId) + "/address")
                        .content(asJsonString(objectMapper, requestedAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.errors[0]", is("postalCode: is mandatory")));

    }

    @Test
    public void save_address_expect_validation_error_for_street() throws Exception {
        Long validPersonId = 1L;
        Address requestedAddress = Address.builder().state(TEST_STATE).city(TEST_CITY).postalCode(TEST_POSTAL_CODE).build();

        when(addressService.createAddress(eq(validPersonId), any())).thenReturn(expectedAddress);
        this.mockMvc
                .perform(post(String.format(ADDRESS_ENDPOINT, validPersonId) + "/address")
                        .content(asJsonString(objectMapper, requestedAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.errors[0]", is("street: is mandatory")));

    }


    @Test
    public void update_address_expect_success() throws Exception {

        Long validPersonId = 1L;
        Long validAddressId = 2L;
        Address requestedAddress = Address.builder().city(TEST_CITY).postalCode(TEST_POSTAL_CODE).state(TEST_STATE).street(TEST_STREET).build();

        when(addressService.updateAddress(eq(validPersonId), eq(validAddressId), any())).thenReturn(expectedAddress);
        this.mockMvc
                .perform(put(String.format(ADDRESS_ENDPOINT, validPersonId) + "/address/" + validAddressId)
                        .content(asJsonString(objectMapper, requestedAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state", is(TEST_STATE)))
                .andExpect(jsonPath("$.city", is(TEST_CITY)))
                .andExpect(jsonPath("$.postalCode", is(TEST_POSTAL_CODE)))
                .andExpect(jsonPath("$.street", is(TEST_STREET)));
        verify(addressService, times(1)).updateAddress(eq(validPersonId), eq(validAddressId), any());

    }

    @Test
    public void update_address_invalid_PersonId_expect_exception() throws Exception {

        Long invalidPersonId = 1L;
        Long validAddressId = 2L;
        Address requestedAddress = Address.builder().city(TEST_CITY).postalCode(TEST_POSTAL_CODE).state(TEST_STATE).street(TEST_STREET).build();

        when(addressService.updateAddress(eq(invalidPersonId), eq(validAddressId), any())).thenThrow(new ObjectNotFound("person", invalidPersonId));
        this.mockMvc
                .perform(put(String.format(ADDRESS_ENDPOINT, invalidPersonId) + "/address/" + validAddressId)
                        .content(asJsonString(objectMapper, requestedAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.errors[0]", is("Resource person with id " + invalidPersonId + " does not exist")));
        verify(addressService, times(1)).updateAddress(eq(invalidPersonId), eq(validAddressId), any());

    }


    @Test
    public void update_address_invalid_addressId_expect_exception() throws Exception {

        Long validPersonId = 1L;
        Long invalidAddressId = 2L;
        Address requestedAddress = Address.builder().city(TEST_CITY).postalCode(TEST_POSTAL_CODE).state(TEST_STATE).street(TEST_STREET).build();

        when(addressService.updateAddress(eq(validPersonId), eq(invalidAddressId), any())).thenThrow(new ObjectNotFound("address", invalidAddressId));
        this.mockMvc
                .perform(put(String.format(ADDRESS_ENDPOINT, validPersonId) + "/address/" + invalidAddressId)
                        .content(asJsonString(objectMapper, requestedAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.errors[0]", is("Resource address with id " + invalidAddressId + " does not exist")));
        verify(addressService, times(1)).updateAddress(eq(validPersonId), eq(invalidAddressId), any());

    }

    @Test
    public void update_address_expect_validation_error_for_street() throws Exception {
        long validPersonId = 1L;
        long validAddressId = 2L;
        Address requestedAddress = Address.builder().state(TEST_STATE).city(TEST_CITY).postalCode(TEST_POSTAL_CODE).build();

        when(addressService.createAddress(eq(validPersonId), any())).thenReturn(expectedAddress);
        this.mockMvc
                .perform(put(String.format(ADDRESS_ENDPOINT, validPersonId) + "/address/" + validAddressId)
                        .content(asJsonString(objectMapper, requestedAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.errors[0]", is("street: is mandatory")));
    }


    @Test
    public void update_address_expect_validation_error_for_postal_code() throws Exception {
        long validPersonId = 1L;
        long validAddressId = 2L;
        Address requestedAddress = Address.builder().state(TEST_STATE).city(TEST_CITY).street(TEST_STREET).build();

        when(addressService.createAddress(eq(validPersonId), any())).thenReturn(expectedAddress);
        this.mockMvc
                .perform(put(String.format(ADDRESS_ENDPOINT, validPersonId) + "/address/" + validAddressId)
                        .content(asJsonString(objectMapper, requestedAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.errors[0]", is("postalCode: is mandatory")));

    }

    @Test
    public void update_address_expect_validation_error_for_state() throws Exception {
        long validPersonId = 1L;
        long validAddressId = 2L;
        Address requestedAddress = Address.builder().postalCode(TEST_POSTAL_CODE).city(TEST_CITY).street(TEST_STREET).build();

        when(addressService.createAddress(eq(validPersonId), any())).thenReturn(expectedAddress);
        this.mockMvc
                .perform(put(String.format(ADDRESS_ENDPOINT, validPersonId) + "/address/" + validAddressId)
                        .content(asJsonString(objectMapper, requestedAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.errors[0]", is("state: is mandatory")));

    }

    @Test
    public void update_address_expect_validation_error_for_city() throws Exception {
        long validPersonId = 1L;
        long validAddressId = 2L;
        Address requestedAddress = Address.builder().postalCode(TEST_POSTAL_CODE).state(TEST_STATE).street(TEST_STREET).build();

        when(addressService.createAddress(eq(validPersonId), any())).thenReturn(expectedAddress);
        this.mockMvc
                .perform(put(String.format(ADDRESS_ENDPOINT, validPersonId) + "/address/" + validAddressId)
                        .content(asJsonString(objectMapper, requestedAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.errors[0]", is("city: is mandatory")));

    }


    @Test
    public void delete_address_expect_success() throws Exception {
        long validPersonId = 1L;
        Long validAddressId = 2L;
        doNothing().when(addressService).deleteAddress(validPersonId, validAddressId);
        this.mockMvc
                .perform(delete(String.format(ADDRESS_ENDPOINT, validPersonId) + "/address/" + validAddressId))
                .andExpect(status().isNoContent());

        verify(addressService, times(1)).deleteAddress(validAddressId , validPersonId);

    }

    @Test
    public void delete_address_expect_exception_for_invalid_person() throws Exception {
        long invalidPersonId = 1L;
        Long validAddressId = 2L;
        doThrow(new ObjectNotFound("person", invalidPersonId)).when(addressService).deleteAddress(validAddressId, invalidPersonId);

        this.mockMvc
                .perform(delete(String.format(ADDRESS_ENDPOINT, invalidPersonId) + "/address/" + validAddressId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.errors[0]", is("Resource person with id " + invalidPersonId + " does not exist")));
        verify(addressService, times(1)).deleteAddress(validAddressId, invalidPersonId);

    }


    @Test
    public void delete_address_expect_exception_for_invalid_address() throws Exception {
        long validPersonId = 1L;
        Long invalidAddressId = 2L;
        doThrow(new ObjectNotFound("address", invalidAddressId)).when(addressService).deleteAddress(invalidAddressId, validPersonId);

        this.mockMvc
                .perform(delete(String.format(ADDRESS_ENDPOINT, validPersonId) + "/address/" + invalidAddressId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reasonCode", is(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.errors[0]", is("Resource address with id " + invalidAddressId + " does not exist")));
        verify(addressService, times(1)).deleteAddress(invalidAddressId, validPersonId);

    }
}