package com.procurement.evaluation.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum Status {
    PENDING("pending"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    CONSIDERATION("consideration");

    private static final Map<String, Status> CONSTANTS = new HashMap<>();

    static {
        for (final Status c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    Status(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static Status fromValue(final String value) {
        final Status constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        }
        return constant;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

}
