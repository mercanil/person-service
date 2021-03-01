package com.mercan.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercan.person.entity.Address;
import com.mercan.person.entity.Person;

public class TestHelper {

    public static String asJsonString(ObjectMapper objectMapper, final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Person createTestPerson(String firstName , String lastName){
        return Person.builder().firstName(firstName).lastName(lastName).build();
    }

    public static Address createTestAddress(String city , String postalCode , String state , String street){
        return Address.builder().city(city).postalCode(postalCode).state(state).street(street).build();
    }

}
