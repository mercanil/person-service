package com.mercan.person.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ObjectNotFound extends RuntimeException {

    private String collection;
    private Object id;
}
