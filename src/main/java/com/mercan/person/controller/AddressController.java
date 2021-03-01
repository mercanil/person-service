package com.mercan.person.controller;

import com.mercan.person.entity.Address;
import com.mercan.person.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "Get addresses by person id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found addresses", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Address.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Person not found", content = @Content)})
    @GetMapping(value = "/{personId}/address", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Address>> getAddress(@PathVariable("personId") Long personId) {
        log.info("get address for person : {}", personId);
        List<Address> addresses = addressService.getAddress(personId);
        log.info("get address for person : {} , response:{}", personId, addresses);
        return ResponseEntity.ok(addresses);
    }


    @PostMapping(value = "/{personId}/address", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Address> createAddress(@PathVariable("personId") Long personId, @Valid @RequestBody Address address) {
        log.info("create address for person : {}", address);
        Address createdAddress = addressService.createAddress(personId, address);
        log.info("create address for person : {} response: {}", address, createdAddress);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
    }

    @PutMapping(value = "/{personId}/address/{addressId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Address> updateAddress(@PathVariable("personId") Long personId,
                                                 @PathVariable("addressId") Long addressId,
                                                 @Valid @RequestBody Address address
    ) {
        log.info("update address for personId : {} addressId : {} address : {}", personId, addressId, address);
        Address updatedAddress = addressService.updateAddress(personId, addressId, address);
        log.info("update address response : {}", updatedAddress);
        return ResponseEntity.ok(updatedAddress);
    }


    @DeleteMapping("/{personId}/address/{addressId}")
    public ResponseEntity deleteAddress(@PathVariable("personId") Long personId,
                                        @PathVariable("addressId") Long addressId
    ) {
        log.info("delete address for personId : {} addressId : {}", personId, addressId);
        addressService.deleteAddress(addressId, personId);
        return ResponseEntity.noContent().build();
    }
}
