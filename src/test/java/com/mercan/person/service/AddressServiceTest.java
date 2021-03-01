package com.mercan.person.service;

import com.mercan.person.entity.Address;
import com.mercan.person.entity.Person;
import com.mercan.person.exception.ObjectNotFound;
import com.mercan.person.repository.AddressRepository;
import com.mercan.person.repository.PersonRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.mercan.helper.TestHelper.createTestAddress;
import static com.mercan.helper.TestHelper.createTestPerson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    private static final String TEST_CITY = "test-city";
    private static final String TEST_POSTAL_CODE = "test-postal-code";
    private static final String TEST_STATE = "test-state";
    private static final String TEST_STREET = "test-street";
    private static final Long TEST_PERSON_ID = 1L;
    private static final String TEST_FIRST_NAME = "test-first-name";
    private static final String TEST_LAST_NAME = "test-last-name";
    private static final Long TEST_ADDRESS_ID = 2L;
    private static Person person;
    private static Address address;

    @Mock
    PersonRepository personRepository;

    @Mock
    AddressRepository addressRepository;

    @InjectMocks
    AddressService addressService;


    @BeforeEach
    public void setup() {
        person = createTestPerson(TEST_FIRST_NAME, TEST_LAST_NAME);
        address = createTestAddress(TEST_CITY, TEST_POSTAL_CODE, TEST_STATE, TEST_STREET);

    }

    @Test
    public void get_address_expect_success() {

        //given
        Address requestedAddress = Address.builder().city(TEST_CITY).postalCode(TEST_POSTAL_CODE).state(TEST_STATE).street(TEST_STREET).build();
        given(personRepository.existsById(TEST_PERSON_ID)).willReturn(true);
        given(addressRepository.findAllByPersonId(TEST_PERSON_ID)).willReturn(Arrays.asList(requestedAddress));

        //when
        List<Address> address = addressService.getAddress(TEST_PERSON_ID);

        //then
        assertThat(address, is(Arrays.asList(requestedAddress)));
        verify(personRepository, times(1)).existsById(TEST_PERSON_ID);
        verify(addressRepository, times(1)).findAllByPersonId(TEST_PERSON_ID);

    }

    @Test
    public void create_address_expect_success() {

        //given
        Address requestedAddress = createTestAddress(TEST_CITY, TEST_POSTAL_CODE, TEST_STATE, TEST_STREET);

        given(personRepository.findById(TEST_PERSON_ID)).willReturn(Optional.of(person));
        given(addressRepository.save(requestedAddress)).willReturn(requestedAddress);

        //when
        Address address = addressService.createAddress(TEST_PERSON_ID, requestedAddress);

        //then
        assertThat(address, is(requestedAddress));
        verify(personRepository, times(1)).findById(TEST_PERSON_ID);


    }

    @Test
    public void create_address_expect_exception_when_person_does_not_exist() {

        //given

        given(personRepository.findById(TEST_PERSON_ID)).willReturn(Optional.empty());


        //then
        Assertions.assertThrows(ObjectNotFound.class, () -> addressService.createAddress(TEST_PERSON_ID, address));
        verify(personRepository, times(1)).findById(TEST_PERSON_ID);
    }

    @Test
    public void update_address_expect_success() {

        //given
        Address requestedAddress = createTestAddress(TEST_CITY, TEST_POSTAL_CODE, TEST_STATE, TEST_STREET);
        Address storedAddress = createTestAddress(TEST_CITY, TEST_POSTAL_CODE, TEST_STATE, TEST_STREET);

        given(personRepository.existsById(TEST_PERSON_ID)).willReturn(true);
        given(addressRepository.save(any())).willReturn(storedAddress);
        given(addressRepository.findById(TEST_ADDRESS_ID)).willReturn(Optional.of(storedAddress));

        //when
        Address address = addressService.updateAddress(TEST_PERSON_ID, TEST_ADDRESS_ID, requestedAddress);

        //then
        assertThat(address, is(storedAddress));
        verify(personRepository, times(1)).existsById(TEST_PERSON_ID);
    }

    @Test
    public void update_address_expect_exception_when_person_does_not_exist() {

        //given
        Address requestedAddress = createTestAddress(TEST_CITY, TEST_POSTAL_CODE, TEST_STATE, TEST_STREET);

        given(personRepository.existsById(TEST_PERSON_ID)).willReturn(false);

        //then
        Assertions.assertThrows(ObjectNotFound.class, () -> addressService.updateAddress(TEST_PERSON_ID, TEST_ADDRESS_ID, requestedAddress));
        verify(personRepository, times(1)).existsById(TEST_PERSON_ID);
        verify(addressRepository, times(0)).save(requestedAddress);
    }

    @Test
    public void update_address_expect_exception_when_address_does_not_exist() {

        //given
        Address requestedAddress = createTestAddress(TEST_CITY, TEST_POSTAL_CODE, TEST_STATE, TEST_STREET);

        given(personRepository.existsById(TEST_PERSON_ID)).willReturn(true);
        given(addressRepository.findById(TEST_ADDRESS_ID)).willReturn(Optional.empty());

        //then
        Assertions.assertThrows(ObjectNotFound.class, () -> addressService.updateAddress(TEST_PERSON_ID, TEST_ADDRESS_ID, requestedAddress));
        verify(personRepository, times(1)).existsById(TEST_PERSON_ID);
        verify(addressRepository, times(0)).save(requestedAddress);
    }


    @Test
    public void delete_address_expect_success() {

        //given

        given(addressRepository.findAllByIdAndPersonId(TEST_ADDRESS_ID, TEST_PERSON_ID)).willReturn(Optional.of(address));

        //when
        addressService.deleteAddress(TEST_ADDRESS_ID, TEST_PERSON_ID);
        verify(addressRepository, times(1)).findAllByIdAndPersonId(TEST_ADDRESS_ID, TEST_PERSON_ID);

    }

    @Test
    public void delete_address_expect_exception_when_address_does_not_exist() {

        //given
        given(addressRepository.findAllByIdAndPersonId(TEST_ADDRESS_ID, TEST_PERSON_ID)).willReturn(Optional.empty());

        //then
        Assertions.assertThrows(ObjectNotFound.class, () -> addressService.deleteAddress(TEST_ADDRESS_ID, TEST_PERSON_ID));
        verify(addressRepository, times(1)).findAllByIdAndPersonId(TEST_ADDRESS_ID, TEST_PERSON_ID);
    }
}