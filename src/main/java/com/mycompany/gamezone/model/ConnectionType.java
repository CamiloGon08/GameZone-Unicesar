package com.mycompany.gamezone.model;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum ConnectionType {
    WIRELESS,
    WIRED;

    /** Case-insensitive, like the previous equalsIgnoreCase checks. */
    public static boolean isValid(String value) {
        for (ConnectionType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    /** "WIRELESS or WIRED" */
    public static String optionsDescription() {
        return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.joining(" or "));
    }
}