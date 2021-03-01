package com.mercan.person.service;

import com.mercan.person.entity.Person;
import com.mercan.person.exception.ObjectNotFound;
import com.mercan.person.repository.PersonRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.mercan.helper.TestHelper.createTestPerson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    private static final String TEST_FIRST_NAME = "test-first-name";
    private static final String TEST_LAST_NAME = "test-last-name";
    private static final Long TEST_ID = 1L;
    public static Person person;

    @Mock
    PersonRepository personRepository;

    @InjectMocks
    PersonService personService;

    @BeforeAll
    public static void setup() {
        person = createTestPerson(TEST_FIRST_NAME, TEST_LAST_NAME);
    }

    @Test
    public void create_person_expect_success() {

        //given
        given(personRepository.save(person)).willReturn(person);

        //when
        Person storedPerson = personService.createPerson(person);

        //then
        assertThat(storedPerson, is(storedPerson));
        verify(personRepository, times(1)).save(storedPerson);

    }

    @Test
    public void update_person_expect_success() {

        //given
        given(personRepository.findById(TEST_ID)).willReturn(Optional.of(person));
        given(personRepository.save(person)).willReturn(person);

        //when
        Person storedPerson = personService.updatePerson(TEST_ID, person);

        //then
        assertThat(storedPerson, is(person));
        verify(personRepository, times(1)).save(person);
    }


    @Test
    public void update_person_expect_exception_when_person_does_not_exist() {

        //given
        given(personRepository.findById(TEST_ID)).willReturn(Optional.empty());


        //then
        Assertions.assertThrows(ObjectNotFound.class, () -> personService.updatePerson(TEST_ID, person));
        verify(personRepository, times(1)).findById(TEST_ID);
        verify(personRepository, times(0)).save(person);

    }

    @Test
    public void delete_person_expect_success() {

        //given
        given(personRepository.findById(TEST_ID)).willReturn(Optional.of(person));
        doNothing().when(personRepository).delete(person);

        //when
        personService.delete(TEST_ID);

        //then
        verify(personRepository, times(1)).delete(person);

    }


    public void delete_person_expect_exception() {
        //given
        given(personRepository.findById(TEST_ID)).willReturn(Optional.empty());

        //then
        Assertions.assertThrows(ObjectNotFound.class, () -> personService.delete(TEST_ID));
        verify(personRepository, times(1)).delete(person);

    }

}