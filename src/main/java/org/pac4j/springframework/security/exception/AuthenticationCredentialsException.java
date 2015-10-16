package org.pac4j.springframework.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Expression used to wrap pac4j's exceptions into spring-security's exceptions
 */
public class AuthenticationCredentialsException extends AuthenticationException {

    public AuthenticationCredentialsException(String msg, Throwable t) {
        super(msg, t);
    }

    public AuthenticationCredentialsException(String msg) {
        super(msg);
    }
}
