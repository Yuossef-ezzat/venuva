package com.example.venuva.Core.Domain.Abstractions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Error {
    public static final Error NONE = new Error("");

    private final String message;

}

