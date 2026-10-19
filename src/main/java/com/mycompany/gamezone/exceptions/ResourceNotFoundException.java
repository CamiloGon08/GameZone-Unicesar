package com.mycompany.gamezone.exceptions;

/** Indicates that an identifier does not refer to an existing resource. */
public class ResourceNotFoundException extends GameZoneException {
    /**
     * @param resourceType Spanish name of the missing resource
     * @param resourceId identifier that was requested
     */
    public ResourceNotFoundException(String resourceType, String resourceId) {
        super("El recurso " + resourceType + " con identificador " + resourceId
                + " no fue encontrado.", "RESOURCE_NOT_FOUND");
    }
}
