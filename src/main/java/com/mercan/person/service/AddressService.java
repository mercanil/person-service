package com.mercan.person.service;

import com.mercan.person.entity.Address;
import com.mercan.person.entity.Person;
import com.mercan.person.exception.ObjectNotFound;
import com.mercan.person.repository.AddressRepository;
import com.mercan.person.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {
    private final AddressRepository addressRepository;
    private final PersonRepository personRepository;

    public List<Address> getAddress(Long personId) {
        if (!personRepository.existsById(personId)) {
            log.error("person is not found for id {}", personId);
            throw new ObjectNotFound("personId ", personId);
        }
        return addressRepository.findAllByPersonId(personId);
    }

    public Address createAddress(Long personId, Address address) {
        Person person = personRepository.findById(personId).orElseThrow(() -> {
            log.error("person is not found for id {}", personId);
            return new ObjectNotFound("person ", personId);
        });

        address.setPerson(person);
        return addressRepository.save(address);
    }

    public Address updateAddress(Long personId, Long addressId, Address addressRequested) {

        if (!personRepository.existsById(personId)) {
            log.error("person is not found for id {}", personId);
            throw new ObjectNotFound("person ", personId);
        }
        return addressRepository.findById(addressId).map(address -> {
            address.setCity(addressRequested.getCity());
            address.setState(addressRequested.getState());
            address.setPostalCode(addressRequested.getPostalCode());
            address.setStreet(addressRequested.getStreet());
            return addressRepository.save(address);
        }).orElseThrow(() -> {
            log.error("address is not found for id {}", addressId);
            return new ObjectNotFound("address ", addressId);
        });

    }

    public void deleteAddress(Long addressId, Long personId) {
        Address address = addressRepository.findAllByIdAndPersonId(addressId, personId)
                .orElseThrow(() -> {
                    log.error("address is not found for addressId {} personId {}", addressId, personId);
                    return new ObjectNotFound("address", addressId);
                });

        addressRepository.delete(address);

    }
}