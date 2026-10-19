package com.mycompany.gamezone.exceptions;

/** Indicates that otherwise valid input violates an operation's business rules. */
public class BusinessRuleException extends GameZoneException {
    /** @param message Spanish description of the violated business rule */
    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE");
    }
}
