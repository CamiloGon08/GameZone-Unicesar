package com.mycompany.gamezone.exceptions;

/** Preserves the technical cause and context of a persistence failure. */
public class PersistenceException extends GameZoneException {
    /**
     * @param message context including the file and attempted operation
     * @param cause original failure, retained for diagnosis
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, "PERSISTENCE_ERROR");
        initCause(cause);
    }
}
