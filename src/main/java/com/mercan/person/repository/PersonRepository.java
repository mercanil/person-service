package com.mercan.person.repository;

import com.mercan.person.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    @Override
    List<Person> findAll();
}
