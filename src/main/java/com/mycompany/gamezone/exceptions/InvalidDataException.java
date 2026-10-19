package com.mycompany.gamezone.exceptions;

/** Identifies an input field whose format or range is invalid. */
public class InvalidDataException extends GameZoneException {
    /**
     * @param fieldName Spanish label of the invalid field
     * @param problem Spanish description of the invalid value
     */
    public InvalidDataException(String fieldName, String problem) {
        super("El campo " + fieldName + " no es válido: " + problem, "INVALID_DATA");
    }
}
