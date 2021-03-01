package com.mercan.person.pojo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApiError {

    private String reasonCode;
    private List<String> errors;

}
