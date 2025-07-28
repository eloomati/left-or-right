// src/main/java/io/mhetko/lor/exception/UserAlreadyExistsException.java
package io.mhetko.lor.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}