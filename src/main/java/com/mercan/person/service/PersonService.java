package com.mercan.person.service;

import com.mercan.person.entity.Person;
import com.mercan.person.exception.ObjectNotFound;
import com.mercan.person.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {
    private final PersonRepository personRepository;

    public List<Person> getPeople() {
        return personRepository.findAll();
    }

    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    public Person updatePerson(Long personId, Person personRequest) {

        return personRepository.findById(personId).map(storedPerson -> {
            storedPerson.setFirstName(personRequest.getFirstName());
            storedPerson.setLastName(personRequest.getLastName());
            storedPerson.setAddress(personRequest.getAddress());
            return personRepository.save(storedPerson);
        }).orElseThrow(() -> {
            log.error("person is not found for id {}", personId);
            return new ObjectNotFound("person", personId);
        });

    }

    public void delete(Long personId) {
        Person person = personRepository.findById(personId).orElseThrow(() -> {
            log.error("person is not found for id {}", personId);
            return new ObjectNotFound("person", personId);
        });
        personRepository.delete(person);
    }

    public Person getPerson(Long personId) {
        return personRepository.findById(personId).orElseThrow(() -> {
            log.error("person is not found for id {}", personId);
            return new ObjectNotFound("person", personId);
        });
    }

    public long getPersonCount() {
        return personRepository.count();
    }
}
