package com.mycompany.gamezone.exceptions;

/** Base unchecked exception for failures reported by GameZone operations. */
public abstract class GameZoneException extends RuntimeException {
    private final String errorCode;

    /** @param message description of the failure */
    public GameZoneException(String message) {
        this(message, "GAMEZONE_ERROR");
    }

    /**
     * @param message description of the failure
     * @param errorCode stable category used independently of the message
     */
    public GameZoneException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /** @return the stable error category */
    public String getErrorCode() {
        return errorCode;
    }
}
