package com.mercan.person.controller;

import com.mercan.person.entity.Person;
import com.mercan.person.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/person")
@RequiredArgsConstructor
@Slf4j
public class PersonController {

    private final PersonService personService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Person>> getPeople() {
        log.info("get people");
        List<Person> people = personService.getPeople();
        log.info("get people response : {}", people);
        return ResponseEntity.ok(people);
    }

    @GetMapping(value = "{personId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> getPerson(@PathVariable("personId") Long personId) {
        log.info("get person id : {}", personId);
        Person person = personService.getPerson(personId);
        log.info("get person response : {}", person);
        return ResponseEntity.ok(person);
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> createPerson(@Valid @RequestBody Person person) {
        log.info("create person : {}", person);
        Person createdPerson = personService.createPerson(person);
        log.info("create person response: {}", createdPerson);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPerson);

    }

    @PutMapping(value = "{personId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> updatePerson(@PathVariable("personId") Long personId, @Valid @RequestBody Person person) {
        log.info("update person Id: {} , person {}", personId, person);
        Person updatedPerson = personService.updatePerson(personId, person);
        log.info("update person response: {}", person);
        return ResponseEntity.ok(updatedPerson);

    }

    @DeleteMapping("{personId}")
    public ResponseEntity deletePerson(@PathVariable("personId") Long personId) {
        log.info("delete person: {}", personId);
        personService.delete(personId);
        return ResponseEntity.noContent().build();
    }

}
