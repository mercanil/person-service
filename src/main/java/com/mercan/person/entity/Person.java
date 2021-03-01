package com.mercan.person.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "is mandatory")
    @Size(max = 250)
    private String firstName;

    @NotBlank(message = "is mandatory")
    @Size(max = 250)
    private String lastName;

    @OneToMany(mappedBy = "person", fetch = FetchType.EAGER)
    private Set<Address> address;


    public Person() {
    }

    public Person(Long id, @NotBlank(message = "is mandatory") @Size(max = 250) String firstName, @NotBlank(message = "is mandatory") @Size(max = 250) String lastName, Set<Address> address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Address> getAddress() {
        return address;
    }

    public void setAddress(Set<Address> address) {
        this.address = address;
    }

}
