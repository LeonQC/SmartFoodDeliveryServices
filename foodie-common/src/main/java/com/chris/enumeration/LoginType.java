package com.chris.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(onMethod_=@__({@JsonValue}))
@RequiredArgsConstructor
public enum LoginType {
    PASSWORD("password"),
    OAUTH2("oauth2");

    private final String code;

    @JsonCreator
    public static LoginType fromString(String value) {
        if (value == null) {
            return null;
        }
        for (LoginType lt : LoginType.values()) {
            if (lt.code.equalsIgnoreCase(value.trim())) {
                return lt;
            }
        }
        return null;
    }
}
