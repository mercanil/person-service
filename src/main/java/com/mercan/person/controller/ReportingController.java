package com.mercan.person.controller;


import com.mercan.person.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/report")
@RequiredArgsConstructor
@Slf4j
public class ReportingController {
    private final PersonService personService;

    @GetMapping("/person/count")
    public ResponseEntity count() {
        log.info("count person");
        return ResponseEntity.ok(personService.getPersonCount());
    }
}
