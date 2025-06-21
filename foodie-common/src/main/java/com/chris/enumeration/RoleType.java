package com.chris.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(onMethod_ = @__({@JsonValue}))
@RequiredArgsConstructor
public enum RoleType {
    MERCHANT("merchant"),
    CLIENT("client"),
    RIDER("rider");

    private final String code;

    @JsonCreator
    public static RoleType fromString(String value) {
        if (value == null) {
            return null;
        }
        for (RoleType rt : RoleType.values()) {
            if (rt.code.equalsIgnoreCase(value.trim())) {
                return rt;
            }
        }
        return null;
    }
}
