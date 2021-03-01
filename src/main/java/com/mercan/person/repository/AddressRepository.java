package com.mercan.person.repository;

import com.mercan.person.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findAllByPersonId(Long personId);

    Optional<Address> findAllByIdAndPersonId(Long addressId, Long personId);
}
