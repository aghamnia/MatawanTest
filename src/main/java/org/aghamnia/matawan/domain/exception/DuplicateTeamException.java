package org.aghamnia.matawan.domain.exception;

/**
 * Exception for duplicate team case.
 */
public class DuplicateTeamException extends RuntimeException {
    public DuplicateTeamException(String message) {
        super(message);
    }
}
